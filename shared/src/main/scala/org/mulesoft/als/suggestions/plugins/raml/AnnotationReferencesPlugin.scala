package org.mulesoft.als.suggestions.plugins.raml

import amf.core.remote.{Raml10, Vendor}
import org.mulesoft.als.suggestions.implementation.Suggestion
import org.mulesoft.als.suggestions.interfaces.{ICompletionPlugin, ICompletionRequest, ISuggestion}
import org.mulesoft.high.level.Search
import org.mulesoft.high.level.interfaces.{IHighLevelNode, IParseResult}

import scala.concurrent.{Future, Promise}

class AnnotationReferencesCompletionPlugin extends ICompletionPlugin {
	override def id: String = AnnotationReferencesCompletionPlugin.ID;
	
	override def languages: Seq[Vendor] = AnnotationReferencesCompletionPlugin.supportedLanguages;
	
	override def isApplicable(request:ICompletionRequest): Boolean = request.config.astProvider match {
		case Some(astProvider) => languages.indexOf(astProvider.language) >= 0 && isInAnnotationName(request);
		
		case _ => false;
	}
	
	override def suggest(request: ICompletionRequest): Future[Seq[ISuggestion]] = {
		var actualPrefix = request.prefix;
		
		if(actualPrefix.trim.startsWith("(")) {
			actualPrefix = actualPrefix.substring(actualPrefix.indexOf("(") + 1).trim;
		}
		
		val result = findDeclarations(request).map(
			declaration => declaration.asElement.get.attributeValue("name").get)
			.map(value => value match {
				case Some(v) => v;

				case _ => value;
		}).map(value => Suggestion(
			value.asInstanceOf[String], id, value.asInstanceOf[String], actualPrefix
		));

		Promise.successful(result).future
	}
	
	def isInAnnotationName(request: ICompletionRequest): Boolean = {
		if(request.astNode.get.property.get == null) {
			return false;
		}
		
		request.astNode.get.property.get.nameId.get == "annotation";
	}
	
	def findDeclarations(request: ICompletionRequest): Seq[IParseResult] = {
		request.astNode.get.astUnit.rootNode.children.filter(child => child.property.get.nameId.get == "annotationTypes");
	}
}

object AnnotationReferencesCompletionPlugin {
	val ID = "ammotationRef.completion";
	
	val supportedLanguages: List[Vendor] = List(Raml10);
	
	def apply(): AnnotationReferencesCompletionPlugin = new AnnotationReferencesCompletionPlugin();
}