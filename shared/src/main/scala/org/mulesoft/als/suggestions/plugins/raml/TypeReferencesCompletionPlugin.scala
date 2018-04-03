package org.mulesoft.als.suggestions.plugins.raml

import amf.core.remote.{Raml10, Vendor}
import org.mulesoft.als.suggestions.implementation.Suggestion
import org.mulesoft.als.suggestions.interfaces.{ICompletionPlugin, ICompletionRequest, ISuggestion}

import scala.concurrent.{Future, Promise}

class TypeReferenceCompletionPlugin extends ICompletionPlugin {
	override def id: String = TemplateReferencesCompletionPlugin.ID;
	
	override def languages: Seq[Vendor] = TemplateReferencesCompletionPlugin.supportedLanguages;
	
	override def isApplicable(request:ICompletionRequest): Boolean = request.config.astProvider match {
		case Some(astProvider) => languages.indexOf(astProvider.language) >= 0 && isInTypeTypeProperty(request);
		
		case _ => false;
	}
	
	override def suggest(request: ICompletionRequest): Future[Seq[ISuggestion]] = {

		var builtIns = request.astNode.get.asAttr.get.definition.get.universe.builtInNames() :+ "object";
		
		val result = builtIns.map(name => Suggestion(name, id, name, request.prefix));
		Promise.successful(result).future
	}
	
	def isInTypeTypeProperty(request: ICompletionRequest): Boolean = {
		if(request.astNode.get.isElement) {
			return false;
		}
		
		if(request.astNode.get.property.get == null) {
			return false;
		}
		
		if(request.astNode.get.property.get.nameId.get != "type") {
			return false;
		}
		
		if(request.astNode.get.property.get.domain.get.nameId.get != "TypeDeclaration") {
			return false;
		}
		
		true;
	}
}

object TypeReferenceCompletionPlugin {
	val ID = "typeRef.completion";
	
	val supportedLanguages: List[Vendor] = List(Raml10);
	
	def apply(): TypeReferenceCompletionPlugin = new TypeReferenceCompletionPlugin();
}