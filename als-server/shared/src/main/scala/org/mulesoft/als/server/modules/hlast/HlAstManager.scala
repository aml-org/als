package org.mulesoft.als.server.modules.hlast

import amf.core.model.document.BaseUnit
import org.mulesoft.als.server.Initializable
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.modules.ast.AstManager
import org.mulesoft.als.server.textsync.TextDocumentManager
import org.mulesoft.als.server.util.PathRefine
import org.mulesoft.high.level.Core
import org.mulesoft.high.level.implementation.AlsPlatform
import org.mulesoft.high.level.interfaces.IProject

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class HlAstManager(private val textDocumentManager: TextDocumentManager,
                   private val astManager: AstManager,
                   private val platform: AlsPlatform,
                   private val logger: Logger) extends Initializable {

  private var initialized: Boolean = false

  var astListeners: mutable.Buffer[HlAstListener] = ArrayBuffer()

  var currentASTs: mutable.Map[String, IProject] = mutable.HashMap()

  override def initialize(): Future[Unit] =
    Core
      .init()
      .map(_ => initialized = true)
      .map(_ => astManager.onNewASTAvailable(this.newASTAvailable))

  def onNewASTAvailable(listener: HlAstListener, unsubscribe: Boolean = false): Unit =
    addListener(this.astListeners, listener, unsubscribe)

  def newASTAvailable(uri: String, version: Int, ast: BaseUnit): Unit = {

    logger.debug("Got new AST:\n" + ast.toString, "HlAstManager", "newASTAvailable")

    val projectFuture = hlFromAST(ast)

    projectFuture.map(project => {
      currentASTs(uri) = project
      notifyASTChanged(uri, version, project)
    })
  }

  def notifyASTChanged(uri: String, version: Int, project: IProject): Unit = {

    logger.debug("Got new AST parser results, notifying the listeners", "HlAstManager", "notifyASTChanged")

    this.astListeners.foreach { listener =>
      listener.apply(uri, version, project.rootASTUnit.rootNode)
    }

  }

  def hlFromAST(ast: BaseUnit): Future[IProject] = {
    val startTime = System.currentTimeMillis()
    Core.buildModel(ast, this.platform)
      .map(result => {
        val endTime = System.currentTimeMillis()
        logger.debugDetail(s"It took ${endTime - startTime} milliseconds to build ALS ast", "HlAstManager", "hlFromAST")

        result
      })
  }

  def forceGetCurrentAST(uri: String): Future[IProject] = {
    logger.debug(s"Calling forceGetCurrentAST for uri $uri", "HlAstManager", "forceGetCurrentAST")
    astManager.forceGetCurrentAST(uri).flatMap(hlFromAST)
  }

  /**
    * Builds new AST for content
    */
  def forceBuildNewAST(_uri: String, text: String): Future[IProject] = {
    val uri = PathRefine.refinePath(_uri, platform)
    logger.debug(s"Calling forceBuildNewAST for uri $uri", "HlAstManager", "forceBuildNewAST")

    astManager
      .forceBuildNewAST(uri, text)
      .flatMap(hlFromAST) recoverWith {
      case error =>
        logger.debugDetail(s"Failed to build AST for uri $uri", "HlAstManager", "forceBuildNewAST")
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

