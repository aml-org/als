package org.mulesoft.als.suggestions.plugins.raml

import amf.core.remote.{Oas, Raml10, Vendor}
import org.mulesoft.als.suggestions.implementation.{CompletionResponse, Suggestion}
import org.mulesoft.als.suggestions.interfaces._
import org.mulesoft.als.suggestions.plugins.KnownPropertyValuesCompletionPlugin
import org.mulesoft.high.level.interfaces.IHighLevelNode
import org.mulesoft.typesystem.nominal_interfaces.IProperty
import org.mulesoft.typesystem.nominal_interfaces.extras.PropertySyntaxExtra
import org.mulesoft.high.level.{Declaration, Search}
import org.mulesoft.positioning.IPositionsMapper
import org.mulesoft.typesystem.syaml.to.json.{YPoint, YRange}
import org.yaml.model._

import scala.collection.mutable
import scala.concurrent.{Future, Promise}

class SecurityReferencesCompletionPlugin extends ICompletionPlugin {

    override def id: String = SecurityReferencesCompletionPlugin.ID

    override def languages: Seq[Vendor] = SecurityReferencesCompletionPlugin.supportedLanguages

    override def isApplicable(request:ICompletionRequest): Boolean = request.config.astProvider match {

        case Some(astProvider) =>
            languages.indexOf(astProvider.language)>=0
        case _ => false
    }

    override def suggest(request: ICompletionRequest): Future[ICompletionResponse] = {



        var paramFor = inParamOf(request).get;

        val result:Seq[ISuggestion] = request.astNode match {
            case Some(n) => if (n.isElement) {

                val e = n.asElement.get
                var owner = e
                var prop:Option[IProperty] = None
                if(e.definition.isAssignableFrom("SecuritySchemeRef")){
                    owner = e.parent.get
                    prop = owner.definition.property("securedBy")
                }
                else if(request.actualYamlLocation.flatMap(_.keyValue).map(_.yPart.toString).contains("securedBy")){
                    prop = owner.definition.property("securedBy")
                }
                if(prop.isEmpty){
                    Seq()
                }
                else {
                    var usedNames = owner.elements(prop.get.nameId.get).flatMap(_.asElement).flatMap(_.attribute("name").map(_.value).map(_.toString))

                    var declarations = Search.getDeclarations(n.astUnit, "AbstractSecurityScheme")
                    declarations = declarations.filter(x => {
                        var nameOpt = x.node.attribute("name").map(_.value).map(name => {
                            if (x.namespace.isDefined) {
                                s"${x.namespace}.$name"
                            }
                            else {
                                name.toString
                            }
                        })
                        nameOpt.isDefined && !usedNames.contains(nameOpt.get)
                    })

                    if (paramFor != null) {
                        Seq();
                    }
                    else {
                        val isSequence = KnownPropertyValuesCompletionPlugin.isSequence(owner, prop.get.nameId.get)
                        var resultingText = declarations.map(decl => {
                            val declNode = decl.node
                            var nameOpt = declNode.attribute("name").flatMap(_.value).map(_.toString)
                            val plainName = nameOpt.get
                            val nsOpt = decl.namespace
                            val name = if (nsOpt.isDefined) s"${nsOpt.get}.$plainName" else plainName
                            name
                        })
                        if (isSequence) {
                            resultingText.map(x => Suggestion(x, id, x, request.prefix))
                        }
                        else {
                            resultingText.map(x => Suggestion(s"[ $x ]", id, x, request.prefix))
                        }
                    }
                }
            } else {
                Seq();
            }

            case _ => Seq();
        }

        var response = CompletionResponse(result, LocationKind.VALUE_COMPLETION, request)
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

object SecurityReferencesCompletionPlugin {
	val ID = "securityRef.completion";
	
	val supportedLanguages: List[Vendor] = List(Raml10);
	
	def apply(): SecurityReferencesCompletionPlugin = new SecurityReferencesCompletionPlugin();
}