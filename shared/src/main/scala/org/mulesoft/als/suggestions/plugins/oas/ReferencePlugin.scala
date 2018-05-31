package org.mulesoft.als.suggestions.plugins.oas;

import amf.core.remote.{Oas, Oas2, Oas2Yaml, Vendor}
import org.mulesoft.als.suggestions.implementation.{CompletionResponse, Suggestion}
import org.mulesoft.als.suggestions.interfaces._
import org.mulesoft.high.level.interfaces.IHighLevelNode
import org.mulesoft.high.level.{Declaration, Search}
import org.mulesoft.typesystem.nominal_interfaces.IProperty
import org.mulesoft.typesystem.nominal_interfaces.extras.PropertySyntaxExtra

import scala.collection.mutable
import scala.concurrent.{Future, Promise}

abstract class ReferencePlugin extends ICompletionPlugin {

    override def languages: Seq[Vendor] = ReferencePlugin.supportedLanguages

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
        val result = request.astNode match {
            case Some(n) => if(n.isElement) {
                var element = n.asElement.get;

                if(isOASResponseReference(element)) {
                    var ds = Search.getDeclarations(element.astUnit, definitionClass);
                    
                    ds.map(declaration => {
                        var uri = oasDeclarationReference(declaration);
                        
                        var label = responseDeclarationName(declaration);
                        
                        Suggestion(wrapDeclarationReference(uri, request), id, label, request.prefix);
                    });
                } else {
					Seq();
                }
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
        var isJSON = request.config.astProvider.get.syntax == Syntax.JSON
        if(isJSON){
            "{ \"$ref\": \"" + reference + "\" }"
        }
        else {
            var indentation = "\n";

            for (i <- 0 until request.indentCount + 1) {
                indentation += request.currentIndent;
            }

            indentation + "$ref: \"" + reference + "\"";
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
	
	def isOASResponseReference(node:IHighLevelNode): Boolean = {
        if(node.definition.universe.name.get != "OAS") {
            return false;
        }
        
        if(!node.definition.isAssignableFrom(targetClass)) {
            return false
        }
        
        true;
    }

    def targetClass:String
}

object ReferencePlugin {
    val supportedLanguages:List[Vendor] = List(Oas, Oas2, Oas2Yaml);
}

