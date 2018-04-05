package org.mulesoft.als.suggestions.plugins

import amf.core.remote.{Oas, Raml10, Vendor}
import org.mulesoft.als.suggestions.implementation.Suggestion
import org.mulesoft.als.suggestions.interfaces.{ICompletionPlugin, ICompletionRequest, ISuggestion}
import org.mulesoft.als.suggestions.plugins.raml.AnnotationReferencesCompletionPlugin
import org.mulesoft.high.level.interfaces.{IHighLevelNode, IParseResult}
import org.mulesoft.typesystem.nominal_interfaces.extras.PropertySyntaxExtra
import org.mulesoft.typesystem.nominal_interfaces.IProperty
import org.yaml.model.YScalar

import scala.concurrent.{Future, Promise}
import scala.collection.mutable

class StructureCompletionPlugin extends ICompletionPlugin {

    override def id: String = StructureCompletionPlugin.ID

    override def languages: Seq[Vendor] = StructureCompletionPlugin.supportedLanguages
    
    override def isApplicable(request: ICompletionRequest): Boolean = request.config.astProvider match {
        case Some(astProvider) =>
            if(AnnotationReferencesCompletionPlugin().isApplicable(request)) {
				false;
			} else if(languages.indexOf(astProvider.language)<0){
                false;
            } else if(isContentType(request)) {
                true;
            } else {
                request.actualYamlLocation match {
                    case Some(l) =>
                        if(l.inKey(request.position)){
                            true
                        }
                        else if(request.yamlLocation.get.hasSameValue(l)){
                            if(l.keyValue.isDefined) {
                                request.astNode.map(_.astUnit.positionsMapper) match {
                                    case Some(pm) => pm.point(request.position).line > l.keyValue.get.range.start.line
                                    case None => false
                                }
                            }
                            else {
                                false
                            }
                        }
                        else {
                            false
                        }
                    case _ => false
                }
            }
        case _ => false
    }
    
    def contentTypes(request: ICompletionRequest): Seq[String] = {
        var extra = request.astNode.get.asElement.get.definition.universe.`type`("Api").get.property("mediaType").get.getExtra(PropertySyntaxExtra).get;
        
        extra.oftenValues.map(_.asInstanceOf[String]);
    }
    
    def isBody(node: IParseResult): Boolean = {
        node.property.get.nameId.get == "body"
    }
    
    def isContentType(request: ICompletionRequest): Boolean = {
        getBody(request) match {
            case Some(body) => if(!isBody(body)) {
                return false
            }
            case _ => {
                return false
            }
        }
        
        request.actualYamlLocation match {
            case Some(location) => location.keyValue match {
                case Some(yPart) => yPart.toString() == "body";
                
                case _ => return false;
            }
            
            case _ => return false;
        }
        
        request.actualYamlLocation.get.keyValue.get.yPart.asInstanceOf[YScalar].text == "body"
    }
    
    def isMethodKey(request: ICompletionRequest): Boolean = {
        if(!request.astNode.get.isElement) {
            return false;
        }
        
        request.astNode.get.asElement.get.definition.isAssignableFrom("ResourceBase")
    }
    
    def methodsList(request: ICompletionRequest): Seq[String] = {
        var extra = request.astNode.get.asElement.get.definition.universe.`type`("Method").get.property("method").get.getExtra(PropertySyntaxExtra).get;
    
        extra.enum.map(_.asInstanceOf[String]);
    }

    override def suggest(request: ICompletionRequest): Future[Seq[ISuggestion]] = {
        var result = request.astNode match {
            case Some(n) => if(isContentType(request)) {
                contentTypes(request).map(value => Suggestion(value, id, value, request.prefix));
            } else if(n.isElement) {
                var element = n.asElement.get;

                extractSuggestableProperties(element).map(_.nameId.get).map(pName => Suggestion(pName, id, pName, request.prefix));
            } else {
                Seq();
            }

            case _ => Seq();
        }
        
        if(isMethodKey(request)) {
            result = result ++ methodsList(request).map(value => Suggestion(value, id, value, request.prefix))
        }

        Promise.successful(result).future
    }
    
    def getBody(request: ICompletionRequest): Option[IParseResult] = {
        //TODO: implement when node searching will be fixed.
        Option.empty;
    }

    def extractSuggestableProperties(node:IHighLevelNode):Seq[IProperty] = {

        var existingProperties:mutable.Map[String,IProperty] = mutable.Map()
        node.children.foreach(x=>x.property match {
            case Some(p) => p.nameId match {
                case Some(n) => existingProperties(n) = p
                case _ =>
            }
            case _ =>
        })

        var result = node.definition.allProperties
            .filter(p => p.nameId match {
            case Some(n) =>
                if(!existingProperties.contains(n)){
                    var e = p.getExtra(PropertySyntaxExtra).getOrElse(PropertySyntaxExtra.empty)

                    if(e.isKey || e.isValue || e.isHiddenFromUI){
                        false
                    }
                    else if(p.isMultiValue){
                        if(e.isEmbeddedInArray||e.isEmbeddedInMaps){
                            true
                        }
                        else {
                            false
                        }
                    }
                    else true
                }
                else false
            case _ => false
        })
        result
    }
}

object StructureCompletionPlugin {
    val ID = "structure.completion";
    
    val supportedLanguages:List[Vendor] = List(Raml10, Oas);
    
    def apply():StructureCompletionPlugin = new StructureCompletionPlugin();
}