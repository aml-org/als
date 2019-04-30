package org.mulesoft.als.server.modules.ast

import java.io.{PrintWriter, StringWriter}

import amf.client.remote.Content
import amf.core.AMF
import amf.core.client.ParserConfig
import amf.core.lexer.CharSequenceStream
import amf.core.model.document.BaseUnit
import amf.internal.resource.ResourceLoader
import amf.plugins.document.vocabularies.AMLPlugin
import org.mulesoft.als.server.Initializable
import org.mulesoft.als.server.textsync.{ChangedDocument, OpenedDocument, TextDocument, TextDocumentManager}
import org.mulesoft.high.level.amfmanager.ParserHelper
import amf.plugins.document.webapi.validation.PayloadValidatorPlugin
import amf.plugins.document.webapi.{Oas20Plugin, Oas30Plugin, Raml08Plugin, Raml10Plugin}
import amf.plugins.features.validation.AMFValidatorPlugin
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.modules.common.reconciler.Reconciler
import org.mulesoft.als.server.platform.{ProxyContentPlatform, ServerPlatform}

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * AST manager
  */
class AstManager(private val textDocumentManager: TextDocumentManager,
                 private val platform: ServerPlatform,
                 private val logger: Logger)
    extends Initializable {

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
    //    this.currentASTs.get(uri) match {
    //      case Some(current) => Future.successful(current)
    //      case _ =>
    val editorOption = textDocumentManager.getTextDocument(uri)
    if (editorOption.isDefined) {
      init()
        .flatMap(_ => parse(uri, editorOption.map(loaderFromEditor)))
    } else {
      Future.failed(new Exception("No editor found for uri " + uri))
    }
    //    }
  }

  private def loaderFromEditor(textEditor: TextDocument) = {
    new ResourceLoader {
      override def fetch(resource: String): Future[Content] =
        Future(Content(new CharSequenceStream(resource, textEditor._buffer.text), resource))

      override def accepts(resource: String): Boolean = platform.resolvePath(textEditor.path) == resource
    }
  }

  def onNewASTAvailable(listener: AstListener, unsubscribe: Boolean = false): Unit = {
    this.addListener(this.astListeners, listener, unsubscribe)
  }

  def onOpenDocument(document: OpenedDocument): Unit =
    parse(document.uri)
      .foreach(unit => registerNewAST(document.uri, document.version, unit))

  def onChangeDocument(document: ChangedDocument): Unit = {
    logger.debug(s"document ${document.uri} is changed", "ASTManager", "onChangeDocument")
    val resolvedPath = platform.resolvePath(document.uri)
    val resourceLoader = document.text.map(t => {
      new ResourceLoader {
        override def fetch(resource: String): Future[Content] =
          Future(Content(new CharSequenceStream(t), resolvedPath))

        override def accepts(resource: String): Boolean = resource == resolvedPath
      }
    })
    reconciler
      .shedule(new DocumentChangedRunnable(document.uri, () => parse(resolvedPath, resourceLoader)))
      .future
      .map(unit => registerNewAST(document.uri, document.version, unit))
      .recover {
        case e: Throwable =>
          this.currentASTs.remove(document.uri)
          val writer = new StringWriter()
          e.printStackTrace(new PrintWriter(writer))
          logger.debug(s"Failed to parse ${document.uri} with exception $writer", "ASTManager", "onChangeDocument")
      }
  }

  def onCloseDocument(uri: String): Unit = {
    this.currentASTs.remove(uri)

  }

  def registerNewAST(uri: String, version: Int, ast: BaseUnit): Unit = {

    this.logger.debug("Registering new AST for URI: " + uri, "ASTManager", "registerNewAST")

    this.currentASTs(uri) = ast

    this.notifyASTChanged(uri, version, ast)
  }

  def notifyASTChanged(uri: String, version: Int, ast: BaseUnit) = {

    this.logger.debug("Got new AST parser results, notifying the listeners", "ASTManager", "notifyASTChanged")

    this.astListeners.foreach { listener =>
      listener.apply(uri, version, ast)
    }

  }

  def addListener[T](memberListeners: mutable.Set[T], listener: T, unsubscribe: Boolean = false): Unit =
    if (unsubscribe) memberListeners.remove(listener) else memberListeners.add(listener)

  /**
    * Gets current AST if there is any.
    * If not, performs immediate asynchronous parsing and returns the results.
    *
    * @param uri
    */
  def forceBuildNewAST(uri: String, text: String): Future[BaseUnit] =
    parseWithContentSubstitution(uri, text)

  def parse(uri: String, loaderOpt: Option[ResourceLoader] = None): Future[BaseUnit] = {
    val language = textDocumentManager.getTextDocument(uri).map(_.language).getOrElse("OAS 2.0")

    val protocolUri = platform.resolvePath(uri)
    logger.debugDetail(s"Protocol uri is $protocolUri", "ASTManager", "parse")

    val defaultEnvironment = platform.defaultEnvironment.add(platform.fileLoader)
    val environment = loaderOpt
      .map(defaultEnvironment.add)
      .getOrElse(defaultEnvironment)

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

    helper.parse(config, environment).map { result =>
      val endTime = System.currentTimeMillis()
      logger.debugDetail(s"It took ${endTime - startTime} milliseconds to build AMF ast", "ASTManager", "parse")
      result
    }
  }

  def parseWithContentSubstitution(uri: String, content: String): Future[BaseUnit] = {
    val proxyPlatform = new ProxyContentPlatform(platform, logger, uri, content)

    val language = textDocumentManager.getTextDocument(uri).map(_.language).getOrElse("OAS 2.0")

    val cfg = new ParserConfig(
      Some(ParserConfig.PARSE),
      Some(uri),
      Some(language),
      Some("application/yaml"),
      None,
      Some("AMF Graph"),
      Some("application/ld+json")
    )

    val startTime = System.currentTimeMillis()

    val helper = ParserHelper(proxyPlatform)

    helper.parse(cfg, proxyPlatform.defaultEnvironment).map { r =>
      val endTime = System.currentTimeMillis()
      this.logger.debugDetail(s"It took ${endTime - startTime} milliseconds to build AMF ast",
                              "ASTManager",
                              "parseWithContentSubstitution")
      r
    }
  }
}
