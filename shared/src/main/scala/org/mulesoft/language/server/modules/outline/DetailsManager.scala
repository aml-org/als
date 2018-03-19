import org.mulesoft.language.common.typeInterfaces._
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
import org.mulesoft.language.outline.structure.detailsImpl.DetailsBuilder
import org.mulesoft.language.outline.structure.detailsImpl.DetailsConfiguration
import org.mulesoft.language.outline.structure.detailsImpl.ConfigFactory
import org.mulesoft.language.common.typeInterfaces.IDetailsReport
import scala.collection.mutable

class DetailsManager extends AbstractServerModule {

  /**
    * Module ID
    */
  val moduleId: String = "DETAILS_MANAGER"

  val moduleDependencies: Array[String] = Array("EDITOR_MANAGER", "HL_AST_MANAGER")

  val mainInterfaceName: Option[String] = None

  val positions: mutable.HashMap[String, Int] = new mutable.HashMap()

  val onNewASTAvailableListener: IASTListener = new IASTListener {

    override def apply(uri: String, version: Int, ast: BaseUnit): Unit = {
      DetailsManager.this.newASTAvailable(uri, version, ast)
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
      this.connection.onChangePosition((uri, position) => {
        this.positions(uri) = position
      })

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
      "DetailsManager", "newASTAvailable")


    val det = this.getDetailsFromAST(ast, uri)

    val detailsReport = new IDetailsReport {

      override var uri: String = astUri

      override var position: Int = this.positions(uri)

      override var version: Int = astVersion

      override var details: IDetailsItem = det
    }

    this.connection.detailsAvailable(detailsReport)
  }

  def getDetailsFromAST(ast: IParseResult, uri: String): Unit = {

    val config = ConfigFactory.getConfig(new ASTProvider(ast))

    new DetailsBuilder(config).getDetails(ast, this.positions(uri))
  }
}
