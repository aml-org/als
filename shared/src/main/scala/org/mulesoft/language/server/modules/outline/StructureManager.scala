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
import org.mulesoft.language.server.common.utils.PathRefine

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

  def newASTAvailable(_astUri: String, astVersion: Int, ast: IParseResult): Unit = {
      val astUri = PathRefine.refinePath(_astUri, platform)
    this.connection.debug("Got new AST:\n" + ast.toString,
      "StructureManager", "newASTAvailable")

    val editor = this.getEditorManager.getEditor(astUri)

    if (editor.isDefined) {

      val struct = this.getStructureFromAST(ast, editor.get.language, editor.get.cursorPosition)

      this.connection.debugDetail(s"Got result for url ${astUri} of size ${struct.size}",
        "StructureManager", "onDocumentStructure")

      val structureReport = IStructureReport (

        _astUri,

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

      this.getASTManager.forceGetCurrentAST(url).map(ast => {
		  val result = StructureManager.this.getStructureFromAST(ast.rootASTUnit.rootNode, editor.get.language, editor.get.cursorPosition);
		  
		  this.connection.debugDetail(s"Got result for url ${url} of size ${result.size}", "StructureManager", "onDocumentStructure");
		  var prepared = new mutable.HashMap[String, StructureNodeJSON]();
	
		  result.keySet.foreach(key => {
			  prepared(key) = recoverRanges(result(key), 0, ast.rootASTUnit.text.length - 1);
		  });
	
		  prepared.toMap;
      });

    } else {
      Future.successful(Map.empty)
    }
  }
	
	def recoverRanges(node: StructureNodeJSON, start: Int, end: Int): StructureNodeJSON = {
		var nodeEnd = node.end;
		
		var nodeStart = node.start;
		
		if(isBrokenRange(node)) {
			nodeStart = start;
			
			nodeEnd = findNodeEnd(node, start, end);
		}
		
		var childStart = nodeStart;
		
		var newChildren: Seq[StructureNodeJSON] = node.children.map(child => {
			var recovered = recoverRanges(child, childStart, nodeEnd);
			
			childStart = findNodeEnd(child, childStart, nodeEnd);
			
			recovered
		});
		
		new StructureNodeJSON {
			override def start: Int = nodeStart;
			override def end: Int = nodeEnd;
			
			override def children: Seq[StructureNodeJSON] = newChildren;
			
			override def icon: String = node.icon;
			override def typeText: Option[String] = node.typeText;
			override def textStyle: String = node.textStyle;
			override def text: String = node.text;
			override def category: String = node.category;
			override def selected: Boolean = node.selected;
			override def key: String = node.key;
		};
	}
	
  def findNodeEnd(node: StructureNodeJSON, start: Int, nodeEnd: Int = -1): Int = {
	  if(!isBrokenRange(node)) {
		  return node.end;
	  }
	  
	  var end = start;
	  
	  if(node.children.isEmpty) {
		  if(end + 2 > nodeEnd) {
			  return end
		  }
		  
		  return end + 2;
	  }
	  
	  node.children.foreach(item => {
		  end = findNodeEnd(item, end, nodeEnd);
	  })
	  
	  end;
  }

  def isBrokenRange(node: StructureNodeJSON): Boolean = {
	  return node.start <= 0 || node.end <= 0;
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