package org.mulesoft.als.suggestions.plugins.raml

import amf.core.remote.{Oas, Raml10, Vendor}
import org.mulesoft.als.suggestions.implementation.{CompletionResponse, Suggestion}
import org.mulesoft.als.suggestions.interfaces._
import org.mulesoft.high.level.interfaces.IHighLevelNode
import org.mulesoft.typesystem.nominal_interfaces.IProperty
import org.mulesoft.typesystem.nominal_interfaces.extras.PropertySyntaxExtra
import org.mulesoft.high.level.Search

import scala.collection.mutable
import scala.concurrent.{Future, Promise}

class TemplateReferencesCompletionPlugin extends ICompletionPlugin {

    override def id: String = TemplateReferencesCompletionPlugin.ID

    override def languages: Seq[Vendor] = TemplateReferencesCompletionPlugin.supportedLanguages

    override def isApplicable(request:ICompletionRequest): Boolean = request.config.astProvider match {

        case Some(astProvider) =>
            languages.indexOf(astProvider.language)>=0
        case _ => false
    }

    override def suggest(request: ICompletionRequest): Future[ICompletionResponse] = {
			var paramFor = inParamOf(request).get;
		
			val result = request.astNode match {
				case Some(n) => if(n.isElement) {
					var actualPrefix = request.prefix;

					var squareBracketsRequired = false;

					var declarations = n.asElement.get.definition.nameId match {
						case Some("ResourceTypeRef")  => {
							Search.getDeclarations(n.astUnit, "ResourceType");
						}

						case Some("TraitRef")  => {
							squareBracketsRequired = true;

							Search.getDeclarations(n.astUnit, "Trait");
						}

						case _=> Seq();
					}

					if(paramFor != null) {
						var foundDeclarations = declarations.filter(declaration => paramFor == declaration.node.attribute("name").get.value.asInstanceOf[Some[String]].get);

						if(foundDeclarations.isEmpty) {
							Seq()
						}
                        else {

                            var declaration = foundDeclarations(0);

                            var paramNames = declaration.node.attributes("parameters").map(param => param.value.asInstanceOf[Some[String]].get);

                            if (actualPrefix.indexOf("{") >= 0) {
                                actualPrefix = actualPrefix.substring(actualPrefix.indexOf("{") + 1).trim();
                            }

                            paramNames.map(name => Suggestion(name, id, name, actualPrefix));
                        }
					}
                    else {

                        declarations.map(declaration => {
                            var declarationName = declaration.node.attribute("name").get.value.asInstanceOf[Some[String]].get

                            var bracketsRequired = request.prefix.indexOf("{") != 0;

                            squareBracketsRequired = squareBracketsRequired && request.prefix.indexOf("[") != 0;

                            var snippet = declarationName;

                            var params = declaration.node.attributes("parameters");

                            if (!params.isEmpty) {
                                snippet = snippet + ": {";

                                var count = 0;

                                params.foreach(param => {
                                    var needComma = param != params.last;

                                    snippet += param.value.asInstanceOf[Some[String]].get + " : ";

                                    if (needComma) {
                                        snippet += ", ";
                                    }
                                });

                                snippet += "}";
                            }

                            if (bracketsRequired) {
                                snippet = "{" + snippet + "}";
                            } else {
                                actualPrefix = actualPrefix.substring(actualPrefix.indexOf("{") + 1).trim();
                            }

                            if (squareBracketsRequired) {
                                snippet = "[" + snippet + "]";
                            } else {
                                actualPrefix = actualPrefix.substring(actualPrefix.indexOf("[") + 1).trim();
                            }

                            Suggestion(snippet, id, declarationName, actualPrefix);
                        });
                    }
				} else {
					Seq();
				}

				case _ => Seq();
			}

        var response = CompletionResponse(result,LocationKind.VALUE_COMPLETION,request)
        Promise.successful(response).future
    }
	
	def inParamOf(request: ICompletionRequest): Option[String] = {
		var text: String = request.config.editorStateProvider.get.getText;
		
		var currentPosition = request.position;
		
		var lineStart = text.substring(0, currentPosition).lastIndexOf("\n") + 1;
		
		if(lineStart < 0) {
			return Some(null);
		}
		
		var line = text.substring(lineStart, currentPosition);
		
		var openSquaresCount = line.count(_ == "{".charAt(0));
		
		if(openSquaresCount < 2) {
			return Some(null);
		}
		
		if(line.last == "{".charAt(0)) {
			line = line + " ";
		}
		
		var rightExps = line.split("\\{");
		
		var canContainReference = rightExps(rightExps.size - 2);
		
		var referenceParts = canContainReference.split(":");
		
		if(referenceParts.length != 2) {
			return Some(null);
		}
		
		Some(referenceParts(0));
	}
}

object TemplateReferencesCompletionPlugin {
	val ID = "templateRef.completion";
	
	val supportedLanguages: List[Vendor] = List(Raml10);
	
	def apply(): TemplateReferencesCompletionPlugin = new TemplateReferencesCompletionPlugin();
}