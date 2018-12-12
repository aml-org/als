package org.mulesoft.als.suggestions.plugins.raml

import amf.core.remote.{Raml10, Vendor}
import org.mulesoft.als.suggestions.implementation.{CompletionResponse, Suggestion}
import org.mulesoft.als.suggestions.interfaces._
import org.mulesoft.high.level.Search
import org.mulesoft.positioning.YamlLocation
import org.yaml.model.{YMapEntry, YNode, YScalar}

import scala.collection.mutable.ListBuffer
import scala.concurrent.{Future, Promise}

class TypeReferencesCompletionPlugin extends ICompletionPlugin {
	override def id: String = TypeReferencesCompletionPlugin.ID;
	
	override def languages: Seq[Vendor] = TemplateReferencesCompletionPlugin.supportedLanguages;
	
	override def isApplicable(request:ICompletionRequest): Boolean = request.config.astProvider match {
		case Some(astProvider) => languages.indexOf(astProvider.language) >= 0 && request.astNode.isDefined && request.astNode.get != null && !IncludeCompletionPlugin.apply().isApplicable(request) && isInTypeTypeProperty(request);
		
		case _ => false;
	}
	
	override def suggest(request: ICompletionRequest): Future[ICompletionResponse] = {

        val result = TypeReferencesCompletionPlugin.typeSuggestions(request, id)
        var response = CompletionResponse(result,LocationKind.VALUE_COMPLETION,request)
		Promise.successful(response).future
	}

    def isInTypeTypeProperty(request: ICompletionRequest): Boolean = {
        val node = request.astNode.get
        if(node.isElement) {
            val pm = node.astUnit.positionsMapper
            val result = node.sourceInfo.yamlSources.headOption.filter(_.isInstanceOf[YMapEntry]).map(_.asInstanceOf[YMapEntry]).exists(me=>{
                val loc = YamlLocation(me,pm)
                val value = me.value
                val valueScalar = value.value
				
				val valueTag = value match {
					case node: YNode.MutRef => node.origTag;
					
					case node: YNode => node.tag;
					
					case _ => null;
				}
				
                val isScalar = (valueTag == null || valueTag.text != "!include") && valueScalar.isInstanceOf[YScalar]
                isScalar
            })
            return result
		}
		
		if(node.property.get == null) {
			return false;
		}
		
		if(node.property.get.nameId.get != "type") {
			return false;
		}
		
		if(node.property.get.domain.get.nameId.get != "TypeDeclaration") {
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
        var element = if(node.isElement) node.asElement.get else node.parent.get
        val typeName = element.attribute("name").flatMap(_.value).map(_.toString).getOrElse("")
        var builtIns = node.astUnit.rootNode.definition.universe.builtInNames() :+ "object";
        val result = ListBuffer[Suggestion]() ++= builtIns.map(name => Suggestion(name, "Builtin type", name, request.prefix));
        request.astNode.map(_.astUnit).foreach(u => {
            Search.getDeclarations(u, "TypeDeclaration").foreach(d => {
                d.node.attribute("name").flatMap(_.value).foreach(name => {
                    if(name!=typeName) {
                        var proposal: String = name.toString
                        d.namespace.foreach(ns => proposal = s"$ns.$proposal")
                        result += Suggestion(proposal, "User type", proposal, request.prefix)
                    }
                })
            })
        })
        result
    }
}