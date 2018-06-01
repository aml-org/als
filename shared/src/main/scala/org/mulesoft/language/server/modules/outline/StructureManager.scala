package org.mulesoft.language.server.modules.outline

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
import org.mulesoft.high.level.interfaces.IProject
import org.mulesoft.high.level.interfaces.IParseResult
import org.mulesoft.language.outline.structure.structureImpl.StructureBuilder
import org.mulesoft.language.outline.structure.structureImpl.StructureBuilder
import org.mulesoft.language.outline.structure.structureInterfaces.StructureConfiguration
import org.mulesoft.language.outline.structure.structureImpl.ConfigFactory
import org.mulesoft.language.common.dtoTypes.IStructureReport
import org.mulesoft.language.outline.structure.structureInterfaces.StructureNodeJSON
import amf.core.model.document.BaseUnit

import scala.collection.mutable

class StructureManager extends AbstractServerModule {

  /**
    * Module ID
    */
  val moduleId: String = "STRUCTURE_MANAGER"

  val moduleDependencies: Array[String] = Array("EDITOR_MANAGER", "HL_AST_MANAGER")

  val mainInterfaceName: Option[String] = None

  val onNewASTAvailableListener: IHLASTListener = new IHLASTListener {

    override def apply(uri: String, version: Int, ast: IParseResult): Unit = {
      StructureManager.this.newASTAvailable(uri, version, ast)
    }
  }

  val onDocumentStructureListener: (String) => Future[Map[String, StructureNodeJSON]] =
    onDocumentStructure

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

      this.connection.onDocumentStructure(this.onDocumentStructureListener)

      Success(this)
    } else {

      superLaunch
    }
  }


  override def stop(): Unit = {

    super.stop()

    this.getASTManager.onNewASTAvailable(this.onNewASTAvailableListener, true)
    this.connection.onDocumentStructure(this.onDocumentStructureListener, true)
  }

  def newASTAvailable(astUri: String, astVersion: Int, ast: IParseResult): Unit = {

    this.connection.debug("Got new AST:\n" + ast.toString,
      "StructureManager", "newASTAvailable")

    val editor = this.getEditorManager.getEditor(astUri)

    if (editor.isDefined) {

      val struct = this.getStructureFromAST(ast, editor.get.language, editor.get.cursorPosition)

      this.connection.debugDetail(s"Got result for url ${astUri} of size ${struct.size}",
        "StructureManager", "onDocumentStructure")

      val structureReport = IStructureReport (

        astUri,

        astVersion,

        struct
      )

      this.connection.structureAvailable(structureReport)
    }
  }

  def onDocumentStructure(url: String): Future[Map[String, StructureNodeJSON]] = {

    this.connection.debug("Asked for structure:\n" + url,
      "StructureManager", "onDocumentStructure")

    val editor = this.getEditorManager.getEditor(url)

    if (editor.isDefined) {

      this.getASTManager.forceGetCurrentAST(url).map(ast=>{

        val result = StructureManager.this.getStructureFromAST(
          ast.rootASTUnit.rootNode, editor.get.language, editor.get.cursorPosition)

        this.connection.debugDetail(s"Got result for url ${url} of size ${result.size}",
          "StructureManager", "onDocumentStructure")

        result

      })

    } else {
      Future.successful(Map.empty)
    }
  }

  def getStructureFromAST(ast: IParseResult, language: String, position: Int): Map[String, StructureNodeJSON] = {

    val config = ConfigFactory.getConfig(new ASTProvider(ast, position, language))

    if (config.isDefined) {

      val categories = new StructureBuilder(config.get).getStructureForAllCategories

      val result = new mutable.HashMap[String, StructureNodeJSON]()
      categories.keySet.foreach(categoryName=>{
        result(categoryName) = categories(categoryName).toJSON
      })

      result.toMap
    } else {

      Map.empty
    }
  }
}

object StructureManager {
  /**
    * Module ID
    */
  val moduleId: String = "STRUCTURE_MANAGER"
}