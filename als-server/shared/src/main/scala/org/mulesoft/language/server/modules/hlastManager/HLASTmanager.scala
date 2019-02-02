package org.mulesoft.language.server.modules.hlastManager

import amf.core.model.document.BaseUnit
import org.mulesoft.high.level.Core
import org.mulesoft.high.level.interfaces.IProject
import org.mulesoft.language.server.common.utils.PathRefine
import org.mulesoft.language.server.core.{AbstractServerModule, IServerModule}
import org.mulesoft.language.server.modules.astManager.{IASTListener, IASTManagerModule}
import org.mulesoft.language.server.modules.editorManager.IEditorManagerModule

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success, Try}

class HLASTmanager extends AbstractServerModule with IHLASTManagerModule {
  private var initialized: Boolean = false

  val moduleDependencies: Array[String] = Array(IEditorManagerModule.moduleId, IASTManagerModule.moduleId)

  var astListeners: mutable.Buffer[IHLASTListener] = ArrayBuffer()

  var currentASTs: mutable.Map[String, IProject] = mutable.HashMap()

  val onNewASTAvailableListener: IASTListener = (uri: String, version: Int, ast: BaseUnit) => {
    HLASTmanager.this.newASTAvailable(uri, version, ast)
  }

  protected def getEditorManager: IEditorManagerModule = {

    this.getDependencyById(IEditorManagerModule.moduleId).get
  }

  protected def getASTManager: IASTManagerModule = {

    this.getDependencyById(IASTManagerModule.moduleId).get
  }

  override def launch(): Try[IServerModule] = {

    val superLaunch = super.launch()

    Core
      .init()
      .map(nothing => {
        initialized = true
      })

    if (superLaunch.isSuccess) {

      this.getASTManager.onNewASTAvailable(this.onNewASTAvailableListener)

      Success(this)
    } else {

      superLaunch
    }
  }

  override def stop(): Unit = {

    super.stop()

    this.getASTManager.onNewASTAvailable(this.onNewASTAvailableListener, true)
  }

  def onNewASTAvailable(listener: IHLASTListener, unsubscribe: Boolean = false): Unit = {

    this.addListener(this.astListeners, listener, unsubscribe)
  }

  def newASTAvailable(uri: String, version: Int, ast: BaseUnit): Unit = {

    this.connection.debug("Got new AST:\n" + ast.toString, "HLASTmanager", "newASTAvailable")

    val projectFuture = this.hlFromAST(ast)

    projectFuture.map(project => {

      this.currentASTs(uri) = project

      this.notifyASTChanged(uri, version, project)
    })
  }

  def notifyASTChanged(uri: String, version: Int, project: IProject) = {

    this.connection.debug("Got new AST parser results, notifying the listeners", "HLASTmanager", "notifyASTChanged")

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

    checkInitialization()
      .flatMap(_ => Core.buildModel(ast, this.platform))
      .map(result => {

        val endTime = System.currentTimeMillis()
        this.connection
          .debugDetail(s"It took ${endTime - startTime} milliseconds to build ALS ast", "HLASTmanager", "hlFromAST")

        result
      })
  }

  def forceGetCurrentAST(uri: String): Future[IProject] = {

    this.connection.debug(s"Calling forceGetCurrentAST for uri $uri", "HLASTmanager", "forceGetCurrentAST")

    val current = this.currentASTs.get(uri)

    if (current.isDefined) {
      Future.successful(current.get)
    } else {
      getASTManager.forceGetCurrentAST(uri).flatMap(hlFromAST)
    }
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
    this.connection.debug(s"Calling forceBuildNewAST for uri $uri", "HLASTmanager", "forceBuildNewAST")

    getASTManager
      .forceBuildNewAST(uri, text)
      .flatMap(hlFromAST) recoverWith {
      case error =>
        this.connection.debugDetail(s"Failed to build AST for uri $uri", "HLASTmanager", "forceBuildNewAST")
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

object HLASTmanager {

  /**
    * Module ID
    */
  val moduleId: String = "HL_AST_MANAGER"
}
