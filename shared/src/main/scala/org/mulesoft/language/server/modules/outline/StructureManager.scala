import org.mulesoft.language.common.dtoTypes._
import org.mulesoft.language.server.core.{AbstractServerModule, IServerModule}
import org.mulesoft.language.server.modules.hlastManager.{HLASTManager, IHLASTListener}
import org.mulesoft.language.server.server.modules.commonInterfaces.{IEditorTextBuffer, IPoint}
import org.mulesoft.language.server.server.modules.editorManager.IEditorManagerModule

import scala.collection.mutable.Buffer
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}
import scala.concurrent.ExecutionContext.Implicits.global
import org.mulesoft.high.level.Core
import org.mulesoft.high.level.project.IProject
import org.mulesoft.high.level.interfaces.IParseResult
import org.mulesoft.language.outline.structure.structureImpl.StructureBuilder
import org.mulesoft.language.outline.structure.structureImpl.StructureBuilder
import org.mulesoft.language.outline.structure.structureInterfaces.StructureConfiguration
import org.mulesoft.language.outline.structure.structureImpl.ConfigFactory
import org.mulesoft.language.common.dtoTypes.IStructureReport

class StructureManager extends AbstractServerModule {

  /**
    * Module ID
    */
  val moduleId: String = "STRUCTURE_MANAGER"

  val moduleDependencies: Array[String] = Array("EDITOR_MANAGER", "HL_AST_MANAGER")

  val mainInterfaceName: Option[String] = None

  val onNewASTAvailableListener: IASTListener = new IASTListener {

    override def apply(uri: String, version: Int, ast: BaseUnit): Unit = {
      StructureManager.this.newASTAvailable(uri, version, ast)
    }
  }

  protected def getEditorManager: IEditorManagerModule = {

    this.getDependencyById(IEditorManagerModule.moduleId).get
  }

  protected def getASTManager: HLASTManager = {

    this.getDependencyById(HLASTManager.moduleId).get
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

  def newASTAvailable(astUri: String, astVersion: Int, ast: IParseResult): Unit = {

    this.connection.debug("Got new AST:\n" + ast.toString,
      "StructureManager", "newASTAvailable")

    val struct = this.getStructureFromAST(ast)

    val structureReport = new IStructureReport {

      override var uri: String = astUri

      override var version: Int = astVersion

      override var structure: Map[String, StructureNode] = struct
    }

    this.connection.structureAvailable(structureReport)
  }

  def getStructureFromAST(ast: IParseResult): Map[String, StructureNode] = {

    val config = ConfigFactory.getConfig(new ASTProvider(ast))

    new StructureBuilder(config).getStructureForAllCategories()
  }
}
