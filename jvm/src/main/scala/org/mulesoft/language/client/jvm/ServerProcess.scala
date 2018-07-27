package org.mulesoft.language.client.jvm;

import java.util

import org.mulesoft.als.suggestions.interfaces.ISuggestion
import org.mulesoft.language.client.jvm.dtoTypes.{GetCompletionRequest, GetStructureRequest}
import org.mulesoft.language.client.jvm.serverConnection.JAVAServerConnection
import org.mulesoft.language.common.dtoTypes._
import org.mulesoft.language.outline.structure.structureInterfaces.StructureNodeJSON
import org.mulesoft.language.server.core.Server
import org.mulesoft.language.server.modules.findDeclaration.FIndDeclarationModule
import org.mulesoft.language.server.modules.findReferences.{FindReferencesModule, RenameModule}
import org.mulesoft.language.server.modules.hlastManager.HLASTManager
import org.mulesoft.language.server.modules.outline.StructureManager
import org.mulesoft.language.server.modules.suggestions.SuggestionsManager
import org.mulesoft.language.server.server.modules.astManager.{ASTManager, IASTManagerModule}
import org.mulesoft.language.server.server.modules.validationManager.ValidationManager

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success};

object ServerProcess {
	private var connection = new JAVAServerConnection();
	
	def init() {
		val server = new Server(connection, null);
		
		server.registerModule(new ASTManager());
		server.registerModule(new HLASTManager());
		server.registerModule(new ValidationManager());
		server.registerModule(new SuggestionsManager());
		server.registerModule(new StructureManager());
		
		server.registerModule(new FindReferencesModule());
		server.registerModule(new FIndDeclarationModule());
		server.registerModule(new RenameModule());
		
		server.enableModule(IASTManagerModule.moduleId);
		server.enableModule(HLASTManager.moduleId);
		server.enableModule(ValidationManager.moduleId);
		server.enableModule(SuggestionsManager.moduleId);
		server.enableModule(StructureManager.moduleId);
		
		server.enableModule(FindReferencesModule.moduleId)
		server.enableModule(FIndDeclarationModule.moduleId)
		
		server.enableModule(RenameModule.moduleId);
	}
	
	def documentOpened(document: IOpenedDocument) {
		connection.handleOpenDocument(document);
	}
	
	def documentChanged(uri: String, text: String, version: Int) {
		
		connection.handleChangedDocument(new IChangedDocument(uri, version, Some(text), None));
	}
	
	def getUnit(url: String): Unit = {
	
	}
	
	def getSuggestions(uri: String, position: Int, suggestiosHandler: SuggestionsHandler) {
		connection.handleGetSuggestions(new GetCompletionRequest(uri, position)) andThen {
			case Success(result) => {
				var list = new util.ArrayList[ISuggestion]();
				
				result.map(new SuggestionComparableWrapper(_)).distinct.map(_.suggestion).foreach(list.add(_));
				
				suggestiosHandler.success(list);
				
				list.forEach(entry => {
					println(entry.category + ", " + entry.description + ", " + entry.displayText + ", " + entry.prefix + ", " + entry.text + ", " + list.size());
				})
			}
			
			case Failure(error) => suggestiosHandler.failure(error);
		}
	}
	
	def getStructure(uri: String, structureHandler: StructureHandler) {
		connection.handleGetStructure(new GetStructureRequest(uri)) andThen {
			case Success(result) => {
				var map: util.Map[String, JAVAStructureNode] = new util.HashMap[String, JAVAStructureNode]();
				
				result.structure.foreach(pair => map.put(pair._1, JAVAStructureNode(pair._2)));
				
				structureHandler.success(map);
			}
			
			case Failure(error) => structureHandler.success(new util.HashMap[String, JAVAStructureNode]());
		}
	}
	
	def openDeclaration(uri: String, position: Int, locationsHandler: LocationsHandler) {
		connection.findDeclaration(uri, position) andThen {
			case Success(result) => {
				var list: util.List[ILocation] = new util.ArrayList[ILocation]();
				
				result.foreach(location => list.add(location));
				
				locationsHandler.success(list);
			}
		}
	}
	
	def findReferences(uri: String, position: Int, locationsHandler: LocationsHandler) {
		connection.findReferences(uri, position) andThen {
			case Success(result) => {
				var list: util.List[ILocation] = new util.ArrayList[ILocation]();
				
				result.foreach(location => list.add(location));
				
				locationsHandler.success(list);
			}
		}
	}
	
	def rename(uri: String, position: Int, newName: String, renameHandler: LocationsHandler) {
		connection.rename(uri, position, newName) andThen {
			case Success(result) => {
				var list: util.List[ILocation] = new util.ArrayList[ILocation]();
				
				result.map(item => new ILocation {
					override var range: IRange = item.textEdits.get.head.range;
					
					override var uri: String = item.uri;
					
					override var version: Int = 0;
				}).foreach(location => list.add(location));
				
				renameHandler.success(list);
			}
		}
	}
	
	def setFS(fs: FS) {
		connection.fs = fs;
	}
	
	def onValidation(handler: ValidationHandler) {
		connection.validationHandler = handler;
	}
}

trait ValidationHandler {
	def success(pointOfView: String, issues: java.util.List[IValidationIssue]);
}

trait SuggestionsHandler {
	def success(list: java.util.List[ISuggestion]);
	
	def failure(throwable: Throwable);
}

trait StructureHandler {
	def success(map: util.Map[String, JAVAStructureNode]);
	
	def failure(throwable: Throwable);
}

trait LocationsHandler {
	def success(list: util.List[ILocation]);
	
	def failure(throwable: Throwable);
}

trait FS {
	def content(uri: String): String;
}

class JAVAStructureNode(var node: StructureNodeJSON) {
	var children: util.List[JAVAStructureNode] = new util.ArrayList[JAVAStructureNode]();
}

object JAVAStructureNode {
	def apply(node: StructureNodeJSON): JAVAStructureNode = {
		var result = new JAVAStructureNode(node);
		
		node.children.foreach(child => result.children.add(JAVAStructureNode(child)))
		
		result;
	}
}

class SuggestionComparableWrapper(var suggestion: ISuggestion) {
	override def toString(): String = suggestion.category + ", " + suggestion.description + ", " + suggestion.displayText + ", " + suggestion.prefix + ", " + suggestion.text;
	
	override def equals(another: scala.Any): Boolean = toString().equals(another.toString());
	
	override def hashCode(): Int = toString().hashCode();
}