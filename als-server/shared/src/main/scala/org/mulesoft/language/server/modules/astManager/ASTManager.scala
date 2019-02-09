package org.mulesoft.language.server.modules.astManager

import java.io.{PrintWriter, StringWriter}

import amf.client.remote.Content
import amf.core.AMF
import amf.core.client.ParserConfig
import amf.core.lexer.CharSequenceStream
import amf.core.model.document.BaseUnit
import amf.internal.resource.ResourceLoader
import amf.plugins.document.vocabularies.AMLPlugin
import amf.plugins.document.webapi.validation.PayloadValidatorPlugin
import amf.plugins.document.webapi.{Oas20Plugin, Oas30Plugin, Raml08Plugin, Raml10Plugin}
import amf.plugins.features.validation.AMFValidatorPlugin
import org.mulesoft.high.level.amfmanager.ParserHelper
import org.mulesoft.language.common.dtoTypes.{ChangedDocument, OpenedDocument}
import org.mulesoft.language.server.common.reconciler.Reconciler
import org.mulesoft.language.server.core.AbstractServerModule
import org.mulesoft.language.server.core.platform.ProxyContentPlatform
import org.mulesoft.language.server.modules.editorManager.{EditorManagerModule, TextEditorInfo}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * AST manager
  */
class ASTManager extends AbstractServerModule with ASTManagerModule {

  val moduleDependencies: Array[String] = Array(EditorManagerModule.moduleId)

  /**
    * Current AST listeners
    */
  var astListeners: mutable.Buffer[ASTListener] = ArrayBuffer()

  /**
    * Map from uri to AST
    */
  var currentASTs: mutable.Map[String, BaseUnit] = mutable.HashMap()

  private var initialized: Boolean = false

  private val reconciler: Reconciler = new Reconciler(connection, 500)

  protected def getEditorManager: EditorManagerModule = {
    this.getDependencyById(EditorManagerModule.moduleId).get
  }

  override def launch(): Future[Unit] =
    super.launch()
      .flatMap(_ => {
        this.connection.onOpenDocument(this.onOpenDocument)

        this.getEditorManager.onChangeDocument(this.onChangeDocument)

        this.connection.onCloseDocument(this.onCloseDocument)

        amfInit()
      })

  def init(): Future[Unit] = {
    if (initialized) Future.successful(Unit)
    else {
      amfInit().map(_ => {
        initialized = true
        Unit
      })
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

  override def stop(): Unit = {

    super.stop()

    this.connection.onOpenDocument(this.onOpenDocument, true)
    this.getEditorManager.onChangeDocument(this.onChangeDocument, true)
    this.connection.onCloseDocument(this.onCloseDocument, true)
  }

  def getCurrentAST(uri: String): Option[BaseUnit] = {

    this.currentASTs.get(uri)
  }

  def forceGetCurrentAST(uri: String): Future[BaseUnit] = {
    this.currentASTs.get(uri) match {
      case Some(current) => Future.successful(current)
      case _ =>
        val editorOption = this.getEditorManager.getEditor(uri)

        if (editorOption.isDefined) {

          this
            .init()
            .flatMap(_ => {
              this.parse(uri, editorOption.map(loaderFromEditor)).map { result =>
                this.registerNewAST(uri, 0, result)
                result
              }
            })
        } else {
          Future.failed(new Exception("No editor found for uri " + uri))
        }
    }

  }

  private def loaderFromEditor(textEditor: TextEditorInfo) = {
    new ResourceLoader {
      override def fetch(resource: String): Future[Content] =
        Future(Content(new CharSequenceStream(resource, textEditor._buffer.text), resource))

      override def accepts(resource: String): Boolean = platform.resolvePath(textEditor.path) == resource
    }
  }

  def onNewASTAvailable(listener: ASTListener, unsubscribe: Boolean = false): Unit = {

    this.addListener(this.astListeners, listener, unsubscribe)
  }

  def onOpenDocument(document: OpenedDocument): Unit =
    parse(document.uri)
      .foreach(unit => registerNewAST(document.uri, document.version, unit))

  def onChangeDocument(document: ChangedDocument): Unit = {
    connection.debug(s"document ${document.uri} is changed", "ASTManager", "onChangeDocument")

    reconciler
      .shedule(new DocumentChangedRunnable(document.uri, () => parse(platform.resolvePath(document.uri))))
      .future
      .map(unit => registerNewAST(document.uri, document.version, unit))
      .recover {
        case e: Throwable =>
          val writer = new StringWriter()
          e.printStackTrace(new PrintWriter(writer))
          connection
            .debug(s"Failed to parse ${document.uri} with exception $writer", "ASTManager", "onChangeDocument")
      }
  }

  def onCloseDocument(uri: String): Unit = {
    this.currentASTs.remove(uri)

  }

  def registerNewAST(uri: String, version: Int, ast: BaseUnit): Unit = {

    this.connection.debug("Registering new AST for URI: " + uri, "ASTManager", "registerNewAST")

    this.currentASTs(uri) = ast

    this.notifyASTChanged(uri, version, ast)
  }

  def notifyASTChanged(uri: String, version: Int, ast: BaseUnit) = {

    this.connection.debug("Got new AST parser results, notifying the listeners", "ASTManager", "notifyASTChanged")

    this.astListeners.foreach { listener =>
      listener.apply(uri, version, ast)
    }

  }

  def addListener[T](memberListeners: mutable.Buffer[T], listener: T, unsubscribe: Boolean = false): Unit = {

    if (unsubscribe) {

      val index = memberListeners.indexOf(listener)
      if (index != -1) {
        memberListeners.remove(index)
      }

    } else {

      memberListeners += listener

    }

  }

  /**
    * Gets current AST if there is any.
    * If not, performs immediate asynchronous parsing and returns the results.
    *
    * @param uri
    */
  def forceBuildNewAST(uri: String, text: String): Future[BaseUnit] =
    parseWithContentSubstitution(uri, text)

  def parse(uri: String, loaderOpt: Option[ResourceLoader] = None): Future[BaseUnit] = {
    val language = getEditorManager.getEditor(uri).map(_.language).getOrElse("OAS 2.0")

    val protocolUri = platform.resolvePath(uri)
    connection.debugDetail(s"Protocol uri is $protocolUri", "ASTManager", "parse")

    val defaultEnvironment = platform.defaultEnvironment.add(platform.fileLoader).add(platform.httpLoader)
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

    val helper = ParserHelper(this.platform)

    helper.parse(config, environment).map { result =>
      val endTime = System.currentTimeMillis()
      connection
        .debugDetail(s"It took ${endTime - startTime} milliseconds to build AMF ast", "ASTManager", "parse")
      result
    }
  }

  def parseWithContentSubstitution(uri: String, content: String): Future[BaseUnit] = {
    val proxyPlatform = new ProxyContentPlatform(this.platform, uri, content)

    val language = getEditorManager.getEditor(uri).map(_.language).getOrElse("OAS 2.0")

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
      this.connection.debugDetail(s"It took ${endTime - startTime} milliseconds to build AMF ast",
        "ASTManager",
        "parseWithContentSubstitution")
      r
    }
  }
}
