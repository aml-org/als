package org.mulesoft.language.server.server.modules.astManager

import java.io.{PrintWriter, StringWriter}

import amf.core.client.ParserConfig

import scala.collection.mutable.Buffer
import scala.collection.mutable.ArrayBuffer
import amf.core.model.document.BaseUnit
import amf.core.unsafe.TrunkPlatform
import org.mulesoft.language.common.dtoTypes.{IChangedDocument, IOpenedDocument}
import org.mulesoft.language.server.common.reconciler.Reconciler
import org.mulesoft.language.server.core.{AbstractServerModule, IServerModule}
import org.mulesoft.language.server.core.connections.IServerConnection
import org.mulesoft.language.server.core.platform.ProxyContentPlatform
import org.mulesoft.language.server.modules.astManager.DocumentChangedRunnable
import org.mulesoft.language.server.server.modules.editorManager.IEditorManagerModule

import scala.collection.mutable
import scala.util.{Failure, Success, Try}
import scala.concurrent.{Future, Promise}
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

  private var reconciler: Reconciler = new Reconciler(connection, 500);
  
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
    val current = this.currentASTs.get(uri);
    
    if(current.isDefined) {
      Future.successful(current.get);
    } else {
      val editorOption = this.getEditorManager.getEditor(uri);
      
      if(editorOption.isDefined) {
        var promise = Promise[BaseUnit]();
  
        this.parse(uri).andThen {
          case Success(result) => {
            this.registerNewAST(uri, 0, result);
            
            promise.success(result);
          }
          
          case Failure(throwable) => promise.failure(throwable);
        }
        
        promise.future;
      } else {
        Future.failed(new Exception("No editor found for uri " + uri));
      }
    }

  }

  def onNewASTAvailable(listener: IASTListener, unsubscribe: Boolean = false): Unit = {

    this.addListener(this.astListeners, listener, unsubscribe)
  }

  def onOpenDocument(document: IOpenedDocument): Unit = {

    this.parse(document.uri).foreach(unit=>{
      this.registerNewAST(document.uri, document.version, unit)
    })

    //this.reconciler.schedule(new ParseDocumentRunnable(document.uri, 0, this.editorManager, this.connection, this.connection)).then((newAST => this.registerNewAST(document.uri, document.version, newAST)), (error => this.registerASTParseError(document.uri, error)))
  }

  def onChangeDocument(document: IChangedDocument): Unit = {
    this.connection.debug(s"document ${document.uri} is changed", "ASTManager", "onChangeDocument");
    
    reconciler.shedule(new DocumentChangedRunnable(document.uri, () => this.parse(this.platform.resolvePath(document.uri)))).future.map(unit=>{
      this.registerNewAST(document.uri, document.version, unit)
    }).recover{
      case e:Throwable => {
        val writer = new StringWriter()
        e.printStackTrace(new PrintWriter(writer))
        this.connection.debug(s"Failed to parse ${document.uri} with exception ${writer}", "ASTManager", "onChangeDocument")
      }
    };
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

  /**
    * Gets current AST if there is any.
    * If not, performs immediate asynchronous parsing and returns the results.
    * @param uri
    */
  def forceBuildNewAST(uri: String, text: String): Future[BaseUnit] = {

      this.parseWithContentSubstitution(uri, text).map(unit=>{

        unit
      })
  }

  def parse(uri: String): Future[BaseUnit] = {

    val language = if (uri.endsWith(".raml")) "RAML 1.0" else "OAS 2.0";

    var cfg = new ParserConfig(
      Some(ParserConfig.PARSE),
      Some(uri),
      Some(language),
      Some("application/yaml"),
      None,
      Some("AMF Graph"),
      Some("application/ld+json")
    )

    val startTime = System.currentTimeMillis()

    val helper = ParserHelper(this.platform)

    var promise = Promise[BaseUnit]();
  
    helper.parse(cfg, this.platform.defaultEnvironment).andThen {
      case Success(result) => {
        val endTime = System.currentTimeMillis();
  
        this.connection.debugDetail(s"It took ${endTime-startTime} milliseconds to build AMF ast", "ASTManager", "parse");
    
        promise.success(result);
      }
  
      case Failure(throwable) => promise.failure(throwable);
    }
  
    promise.future;
  }

  def parseWithContentSubstitution(uri: String, content: String): Future[BaseUnit] = {

    val proxyPlatform = new ProxyContentPlatform(this.platform,
      uri, content)

    val language = if (uri.endsWith(".raml")) "RAML 1.0" else "OAS 2.0";

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

    var promise = Promise[BaseUnit]();
  
    helper.parse(cfg, proxyPlatform.defaultEnvironment).andThen {
      case Success(result) => {
        val endTime = System.currentTimeMillis();
      
        this.connection.debugDetail(s"It took ${endTime-startTime} milliseconds to build AMF ast", "ASTManager", "parseWithContentSubstitution");
      
        promise.success(result);
      }
    
      case Failure(throwable) => promise.failure(throwable);
    }
  
    promise.future;
  }
}
