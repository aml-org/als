package org.mulesoft.als.suggestions.plugins

import amf.core.remote.{Oas, Oas2, Oas2Yaml, Raml10, Vendor}
import org.mulesoft.als.suggestions.implementation.{CompletionResponse, Suggestion}
import org.mulesoft.als.suggestions.interfaces.{ICompletionPlugin, ICompletionRequest, Syntax, LocationKind, ICompletionResponse}
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
                            request.yamlLocation.get.value.get.yPart match {
                                case m:YMap => l.mapEntry match {
                                    case Some(me) =>
                                        m.entries.contains(me.yPart)
                                    case _ => false
                                }
                                case _ => false
                            }
                        } else if(l.parentStack.nonEmpty && request.yamlLocation.get.hasSameValue(l.parentStack.last)) {
                            var parent = l.parentStack.last;

                            if(parent.keyValue.isDefined) {
                                request.astNode.map(_.astUnit.positionsMapper) match {
                                    case Some(pm) => pm.point(request.position).line > parent.keyValue.get.range.start.line
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

    override def suggest(request: ICompletionRequest): Future[ICompletionResponse] = {

        var result = request.astNode match {
            case Some(n) =>
                var isYAML = request.config.astProvider.get.syntax == Syntax.YAML
                var postfix = if (isYAML) ":" else ""
                if (isContentType(request)) {
                    contentTypes(request).map(value => Suggestion(value + postfix, id, value, request.prefix));
                } else if (isDiscriminatorValue(request)) {
                    var a = extractFirstLevelScalars(request);

                    extractFirstLevelScalars(request).map(name => Suggestion(name + postfix, id, name, request.prefix));
                } else if (n.isElement) {
                    var element = n.asElement.get;

                    extractSuggestableProperties(element).map(_.nameId.get).map(pName => Suggestion(pName + postfix, id, pName, request.prefix));
                } else {
                    Seq();
                }

            case _ => Seq();
        }
        val response = CompletionResponse(result, LocationKind.KEY_COMPLETION, request)
        Promise.successful(response).future
    }
    
    def extractFirstLevelScalars(request: ICompletionRequest): Seq[String] = request.astNode.get.parent.get.elements("properties").filter(_.definition match {
        case propertyType => Seq("StringTypeDeclaration", "NumberTypeDeclaration", "BooleanTypeDeclaration").exists(propertyType.isAssignableFrom(_));
        
        case _ => false;
    }).map(p => p.definition.nameId.get);
    
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
    
    val supportedLanguages:List[Vendor] = List(Raml10, Oas, Oas2, Oas2Yaml);
    
    def apply():StructureCompletionPlugin = new StructureCompletionPlugin();
}