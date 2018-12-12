package org.mulesoft.als.suggestions.plugins.raml

import amf.core.remote.{Raml10, Vendor}
import org.mulesoft.als.suggestions.implementation.{CompletionResponse, Suggestion}
import org.mulesoft.als.suggestions.interfaces._
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
	
	override def suggest(request: ICompletionRequest): Future[ICompletionResponse] = {
		var actualPrefix = request.prefix;
		
		if(actualPrefix.trim.startsWith("(")) {
			actualPrefix = actualPrefix.substring(actualPrefix.indexOf("(") + 1).trim;
		}

        var closeBracket = true
        if(request.config.originalContent.isDefined){
            var off = request.position
            var content = request.config.originalContent.get
            if(off < content.length && content.charAt(off) == ')'){
                closeBracket = false
            }
        }

		
		val result = findDeclarations(request).map(
			declaration => declaration.asElement.get.attributeValue("name").get)
			.map(value => value match {
				case Some(v) => v;

				case _ => value;
		}).map(value => {
            var displayText = value.asInstanceOf[String]
            var text = displayText
            if(closeBracket){
                text += ")"
            }
            Suggestion(text, s"'$text' annotation", displayText, actualPrefix)
        });
        val response = CompletionResponse(result, LocationKind.KEY_COMPLETION, request).withNoColon(!closeBracket)
		Promise.successful(response).future
	}
	
	def isInAnnotationName(request: ICompletionRequest): Boolean = {
        if(request.astNode.isEmpty){
            return false
        }
        val node = request.astNode.get
        if(node.property.isEmpty || Option(node.property.get).isEmpty) {
			return false;
		}
        if(!request.prefix.startsWith("(")){
            false
        }
        else if(node.isElement){
            node.asElement.get.definition.isAssignableFrom("Annotable");
        }
        else{
            false
        }
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