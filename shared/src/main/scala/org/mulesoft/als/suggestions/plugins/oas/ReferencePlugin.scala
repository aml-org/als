package org.mulesoft.als.suggestions.plugins.oas;

import amf.core.remote.{Oas, Oas20, Vendor}
import org.mulesoft.als.suggestions.implementation.{CompletionResponse, Suggestion}
import org.mulesoft.als.suggestions.interfaces._
import org.mulesoft.high.level.interfaces.IHighLevelNode
import org.mulesoft.high.level.{Declaration, Search}
import org.mulesoft.typesystem.nominal_interfaces.IProperty
import org.mulesoft.typesystem.nominal_interfaces.extras.PropertySyntaxExtra
import org.yaml.model.{DoubleQuoteMark, YScalar}

import scala.collection.mutable
import scala.concurrent.{Future, Promise}

abstract class ReferencePlugin extends ICompletionPlugin {

    override def languages: Seq[Vendor] = ReferencePlugin.supportedLanguages

    def alwaysSequence:Boolean

    def restrictedClasses:Seq[String]

    override def isApplicable(request:ICompletionRequest): Boolean = request.config.astProvider match {
        case Some(astProvider) => if(languages.indexOf(astProvider.language) < 0) {
			false;
		} else {
			request.actualYamlLocation match {
				case Some(l) => l.inKey(request.position) || request.yamlLocation.get.hasSameValue(l);
				case _ => false;
			}
		}
		
		case _ => false
    }
	
    override def suggest(request: ICompletionRequest): Future[ICompletionResponse] = {
        var isAttr = false
        var elementOpt:Option[IHighLevelNode] = request.astNode match {
            case Some(n) => if(n.isElement) {
                n.asElement
            }
            else if(n.isAttr){
                if(n.property.flatMap(_.nameId).contains("$ref")) {
                    isAttr = true
                }
                n.parent
            }
            else {
                None
            }
            case _ => None
        }
        val result = elementOpt match {
            case Some(element) =>
                if(hasTargetClass(element)) {
                    var ds = Search.getDeclarations(element.astUnit, definitionClass);

                    ds.map(declaration => {
                        var uri = oasDeclarationReference(declaration);
                        val text = if(isAttr){
                            if(request.prefix.startsWith("/") && uri.startsWith("#")){
                                uri = uri.substring(1)
                                uri
                            }
                            else {
                                val needQuotes = request.actualYamlLocation.flatMap(_.value).map(_.yPart) match {
                                    case Some(l) => l match {
                                        case sc:YScalar => {
                                            sc.mark != DoubleQuoteMark
                                        }
                                        case _ => false
                                    }
                                    case _ => false
                                }
                                if(needQuotes) {
                                    "\"" + uri + "\""
                                }
                                else {
                                    uri
                                }
                            }
                        } else {
                            wrapDeclarationReference(uri, request)
                        }
                        Suggestion(text, id, uri, request.prefix);
                    });
                } else {
					Seq();
                }
            case _ => Seq();
        }

        val response = CompletionResponse(result, LocationKind.VALUE_COMPLETION, request)
        Promise.successful(response).future
    }

    def definitionClass:String

    def wrapDeclarationReference(reference: String, request: ICompletionRequest): String = {
        var element = request.astNode.flatMap(n=>{
            if(n.isElement){
                n.asElement
            }
            else if(n.isAttr){
                n.parent
            }
            else {
                None
            }
        })
        var isJSON = request.config.astProvider.get.syntax == Syntax.JSON
        if(isJSON){
            "{ \"$ref\": \"" + reference + "\" }"
        }
        else {
            var keyLine = element.flatMap(_.sourceInfo.ranges.headOption).map(_.start.line).getOrElse(-1)
            var line = request.astNode.get.astUnit.positionsMapper.point(request.position).line
            var indentation = ""
            if(!alwaysSequence && line == keyLine) {
                indentation = "\n";
                for (i <- 0 until request.indentCount + 1) {
                    indentation += request.currentIndent;
                }
            }
            indentation + "\"$ref\": \"" + reference + "\"";
        }
    }

    def oasDeclarationReference(declaration: Declaration): String = {
        var declarationName = declaration.node.attribute("key").get.value.get;

		var propertyName = declaration.node.property.get.nameId.get;

        "#/" + propertyName + "/" + declarationName;
    }

    def responseDeclarationName(declaration: Declaration): String = {
        declaration.node.attribute("key").get.value.get.asInstanceOf[String];
    }

	def hasTargetClass(node:IHighLevelNode): Boolean = {
        if(node.definition.universe.name.get != "OAS") {
            return false;
        }
        if(restrictedClasses.exists(node.definition.isAssignableFrom)){
            return false
        }
        if(!node.definition.isAssignableFrom(targetClass)) {
            return false
        }
        
        true;
    }

    def targetClass:String
}

object ReferencePlugin {
    val supportedLanguages:List[Vendor] = List(Oas, Oas20);
}

