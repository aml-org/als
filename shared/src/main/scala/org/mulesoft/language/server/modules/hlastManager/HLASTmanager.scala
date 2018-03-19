package org.mulesoft.language.server.modules.hlastManager


import org.mulesoft.language.common.typeInterfaces.{IRange, IValidationIssue, IValidationReport}
import org.mulesoft.language.server.core.{AbstractServerModule, IServerModule}
import org.mulesoft.language.server.server.modules.astManager.{IASTListener, IASTManagerModule, ParserHelper}
import org.mulesoft.language.server.server.modules.commonInterfaces.{IEditorTextBuffer, IPoint}
import org.mulesoft.language.server.server.modules.editorManager.IEditorManagerModule

import scala.collection.mutable.Buffer
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}
import scala.concurrent.ExecutionContext.Implicits.global
import org.mulesoft.high.level.Core
import org.mulesoft.high.level.project.IProject


class HLASTManager extends AbstractServerModule {

  /**
    * Module ID
    */
  val moduleId: String = "HL_AST_MANAGER"

  val moduleDependencies: Array[String] = Array("EDITOR_MANAGER", "AST_MANAGER")

  val mainInterfaceName: Option[String] = None

  var astListeners: Buffer[IASTListener] = ArrayBuffer()

  val onNewASTAvailableListener: IASTListener = new IASTListener {

    override def apply(uri: String, version: Int, ast: BaseUnit): Unit = {
      HLASTManager.this.newASTAvailable(uri, version, ast)
    }
  }

  protected def getEditorManager: IEditorManagerModule = {

    this.getDependencyById(IEditorManagerModule.moduleId).get
  }

  protected def getASTManager: IASTManagerModule = {

    this.getDependencyById(IASTManagerModule.moduleId).get
  }

  override def launch(): Try[IServerModule] = {

    val superLaunch = super.launch()

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

  def newASTAvailable(uri: String, version: Int, ast: BaseUnit): Unit = {

    this.connection.debug("Got new AST:\n" + ast.toString,
      "HLASTManager", "newASTAvailable")

    val project = this.hlFromAST(ast);

    this.notifyASTChanged(uri, version, project)
  }

  def notifyASTChanged(uri: String, version: Int, project: IProject) = {

    this.connection.debug("Got new AST parser results, notifying the listeners",
      "HLASTManager", "notifyASTChanged")

    this.astListeners.foreach { listener =>

      listener.apply(uri, version, project.rootASTUnit.rootNode)
    }

  }

  def hlFromAST(ast: BaseUnit): IProject {

    Core.buildModel(ast)
  }

}
object HLASTManager {

  /**
    * Module ID
    */
  val moduleId: String = "HL_AST_MANAGER"
}