package org.mulesoft.als.suggestions.plugins

import amf.core.remote.{Oas, Oas20, Raml10, Vendor}
import org.mulesoft.als.suggestions.implementation.{CompletionResponse, Suggestion, SuggestionCategoryRegistry}
import org.mulesoft.als.suggestions.interfaces._
import org.mulesoft.high.level.builder.ProjectBuilder
import org.mulesoft.typesystem.nominal_interfaces.IArrayType
import org.mulesoft.typesystem.nominal_interfaces.extras.{DescriptionExtra, PropertySyntaxExtra}

import scala.collection.mutable.ListBuffer
import scala.concurrent.{Future, Promise}

class KnownKeyPropertyValuesCompletionPlugin extends ICompletionPlugin {

    override def id: String = KnownKeyPropertyValuesCompletionPlugin.ID

    override def languages: Seq[Vendor] = StructureCompletionPlugin.supportedLanguages
    
    override def isApplicable(request: ICompletionRequest): Boolean = {
        val se = request.astNode.flatMap(_.asAttr).flatMap(_.property).flatMap(_.getExtra(PropertySyntaxExtra))
        if (request.astNode.isEmpty || !request.astNode.get.isElement) {
            if(isMethodKey(request)){
                true
            }
            else if (se.nonEmpty && se.get.isKey && (se.get.oftenValues.nonEmpty || se.get.enum.nonEmpty)) {
                true
            }
            else {
                false
            }
        }  else if (request.actualYamlLocation.isEmpty) {
            false;
        } else if (request.kind != LocationKind.KEY_COMPLETION) {
            false;
        } else if (request.actualYamlLocation.isEmpty || request.yamlLocation.isEmpty) {
            false;
        } else {
            request.actualYamlLocation.get.parentStack.nonEmpty && request.actualYamlLocation.get.parentStack.last.hasSameValue(request.yamlLocation.get);
        }
    }

    override def suggest(request: ICompletionRequest): Future[ICompletionResponse] = {
        var result:ListBuffer[ISuggestion] = ListBuffer()
        var isYAML = request.config.astProvider.get.syntax == Syntax.YAML
        
        var astNode = request.astNode.get;
        val pm = astNode.astUnit.positionsMapper
        val line = pm.point(request.position).line
        val off = pm.lineOffset(pm.line(line).getOrElse("")) + 2
        if(isMethodKey(request)) {
            astNode.parent match {
                case Some(parent) => if(parent.definition.isAssignableFrom("Method")) {
                    parent.parent match {
                        case Some(resource) => if(resource.definition.isAssignableFrom("ResourceBase")) {
                            astNode = resource;
                        }
                        case _ =>;
                    }
                }
                
                case _ =>;
            }
        }
        if(astNode.isElement) {
            val definition = astNode.asElement.get.definition
            definition.allProperties.foreach(p => {
                var isEmbeddedInMaps = false
                p.getExtra(PropertySyntaxExtra).foreach(extra => {
                    isEmbeddedInMaps = extra.isEmbeddedInMaps
                })
                if (p.isMultiValue && !isEmbeddedInMaps) {
                    p.range.foreach(range => {
                        if (range.isArray && range.asInstanceOf[IArrayType].componentType.isDefined) {
                            val rangeComponentType = range.asInstanceOf[IArrayType].componentType.get
                            rangeComponentType.properties.foreach(p => {
                                p.getExtra(PropertySyntaxExtra).foreach(extra => {
                                    if (extra.isKey) {
                                        extra.enum.foreach(x => {
                                            var text = x.toString
                                            val description = p.getExtra(DescriptionExtra).map(_.text).getOrElse("")
                                            val suggestion = Suggestion(text, description, text, request.prefix)
                                            val ws = "\n" + (" " * off)
                                            suggestion.withTrailingWhitespace(ws)
                                            ProjectBuilder.determineFormat(request.astNode.get.astUnit.baseUnit).flatMap(SuggestionCategoryRegistry.getCategory(_, text, Option(definition), Option(rangeComponentType))).foreach(suggestion.withCategory)
                                            result += suggestion
                                        })
                                    }
                                })
                            })
                        }
                    })
                }
            })
        }
        else if (astNode.isAttr){
            astNode.property.flatMap(_.getExtra(PropertySyntaxExtra)).foreach(se => {
                var list:ListBuffer[Any] = ListBuffer()
                list ++= se.oftenValues
                list ++= se.enum
                val prop = astNode.property.get
                val parentProp = astNode.parent.flatMap(_.property)


                val description = prop.getExtra(DescriptionExtra).map(_.text).getOrElse("")
                val defOpt = astNode.parent.map(_.definition)
                val rangeOpt = prop.range
                if(parentProp.isDefined) {
                    val existing = astNode.parent.flatMap(_.parent).map(_.elements(parentProp.get.nameId.get).flatMap(_.attribute(prop.nameId.get).flatMap(_.value))).getOrElse(Seq())
                    list = list.filter(x => !existing.contains(x))
                }
                list.foreach(x => {
                    var text = x.toString
                    val suggestion = Suggestion(text, description, text, request.prefix)
                    val ws = "\n" + (" " * off)
                    suggestion.withTrailingWhitespace(ws)
                    ProjectBuilder.determineFormat(request.astNode.get.astUnit.baseUnit).flatMap(SuggestionCategoryRegistry.getCategory(_, text, defOpt, rangeOpt)).foreach(suggestion.withCategory)
                    result += suggestion
                })
            })
        }
        val response = CompletionResponse(result, LocationKind.KEY_COMPLETION, request)
        Promise.successful(response).future
    }
    
    def isMethodKey(request: ICompletionRequest): Boolean = request.astNode match {
        case Some(node) => node.property match {
            case Some(property) => property.domain match {
                case Some(domain) => domain.isAssignableFrom("Method");
                
                case _ => false;
            };
            
            case _ => false;
        };
        
        case _ => false;
    }
 }

object KnownKeyPropertyValuesCompletionPlugin {
    val ID = "known.key.property.values.completion";
    
    val supportedLanguages:List[Vendor] = List(Raml10, Oas, Oas20);

    def apply():KnownKeyPropertyValuesCompletionPlugin = new KnownKeyPropertyValuesCompletionPlugin();
}

