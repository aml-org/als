package org.mulesoft.als.server.modules.ast

import java.io.{PrintWriter, StringWriter}

import amf.core.AMF
import amf.core.client.ParserConfig
import amf.core.model.document.BaseUnit
import amf.core.remote.Platform
import amf.internal.environment.Environment
import amf.plugins.document.vocabularies.AMLPlugin
import amf.plugins.document.webapi.validation.PayloadValidatorPlugin
import amf.plugins.document.webapi.{Oas20Plugin, Oas30Plugin, Raml08Plugin, Raml10Plugin}
import amf.plugins.features.validation.AMFValidatorPlugin
import org.mulesoft.als.common.EnvironmentPatcher
import org.mulesoft.als.server.Initializable
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.modules.common.reconciler.Reconciler
import org.mulesoft.als.server.textsync.{ChangedDocument, OpenedDocument, TextDocumentManager}
import org.mulesoft.high.level.amfmanager.ParserHelper

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * AST manager
  */
class AstManager(private val textDocumentManager: TextDocumentManager,
                 private val baseEnvironment: Environment,
                 private val platform: Platform,
                 private val logger: Logger)
    extends Initializable {

  private val serverEnvironment = Environment()
    .withLoaders(TextDocumentLoader(textDocumentManager) +: baseEnvironment.loaders)

  /**
    * Current AST listeners
    */
  var astListeners: mutable.Set[AstListener] = mutable.Set()

  /**
    * Map from uri to AST
    */
  var currentASTs: mutable.Map[String, BaseUnit] = mutable.HashMap()

  private var initialized: Option[Future[Unit]] = None

  private val reconciler: Reconciler = new Reconciler(logger, 500)

  override def initialize(): Future[Unit] =
    amfInit()
      .map(_ => {
        textDocumentManager.onOpenedDocument(this.onOpenDocument)
        textDocumentManager.onChangeDocument(this.onChangeDocument)
        textDocumentManager.onClosedDocument(this.onCloseDocument)
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
    amf.core.AMF.registerPlugin(AMLPlugin)
    amf.core.AMF.registerPlugin(Raml10Plugin)
    amf.core.AMF.registerPlugin(Raml08Plugin)
    amf.core.AMF.registerPlugin(Oas20Plugin)
    amf.core.AMF.registerPlugin(Oas30Plugin)
    amf.core.AMF.registerPlugin(AMFValidatorPlugin)
    amf.core.AMF.registerPlugin(PayloadValidatorPlugin)
    AMF.init()
  }

  def getCurrentAST(uri: String): Option[BaseUnit] = None // this.currentASTs.get(uri)

  def forceGetCurrentAST(uri: String): Future[BaseUnit] = {
    val editorOption = textDocumentManager.getTextDocument(uri)
    if (editorOption.isDefined) {
      init()
        .flatMap(_ => parse(uri))
    } else {
      Future.failed(new Exception("No editor found for uri " + uri))
    }
  }

  def onNewASTAvailable(listener: AstListener, unsubscribe: Boolean = false): Unit =
    addListener(astListeners, listener, unsubscribe)

  def onOpenDocument(document: OpenedDocument): Unit =
    parse(document.uri)
      .foreach(unit => registerNewAST(document.uri, document.version, unit))

  def onChangeDocument(document: ChangedDocument): Unit = {
    logger.debug(s"document ${document.uri} is changed", "ASTManager", "onChangeDocument")

    reconciler
      .shedule(new DocumentChangedRunnable(document.uri, () => parse(document.uri)))
      .future
      .map(unit => registerNewAST(document.uri, document.version, unit))
      .recover {
        case e: Throwable =>
          currentASTs.remove(document.uri)
          val writer = new StringWriter()
          e.printStackTrace(new PrintWriter(writer))
          logger.debug(s"Failed to parse ${document.uri} with exception $writer", "ASTManager", "onChangeDocument")
      }
  }

  def onCloseDocument(uri: String): Unit = {
    currentASTs.remove(uri)

  }

  def registerNewAST(uri: String, version: Int, ast: BaseUnit): Unit = {

    logger.debug("Registering new AST for URI: " + uri, "ASTManager", "registerNewAST")

    currentASTs(uri) = ast

    notifyASTChanged(uri, version, ast)
  }

  def notifyASTChanged(uri: String, version: Int, ast: BaseUnit): Unit = {

    logger.debug("Got new AST parser results, notifying the listeners", "ASTManager", "notifyASTChanged")

    astListeners.foreach { listener =>
      listener.apply(uri, version, ast)
    }

  }

  def addListener[T](memberListeners: mutable.Set[T], listener: T, unsubscribe: Boolean = false): Unit =
    if (unsubscribe) memberListeners.remove(listener) else memberListeners.add(listener)

  /**
    * Gets current AST if there is any.
    * If not, performs immediate asynchronous parsing and returns the results.
    *
    */
  def forceBuildNewAST(uri: String, text: String): Future[BaseUnit] =
    parseWithContentSubstitution(uri, text)

  def parse(uri: String): Future[BaseUnit] = {
    val protocolUri = platform.decodeURI(platform.resolvePath(platform.encodeURI(uri)))
    val language    = textDocumentManager.getTextDocument(protocolUri).map(_.language).getOrElse("OAS 2.0")

    logger.debugDetail(s"Protocol uri is $protocolUri", "ASTManager", "parse")

    val config = new ParserConfig(
      Some(ParserConfig.PARSE),
      Some(protocolUri),
      Some(language),
      Some("application/yaml"),
      None,
      Some("AMF Graph"),
      Some("application/ld+json")
    )

    val startTime = System.currentTimeMillis()

    val helper = ParserHelper(platform)

    helper.parse(config, serverEnvironment).map { result =>
      val endTime = System.currentTimeMillis()
      logger.debugDetail(s"It took ${endTime - startTime} milliseconds to build AMF ast", "ASTManager", "parse")
      result
    }
  }

  def parseWithContentSubstitution(uri: String, content: String): Future[BaseUnit] = {
    val protocolUri = platform.decodeURI(platform.resolvePath(platform.encodeURI(uri)))

    val patchedEnvironment = EnvironmentPatcher.patch(serverEnvironment, uri, content)

    val language = textDocumentManager.getTextDocument(protocolUri).map(_.language).getOrElse("OAS 2.0")

    val cfg = new ParserConfig(
      Some(ParserConfig.PARSE),
      Some(protocolUri),
      Some(language),
      Some("application/yaml"),
      None,
      Some("AMF Graph"),
      Some("application/ld+json")
    )

    val startTime = System.currentTimeMillis()

    ParserHelper(platform)
      .parse(cfg, patchedEnvironment)
      .map { result =>
        val endTime = System.currentTimeMillis()
        logger.debugDetail(s"It took ${endTime - startTime} milliseconds to build AMF ast",
                           "ASTManager",
                           "parseWithContentSubstitution")
        result
      }
  }
}
