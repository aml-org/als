package org.mulesoft.als.suggestions.plugins.raml

import amf.core.remote.{Raml10, Vendor}
import org.mulesoft.als.suggestions.implementation.Suggestion
import org.mulesoft.als.suggestions.interfaces.{ICompletionPlugin, ICompletionRequest, ISuggestion}
import org.mulesoft.high.level.interfaces.IHighLevelNode
import org.mulesoft.typesystem.syaml.to.json.YJSONWrapper

import scala.collection.mutable
import scala.concurrent.{Future, Promise}

class FacetsCompletionPlugin extends ICompletionPlugin {
	override def id: String = FacetsCompletionPlugin.ID;
	
	override def languages: Seq[Vendor] = FacetsCompletionPlugin.supportedLanguages;
	
	override def isApplicable(request:ICompletionRequest): Boolean = request.config.astProvider match {
		case Some(astProvider) => languages.indexOf(astProvider.language) >= 0 && (request.actualYamlLocation match {
			case Some(l) => if(l.inKey(request.position)) {
				true;
			} else if(l.parentStack.nonEmpty && request.yamlLocation.get.hasSameValue(l.parentStack.last)) {
				var parent = l.parentStack.last;
				
				if(parent.keyValue.isDefined) {
					request.astNode.map(_.astUnit.positionsMapper) match {
						case Some(pm) => pm.point(request.position).line > parent.keyValue.get.range.start.line;
						
						case None => false;
					}
				} else {
					false;
				}
			} else {
				false;
			}
			
			case _ => false;
		}) && request.astNode.get.isElement && request.astNode.get.asElement.get.definition.isAssignableFrom("TypeDeclaration");
		
		case _ => false;
	}
	
	override def suggest(request: ICompletionRequest): Future[Seq[ISuggestion]] = {
		var facets = mutable.MutableList[String]();
		
		extendsDeclaredTypes(request.astNode.get.asElement.get, extractTypeDeclarations(request)).foreach(extractFacetsFromDeclaration(_).foreach(facets += _));
		
		extractFacetsFromDeclaration(request.astNode.get.asElement.get).foreach(facets += _);
		
		val result = facets.map(facetName => Suggestion(facetName + ":", id, facetName, request.prefix));

		Promise.successful(result).future
	}
	
	def extractTypeDeclarations(request: ICompletionRequest): Seq[IHighLevelNode] = {
		request.astNode.get.astUnit.rootNode.children.filter(_.isElement).map(_.asElement.get).filter(_.definition.isAssignableFrom("TypeDeclaration"));
	}
	
	def extractFacetsFromDeclaration(node: IHighLevelNode): Seq[String] = {
		node.elements("facets").map(_.attributeValue("name").get.asInstanceOf[Some[String]].get);
	}
	
	def extendsDeclaredTypes(node: IHighLevelNode, declarations: Seq[IHighLevelNode]): Seq[IHighLevelNode] = {
		var extendTypes = node.attributes("type").map(_.value.get.asInstanceOf[YJSONWrapper].value.asInstanceOf[String]);
		
		var currentDeclarations = declarations.filter(declaration => extendTypes.contains(declarationName(declaration)));
		
		var result = mutable.MutableList[IHighLevelNode]();
		
		currentDeclarations.foreach(declaration => {
			result += declaration;
			
			extendsDeclaredTypes(declaration, declarations).foreach(superDeclaration => result += superDeclaration);
		});
		
		result;
	}
	
	def declarationName(node: IHighLevelNode): String = {
		node.attributeValue("name").get.asInstanceOf[Some[String]].get
	}
}

object FacetsCompletionPlugin {
	val ID = "facet.completion";
	
	val supportedLanguages: List[Vendor] = List(Raml10);
	
	def apply(): FacetsCompletionPlugin = new FacetsCompletionPlugin();
}