package org.mulesoft.language.server.server.modules.astManager

import amf.core.client.ParserConfig

import scala.collection.mutable.Buffer
import scala.collection.mutable.ArrayBuffer
import amf.core.model.document.BaseUnit
import amf.core.unsafe.TrunkPlatform
import org.mulesoft.language.common.dtoTypes.{IChangedDocument, IOpenedDocument}
import org.mulesoft.language.server.core.{AbstractServerModule, IServerModule}
import org.mulesoft.language.server.core.connections.IServerConnection
import org.mulesoft.language.server.server.modules.editorManager.IEditorManagerModule

import scala.collection.mutable
import scala.concurrent.Future
import scala.util.{Success, Try}
import scala.concurrent.ExecutionContext.Implicits.global


/**
  * AST manager
  */
class ASTManager extends AbstractServerModule with IASTManagerModule {

  val moduleDependencies: Array[String] = Array(IEditorManagerModule.moduleId)

  /**
    * Current AST listeners
    */
  var astListeners: Buffer[IASTListener] = ArrayBuffer()

  /**
    * Map from uri to AST
    */
  var currentASTs: mutable.Map[String, BaseUnit] = mutable.HashMap()

  //TODO add when reconciler is ready
  //var reconciler: Reconciler = _

//  def this(connection: IServerConnection, editorManager: IEditorManagerModule) = {
//    (this.reconciler = new Reconciler(connection, 250))
//
//  }

  protected def getEditorManager: IEditorManagerModule = {
    this.getDependencyById(IEditorManagerModule.moduleId).get
  }

  override def launch(): Try[IServerModule] = {

    val superLaunch = super.launch()

    if (superLaunch.isSuccess) {

      this.connection.onOpenDocument(this.onOpenDocument _)

      this.getEditorManager.onChangeDocument(this.onChangeDocument _)

      this.connection.onCloseDocument(this.onCloseDocument _)

      Success(this)
    } else {

      superLaunch
    }
  }

  override def stop(): Unit = {

    super.stop()

    this.connection.onOpenDocument(this.onOpenDocument _, true)
    this.getEditorManager.onChangeDocument(this.onChangeDocument _, true)
    this.connection.onCloseDocument(this.onCloseDocument _, true)
  }

  def getCurrentAST(uri: String): Option[BaseUnit] = {

    this.currentASTs.get(uri)
  }

  def forceGetCurrentAST(uri: String): Future[BaseUnit] = {

    // TODO use runnable

    val current = this.currentASTs.get(uri)

    if (current.isDefined) {

      Future.successful(current.get)
    } else {

      val editorOption = this.getEditorManager.getEditor(uri)
      if (editorOption.isDefined) {

        this.parse(uri, editorOption.get.text).map(unit=>{

          this.registerNewAST(uri, 0, unit)

          unit
        })

      } else {
        Future.failed(new Exception("No editor found for uri " + uri))
      }
    }

//    val current = this.currentASTs(uri)
//    if (current) {
//      return Promise.resolve(current)
//
//    }
//    val runner = new ParseDocumentRunnable(uri, null, this.editorManager, this.connection, this.connection)
//    val newASTPromise = runner.run()
//    if ((!newASTPromise)) {
//      return null
//
//    }
//    return newASTPromise.then((newAST => {
//      var version = null
//      val editor = this.editorManager.getEditor(uri)
//      if (editor) {
//        (version = editor.getVersion())
//
//      }
//      this.registerNewAST(uri, version, newAST)
//      return newAST
//
//    }))

  }

  def onNewASTAvailable(listener: IASTListener, unsubscribe: Boolean = false): Unit = {

    this.addListener(this.astListeners, listener, unsubscribe)
  }

  def onOpenDocument(document: IOpenedDocument): Unit = {

    this.parse(document.uri, document.text).foreach(unit=>{
      this.registerNewAST(document.uri, document.version, unit)
    })

    //this.reconciler.schedule(new ParseDocumentRunnable(document.uri, 0, this.editorManager, this.connection, this.connection)).then((newAST => this.registerNewAST(document.uri, document.version, newAST)), (error => this.registerASTParseError(document.uri, error)))
  }

  def onChangeDocument(document: IChangedDocument): Unit = {
    this.connection.debug(" document is changed", "ASTManager", "onChangeDocument")

    this.parse(document.uri, document.text.get).foreach(unit=>{
      this.registerNewAST(document.uri, document.version, unit)
    })
//    this.reconciler.schedule(new ParseDocumentRunnable(document.uri, document.version, this.editorManager, this.connection, this.connection)).then((newAST => {
//      this.connection.debugDetail("On change document handler promise returned new ast", "ASTManager", "onChangeDocument")
//      this.registerNewAST(document.uri, document.version, newAST)
//
//    }), (error => {
//      this.connection.debugDetail("On change document handler promise returned new ast error", "ASTManager", "onChangeDocument")
//      this.registerASTParseError(document.uri, error)
//
//    }))

  }

  def onCloseDocument(uri: String): Unit = {
    this.currentASTs.remove(uri)

  }

  def registerNewAST(uri: String, version: Int, ast: BaseUnit): Unit = {

    this.connection.debug("Registering new AST for URI: " + uri,
      "ASTManager", "registerNewAST")

    this.currentASTs(uri) = ast

    this.notifyASTChanged(uri, version, ast)
  }

//  def registerASTParseError(uri: String, error: Any) = {
//    (this.currentASTs = Map(
//    ))
//    this.notifyASTChanged(uri, null, error)
//
//  }

  def notifyASTChanged(uri: String, version: Int, ast: BaseUnit) = {

    this.connection.debug("Got new AST parser results, notifying the listeners",
      "ASTManager", "notifyASTChanged")

    this.astListeners.foreach { listener =>

      listener.apply(uri, version, ast)
    }

  }

  def addListener[T](memberListeners: Buffer[T], listener: T, unsubscribe: Boolean = false): Unit = {

    if (unsubscribe) {

      val index = memberListeners.indexOf(listener)
      if (index != -1) {
        memberListeners.remove(index)
      }

    }
    else {

      memberListeners += listener

    }

  }

  def parse(uri: String, content: String): Future[BaseUnit] = {

    //TODO move to runnable and handle external file contents
    val platform = TrunkPlatform(content)

    val language = if (uri.endsWith(".raml")) "RAML 1.0" else "OAS 2.0";

    var cfg = new ParserConfig(
      Some(ParserConfig.PARSE),
      Some("api.raml"),
      Some(language),
      Some("application/yaml"),
      None,
      Some("AMF Graph"),
      Some("application/ld+json")
    )

    val helper = ParserHelper(platform)

    helper.parse(cfg)
  }
}
