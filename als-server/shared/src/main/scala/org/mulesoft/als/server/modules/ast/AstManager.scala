package org.mulesoft.als.server.modules.ast

import java.io.{PrintWriter, StringWriter}
import java.util.UUID

import amf.client.remote.Content
import amf.core.AMF
import amf.core.client.ParserConfig
import amf.core.model.document.BaseUnit
import amf.core.remote.Platform
import amf.internal.environment.Environment
import amf.internal.resource.ResourceLoader
import amf.plugins.document.vocabularies.AMLPlugin
import amf.plugins.document.webapi.validation.PayloadValidatorPlugin
import amf.plugins.document.webapi.{Oas20Plugin, Oas30Plugin, Raml08Plugin, Raml10Plugin}
import amf.plugins.features.validation.AMFValidatorPlugin
import org.mulesoft.als.common.{EnvironmentPatcher, FileUtils}
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.modules.common.reconciler.Reconciler
import org.mulesoft.als.server.textsync.{ChangedDocument, OpenedDocument, TextDocumentManager}
import org.mulesoft.amfmanager.ParserHelper
import org.mulesoft.lsp.Initializable
import org.mulesoft.lsp.feature.telemetry.{MessageTypes, TelemetryProvider}

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * AST manager
  */
class AstManager(private val textDocumentManager: TextDocumentManager,
                 private val baseEnvironment: Environment,
                 private val telemetryProvider: TelemetryProvider,
                 private val platform: Platform,
                 private val logger: Logger)
    extends Initializable {

  /**
    * Current AST listeners
    */
  var astListeners: mutable.Set[AstListener] = mutable.Set()

  /**
    * Map from uri to AST
    */
  val currentASTs: mutable.Map[String, BaseUnit] = mutable.HashMap()

  val fileDependencies = new FileDependencies()

  private var initialized: Option[Future[Unit]] = None

  private val reconciler: Reconciler = new Reconciler(logger, 500)

  override def initialize(): Future[Unit] =
    amfInit()
      .map(_ => {
        textDocumentManager.onOpenedDocument(this.onOpenDocument)
        textDocumentManager.onChangeDocument(this.onChangeDocument)
        textDocumentManager.onClosedDocument(this.onCloseDocument)
        textDocumentManager.onIndexDialect(this.onIndexDialect)
      })

  def init(): Future[Unit] = {
    initialized match {
      case Some(f) => f
      case _ =>
        val f = amfInit()
        initialized = Some(f)
        f
    }
  }

  def amfInit(): Future[Unit] = {
    val telemetryUUID = UUID.randomUUID().toString
    telemetryProvider.addTimedMessage("Initialize AMF begin", MessageTypes.BEGIN_AMF_INIT, "", telemetryUUID)
    amf.core.AMF.registerPlugin(AMLPlugin)
    amf.core.AMF.registerPlugin(Raml10Plugin)
    amf.core.AMF.registerPlugin(Raml08Plugin)
    amf.core.AMF.registerPlugin(Oas20Plugin)
    amf.core.AMF.registerPlugin(Oas30Plugin)
    amf.core.AMF.registerPlugin(AMFValidatorPlugin)
    amf.core.AMF.registerPlugin(PayloadValidatorPlugin)
    AMF
      .init()
      .map(i => {
        telemetryProvider.addTimedMessage("Initialize AMF end", MessageTypes.END_AMF_INIT, "", telemetryUUID)
        i
      })
  }

  def getCurrentAST(uri: String, uuid: String): Future[BaseUnit] =
    this.getCurrentAST(uri).map(Future.successful).getOrElse(forceGetCurrentAST(uri, uuid))

  private def getCurrentAST(uri: String): Option[BaseUnit] =
    this.currentASTs.get(uri)

  def forceGetCurrentAST(uri: String, uuid: String): Future[BaseUnit] = {
    val editorOption = textDocumentManager.getTextDocument(uri)
    if (editorOption.isDefined) {
      init()
        .flatMap(_ => {
          parse(uri, telemetryProvider, uuid)
        })
    } else {
      Future.failed(new Exception("No editor found for uri " + uri))
    }
  }

  def onNewASTAvailable(listener: AstListener, unsubscribe: Boolean = false): Unit =
    addListener(astListeners, listener, unsubscribe)

  def onOpenDocument(document: OpenedDocument): Unit = {
    val telemetryUUID: String = UUID.randomUUID().toString
    telemetryProvider.addTimedMessage("open document", MessageTypes.CHANGE_DOCUMENT, document.uri, telemetryUUID)
    parse(document.uri, telemetryProvider, telemetryUUID)
      .foreach(unit => {
        registerNewAST(document.uri, document.version, unit, telemetryUUID)
      })
  }

  def onIndexDialect(uri: String, content: Option[String]): Unit = {
    val telemetryUUID: String = UUID.randomUUID().toString
    logger.debug(s"Dialect $uri is called to index", "ASTManager", "indexDialect")
    telemetryProvider.addTimedMessage("dialect indexed", MessageTypes.INDEX_DIALECT, uri, telemetryUUID)

    ParserHelper(platform)
      .indexDialect(uri, content)
      .foreach(_ => {
        logger.debug(s"Dialect $uri has been indexed", "ASTManager", "indexDialect")
      })
  }

  def onChangeDocument(document: ChangedDocument): Unit = {
    val telemetryUUID: String = UUID.randomUUID().toString
    logger.debug(s"document ${document.uri} is changed", "ASTManager", "onChangeDocument")
    telemetryProvider.addTimedMessage("change document", MessageTypes.CHANGE_DOCUMENT, document.uri, telemetryUUID)
    reconciler
      .shedule(new DocumentChangedRunnable(document.uri, () => parse(document.uri, telemetryProvider, telemetryUUID)))
      .future
      .map(unit => registerNewAST(document.uri, document.version, unit, telemetryUUID))
      .recover {
        case e: Throwable =>
          currentASTs.remove(document.uri)
          val writer = new StringWriter()
          e.printStackTrace(new PrintWriter(writer))
          logger.debug(s"Failed to parse ${document.uri} with exception $writer", "ASTManager", "onChangeDocument")
      }
  }

  def onCloseDocument(uri: String): Unit =
    currentASTs.remove(uri)

  def registerNewAST(uri: String, version: Int, ast: BaseUnit, uuid: String): Unit = {

    logger.debug("Registering new AST for URI: " + uri, "ASTManager", "registerNewAST")

    currentASTs(uri) = ast

    notifyASTChanged(uri, version, ast, uuid)
  }

  def notifyASTChanged(uri: String, version: Int, ast: BaseUnit, uuid: String): Unit = {

    logger.debug("Got new AST parser results, notifying the listeners", "ASTManager", "notifyASTChanged")

    astListeners.foreach { listener =>
      listener.apply(uri, version, ast, uuid)
    }

  }

  def addListener[T](memberListeners: mutable.Set[T], listener: T, unsubscribe: Boolean = false): Unit =
    if (unsubscribe) memberListeners.remove(listener)
    else memberListeners.add(listener)

  /**
    * Gets current AST if there is any.
    * If not, performs immediate asynchronous parsing and returns the results.
    *
    */
  def forceBuildNewAST(uri: String,
                       text: String,
                       telemetryProvider: TelemetryProvider,
                       uuid: String): Future[BaseUnit] =
    parseWithContentSubstitution(uri, text, telemetryProvider, uuid)

  def parse(uri: String, telemetryProvider: TelemetryProvider, uuid: String): Future[BaseUnit] = {
    telemetryProvider.addTimedMessage("Begin parsing", MessageTypes.BEGIN_PARSE, uri, uuid)
    val amfURI = FileUtils.getDecodedUri(uri, platform)

    logger.debugDetail(s"Protocol uri is $amfURI", "ASTManager", "parse")
    val startTime = System.currentTimeMillis()

    val helper = ParserHelper(platform)

    helper.parse(amfURI, envForValidation(uri)).map { result =>
      val endTime = System.currentTimeMillis()
      logger.debugDetail(s"It took ${endTime - startTime} milliseconds to build AMF ast", "ASTManager", "parse")
      telemetryProvider.addTimedMessage("End parsing", MessageTypes.END_PARSE, uri, uuid)
      result
    }
  }

  def parseWithContentSubstitution(uri: String,
                                   content: String,
                                   telemetryProvider: TelemetryProvider,
                                   uuid: String): Future[BaseUnit] = {
    telemetryProvider.addTimedMessage("Begin patching", MessageTypes.BEGIN_PATCHING, uri, uuid)

    val patchedEnvironment =
      EnvironmentPatcher.patch(serverEnvironment, FileUtils.getEncodedUri(uri, platform), content)

    telemetryProvider.addTimedMessage("End patching", MessageTypes.END_PATCHING, uri, uuid)
    telemetryProvider.addTimedMessage("Begin parsing", MessageTypes.BEGIN_PARSE_PATCHED, uri, uuid)

    val startTime = System.currentTimeMillis()

    ParserHelper(platform)
      .parse(FileUtils.getDecodedUri(uri, platform), patchedEnvironment)
      .map { result =>
        val endTime = System.currentTimeMillis()
        logger.debugDetail(s"It took ${endTime - startTime} milliseconds to build AMF ast",
                           "ASTManager",
                           "parseWithContentSubstitution")
        telemetryProvider.addTimedMessage("End parsing", MessageTypes.END_PARSE_PATCHED, uri, uuid)
        result
      }
  }

  private val serverEnvironment = Environment()
    .withLoaders(TextDocumentLoader(textDocumentManager) +: baseEnvironment.loaders)

  private def envForValidation(root: String) = {
    val wrapperLoader = new ResourceLoader {
      override def fetch(resource: String): Future[Content] = {
        platform
          .resolve(resource, serverEnvironment)
          .map(c => {
            fileDependencies.addDependency(root, resource)
            c
          })
      }

      override def accepts(resource: String): Boolean = serverEnvironment.loaders.exists(_.accepts(resource))
    }

    Environment()
      .withLoaders(Seq(wrapperLoader))
  }
}

class FileDependencies() {
  private val map: mutable.Map[String, Set[String]] = mutable.Map()

  def addDependency(root: String, dependency: String): Unit = {
    if (FileUtils.getWithProtocol(root) != dependency)
      map.get(root) match {
        case Some(set: Set[String]) => map.update(root, set + dependency)
        case _                      => map.put(root, Set(dependency))
      }
  }

  def dependenciesFor(root: String): Set[String] = map.getOrElse(root, Set.empty)
}
