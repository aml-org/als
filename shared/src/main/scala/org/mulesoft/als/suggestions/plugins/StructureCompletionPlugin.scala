package org.mulesoft.als.suggestions.plugins

import amf.core.remote.{Oas, Raml10, Vendor}
import org.mulesoft.als.suggestions.implementation.Suggestion
import org.mulesoft.als.suggestions.interfaces.{ICompletionPlugin, ICompletionRequest, ISuggestion}
import org.mulesoft.als.suggestions.plugins.raml.AnnotationReferencesCompletionPlugin
import org.mulesoft.high.level.interfaces.{IHighLevelNode, IParseResult}
import org.mulesoft.positioning.YamlLocation
import org.mulesoft.typesystem.nominal_interfaces.extras.PropertySyntaxExtra
import org.mulesoft.typesystem.nominal_interfaces.{IArrayType, IProperty}
import org.yaml.model.{YMap, YScalar}

import scala.concurrent.{Future, Promise}
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class StructureCompletionPlugin extends ICompletionPlugin {

    override def id: String = StructureCompletionPlugin.ID

    override def languages: Seq[Vendor] = StructureCompletionPlugin.supportedLanguages
    
    override def isApplicable(request: ICompletionRequest): Boolean = request.config.astProvider match {
        case Some(astProvider) =>
            if(request.astNode.isEmpty || request.astNode.get == null) {
                false;
            } else if(AnnotationReferencesCompletionPlugin().isApplicable(request)) {
				false;
			} else if(languages.indexOf(astProvider.language)<0){
                false;
            } else if(isContentType(request)) {
                true;
            } else if(isDiscriminatorValue(request)) {
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
    
    def isDiscriminatorValue(request: ICompletionRequest): Boolean = {
        if(request.astNode.get.property.isEmpty || request.astNode.get.property.get == null) {
            return false;
        }
        
        if(request.astNode.get.property.get.nameId.get != "discriminator") {
            return false;
        }
        
        request.astNode.get.property.get.domain.get.isAssignableFrom("TypeDeclaration");
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
            } else if(isDiscriminatorValue(request)) {
                extractFirstLevelScalars(request).map(name => Suggestion(name, id, name, request.prefix));
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
    
    def extractFirstLevelScalars(request: ICompletionRequest): Seq[String] = {
        request.astNode.get.parent.get.elements("properties").filter(_.definition.nameId match {
            case Some("StringTypeDeclaration") => true
            case Some("NumberTypeDeclaration") => true
            case Some("BooleanTypeDeclaration") => true
            case _ => false
        }).map(p => p.attributeValue("name")).filter(_ match {
            case Some(name) => true
        }).map(_.get.asInstanceOf[Some[String]].get);
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

        val definition = node.definition
        var propName = node.property.flatMap(_.nameId)
        var result:ListBuffer[IProperty] = ListBuffer()
        result ++= definition.allProperties
            .filter(p => p.nameId match {
            case Some(n) =>
                if(!existingProperties.contains(n)){
                    var e = p.getExtra(PropertySyntaxExtra).getOrElse(PropertySyntaxExtra.empty)
                    if(!e.allowsParentProperty(propName)){
                        false
                    }
                    else if(e.isKey || e.isValue || e.isHiddenFromUI){
                        false
                    }
                    else if(p.isMultiValue){
                        val isPrimitive = p.range match {
                            case Some(t) => t match {
                                case arr: IArrayType => arr.componentType match {
                                    case Some(ct) => ct.isValueType
                                    case _ => false
                                }
                                case _ => false
                            }
                            case _ => false
                        }
                        if(isPrimitive){
                            true
                        }
                        else if(e.isEmbeddedInArray||e.isEmbeddedInMaps){
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
        if(definition.isAssignableFrom("TypeDeclaration")){
            var typeIsSet = false
            node.sourceInfo.yamlSources.foreach(ys=>{
                YamlLocation(ys,node.astUnit.positionsMapper).value.foreach(v=>{
                    v.yPart match {
                        case map:YMap => map.entries.find(e=>{
                            e.key.value match {
                                case sc:YScalar => sc.value match {
                                    case "type" => e.value != null
                                    case _ => false
                                }
                                case _ => false
                            }
                        }) match {
                            case Some(e) => typeIsSet = true
                                case _ =>
                        }
                        case _ =>
                    }
                })
            })
            if(!typeIsSet){
                definition.universe.`type`("ObjectTypeDeclaration").flatMap(_.property("properties"))
                    .foreach(p => result += p)
            }
        }
        result
    }
}

object StructureCompletionPlugin {
    val ID = "structure.completion";
    
    val supportedLanguages:List[Vendor] = List(Raml10, Oas);
    
    def apply():StructureCompletionPlugin = new StructureCompletionPlugin();
}