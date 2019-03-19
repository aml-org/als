package org.mulesoft.language.server.modules.hlastManager

import amf.core.model.document.BaseUnit
import org.mulesoft.high.level.Core
import org.mulesoft.high.level.interfaces.IProject
import org.mulesoft.language.server.common.utils.PathRefine
import org.mulesoft.language.server.core.AbstractServerModule
import org.mulesoft.language.server.modules.astManager.{ASTListener, ASTManagerModule}
import org.mulesoft.language.server.modules.editorManager.EditorManagerModule

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class HlAstManager extends AbstractServerModule with IHLASTManagerModule {
  private var initialized: Boolean = false

  val moduleDependencies: Array[String] = Array(EditorManagerModule.moduleId, ASTManagerModule.moduleId)

  var astListeners: mutable.Buffer[IHLASTListener] = ArrayBuffer()

  var currentASTs: mutable.Map[String, IProject] = mutable.HashMap()

  val onNewASTAvailableListener: ASTListener = (uri: String, version: Int, ast: BaseUnit) => {
    HlAstManager.this.newASTAvailable(uri, version, ast)
  }

  protected def getEditorManager: EditorManagerModule = {

    this.getDependencyById(EditorManagerModule.moduleId).get
  }

  protected def getASTManager: ASTManagerModule = {

    this.getDependencyById(ASTManagerModule.moduleId).get
  }

  override def launch(): Future[Unit] =
    super
      .launch()
      .flatMap(_ => {
        this.getASTManager.onNewASTAvailable(this.onNewASTAvailableListener)

        Core
          .init()
          .map(_ => initialized = true)
      })

  override def stop(): Unit = {

    super.stop()

    this.getASTManager.onNewASTAvailable(this.onNewASTAvailableListener, true)
  }

  def onNewASTAvailable(listener: IHLASTListener, unsubscribe: Boolean = false): Unit = {

    this.addListener(this.astListeners, listener, unsubscribe)
  }

  def newASTAvailable(uri: String, version: Int, ast: BaseUnit): Unit = {

    this.connection.debug("Got new AST:\n" + ast.toString, "HlAstManager", "newASTAvailable")

    val projectFuture = this.hlFromAST(ast)

    projectFuture.map(project => {

      this.currentASTs(uri) = project

      this.notifyASTChanged(uri, version, project)
    })
  }

  def notifyASTChanged(uri: String, version: Int, project: IProject) = {

    this.connection.debug("Got new AST parser results, notifying the listeners", "HlAstManager", "notifyASTChanged")

    this.astListeners.foreach { listener =>
      listener.apply(uri, version, project.rootASTUnit.rootNode)
    }

  }

  private def checkInitialization(): Future[Unit] = synchronized {
    if (initialized) Future.successful()
    else Core.init().map(_ => initialized = true)
  }

  def hlFromAST(ast: BaseUnit): Future[IProject] = {
    val startTime = System.currentTimeMillis()

    Core
      .init()
      .flatMap(_ => Core.buildModel(ast, this.platform))
      .map(result => {

        val endTime = System.currentTimeMillis()
        this.connection
          .debugDetail(s"It took ${endTime - startTime} milliseconds to build ALS ast", "HlAstManager", "hlFromAST")

        result
      })
  }

  def forceGetCurrentAST(uri: String): Future[IProject] = {

    this.connection.debug(s"Calling forceGetCurrentAST for uri $uri", "HlAstManager", "forceGetCurrentAST")
    getASTManager.forceGetCurrentAST(uri).flatMap(hlFromAST)

    // Cache will break in some cases
//    this.currentASTs.get(uri) match {
//      case Some(current) => Future.successful(current)
//      case _ =>
//        getASTManager.forceGetCurrentAST(uri).flatMap(hlFromAST)
//    }
  }

  /**
    * Builds new AST for content
    *
    * @param uri
    * @param text
    * @return
    */
  def forceBuildNewAST(_uri: String, text: String): Future[IProject] = {
    val uri = PathRefine.refinePath(_uri, platform)
    this.connection.debug(s"Calling forceBuildNewAST for uri $uri", "HlAstManager", "forceBuildNewAST")

    getASTManager
      .forceBuildNewAST(uri, text)
      .flatMap(hlFromAST) recoverWith {
      case error =>
        this.connection.debugDetail(s"Failed to build AST for uri $uri", "HlAstManager", "forceBuildNewAST")
        Future.failed(error)
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
}

object HlAstManager {

  /**
    * Module ID
    */
  val moduleId: String = "HL_AST_MANAGER"
}
