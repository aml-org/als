package org.mulesoft.als.suggestions.plugins.raml

import amf.core.remote.{Raml10, Vendor}
import org.mulesoft.als.suggestions.implementation.{CompletionResponse, Suggestion}
import org.mulesoft.als.suggestions.interfaces._
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
	
	override def suggest(request: ICompletionRequest): Future[ICompletionResponse] = {
        val element = request.astNode.get.asElement.get
        val off = element.sourceInfo.valueOffset.getOrElse(0) + 2
        val facets = element.definition.allProperties.filter(_.domain.exists(_.isUserDefined))
        val result = facets.map(x=>{
            val facetName = x.nameId.get
            val suggestion = Suggestion(facetName, s"Fix the '$facetName' facet", facetName, request.prefix)
            if(!x.range.exists(_.isValueType)){
                suggestion.withTrailingWhitespace("\n" + " " * off)
            }
            suggestion
        })
        var response = CompletionResponse(result,LocationKind.KEY_COMPLETION,request)
		Promise.successful(response).future
	}
}

object FacetsCompletionPlugin {
	val ID = "facet.completion";
	
	val supportedLanguages: List[Vendor] = List(Raml10);
	
	def apply(): FacetsCompletionPlugin = new FacetsCompletionPlugin();
}