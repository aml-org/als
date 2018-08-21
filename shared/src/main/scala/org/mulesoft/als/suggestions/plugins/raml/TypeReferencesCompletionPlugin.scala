package org.mulesoft.als.suggestions.plugins.raml

import amf.core.remote.{Raml10, Vendor}
import org.mulesoft.als.suggestions.implementation.{CompletionResponse, Suggestion}
import org.mulesoft.als.suggestions.interfaces._
import org.mulesoft.high.level.Search

import scala.collection.mutable.ListBuffer
import scala.concurrent.{Future, Promise}

class TypeReferencesCompletionPlugin extends ICompletionPlugin {
	override def id: String = TypeReferencesCompletionPlugin.ID;
	
	override def languages: Seq[Vendor] = TemplateReferencesCompletionPlugin.supportedLanguages;
	
	override def isApplicable(request:ICompletionRequest): Boolean = request.config.astProvider match {
		case Some(astProvider) => languages.indexOf(astProvider.language) >= 0 && request.astNode.isDefined && request.astNode.get != null && isInTypeTypeProperty(request);
		
		case _ => false;
	}
	
	override def suggest(request: ICompletionRequest): Future[ICompletionResponse] = {

        val result = TypeReferencesCompletionPlugin.typeSuggestions(request, id)
        var response = CompletionResponse(result,LocationKind.VALUE_COMPLETION,request)
		Promise.successful(response).future
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

object TypeReferencesCompletionPlugin {
	val ID = "typeRef.completion";
	
	val supportedLanguages: List[Vendor] = List(Raml10);
	
	def apply(): TypeReferencesCompletionPlugin = new TypeReferencesCompletionPlugin();


    def typeSuggestions(request: ICompletionRequest, id:String):Seq[Suggestion] = {
        val node = request.astNode.get
        var builtIns = node.astUnit.rootNode.definition.universe.builtInNames() :+ "object";
        val result = ListBuffer[Suggestion]() ++= builtIns.map(name => Suggestion(name, id, name, request.prefix));
        request.astNode.map(_.astUnit).foreach(u => {
            Search.getDeclarations(u, "TypeDeclaration").foreach(d => {
                d.node.attribute("name").flatMap(_.value).foreach(name => {
                    var proposal: String = name.toString
                    d.namespace.foreach(ns => proposal = s"$ns.$proposal")
                    result += Suggestion(proposal, id, proposal, request.prefix)
                })
            })
        })
        result
    }
}