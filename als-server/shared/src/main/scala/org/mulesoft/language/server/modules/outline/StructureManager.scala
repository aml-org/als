package org.mulesoft.language.server.modules.outline

import org.mulesoft.high.level.interfaces.IParseResult
import org.mulesoft.language.common.dtoTypes.StructureReport
import org.mulesoft.language.outline.structure.structureImpl.{ConfigFactory, DocumentSymbol, StructureBuilder}
import org.mulesoft.language.outline.structure.structureInterfaces.{StructureConfiguration, StructureNodeJSON}
import org.mulesoft.language.server.common.utils.PathRefine
import org.mulesoft.language.server.core.AbstractServerModule
import org.mulesoft.language.server.modules.editorManager.EditorManagerModule
import org.mulesoft.language.server.modules.hlastManager.{HlAstManager, IHLASTListener}

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class StructureManager extends AbstractServerModule {

  /**
    * Module ID
    */
  val moduleId: String = "STRUCTURE_MANAGER"

  val moduleDependencies: Array[String] = Array("EDITOR_MANAGER", "HL_AST_MANAGER")

  val mainInterfaceName: Option[String] = None

  val onNewASTAvailableListener: IHLASTListener = (uri: String, version: Int, ast: IParseResult) => {
    StructureManager.this.newASTAvailable(uri, version, ast)
  }

  val onDocumentStructureListener: String => Future[List[DocumentSymbol]] =
    onDocumentStructure

  protected def getEditorManager: EditorManagerModule = {

    this.getDependencyById(EditorManagerModule.moduleId).get
  }

  protected def getASTManager: HlAstManager = {

    this.getDependencyById(HlAstManager.moduleId).get
  }

  override def launch(): Future[Unit] =
    super.launch()
      .map(_ => {
        this.getASTManager.onNewASTAvailable(this.onNewASTAvailableListener)
        this.connection.onDocumentStructure(this.onDocumentStructureListener)
      })

  override def stop(): Unit = {

    super.stop()

    this.getASTManager.onNewASTAvailable(this.onNewASTAvailableListener, true)
    this.connection.onDocumentStructure(this.onDocumentStructureListener, true)
  }

  def newASTAvailable(_astUri: String, astVersion: Int, ast: IParseResult): Unit = {
    val astUri = PathRefine.refinePath(_astUri, platform)
    this.connection.debug("Got new AST:\n" + ast.toString, "StructureManager", "newASTAvailable")

    val editor = this.getEditorManager.getEditor(astUri)

    if (editor.isDefined) {

      val struct = this.getStructureFromAST(ast, editor.get.language, editor.get.cursorPosition)

      this.connection
        .debugDetail(s"Got result for url $astUri of size ${struct.size}", "StructureManager", "onDocumentStructure")

      val structureReport = StructureReport(
        _astUri,
        astVersion,
        struct
      )

      this.connection.structureAvailable(structureReport)
    }
  }

  def onDocumentStructure(url: String): Future[List[DocumentSymbol]] = {

    this.connection.debug("Asked for structure:\n" + url, "StructureManager", "onDocumentStructure")

    val editor = this.getEditorManager.getEditor(url)

    if (editor.isDefined) {

      this.getASTManager
        .forceGetCurrentAST(url)
        .map(ast => {
          val result = StructureManager.this
            .getStructureFromAST(ast.rootASTUnit.rootNode, editor.get.language, editor.get.cursorPosition)

          this.connection
            .debugDetail(s"Got result for url $url of size ${result.size}", "StructureManager", "onDocumentStructure")

          result
        })

    } else {
      Future.successful(Nil)
    }
  }

  def getStructureFromAST(ast: IParseResult, language: String, position: Int): List[DocumentSymbol] = {

    ConfigFactory.getConfig(new ASTProvider(ast, position, language)) match {
      case Some(config) => StructureBuilder.listSymbols(ast, config)
      case _            => Nil
    }
  }
}

object StructureManager {

  /**
    * Module ID
    */
  val moduleId: String = "STRUCTURE_MANAGER"
}
