package org.mulesoft.als.suggestions.plugins.raml

import amf.core.remote.{Raml08, Raml10, Vendor}
import amf.plugins.domain.shapes.models.AnyShape
import amf.plugins.domain.webapi.models.Payload
import org.mulesoft.als.suggestions.implementation.{CompletionResponse, PathCompletion, Suggestion}
import org.mulesoft.als.suggestions.interfaces._
import org.yaml.model.{YMap, YScalar}

import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}

class BodyCompletionPlugin extends ICompletionPlugin {

    override def id: String = BodyCompletionPlugin.ID;

    override def languages: Seq[Vendor] = BodyCompletionPlugin.supportedLanguages;

    override def isApplicable(request: ICompletionRequest): Boolean = request.config.astProvider match {
        case Some(astProvider) => {
            val result = languages.indexOf(astProvider.language) >= 0 && isBody(request)
            result
        };

        case _ => false;
    }

    override def suggest(request: ICompletionRequest): Future[ICompletionResponse] = {

        var result: Seq[Suggestion] = Nil
        val actualYamlLocation = request.actualYamlLocation.get
        if(actualYamlLocation.parentStack.lengthCompare(3)>=0) {
            var existing: Seq[String] = actualYamlLocation.parentStack(2).value.get.yPart match {
                case map: YMap => map.entries.map(e => e.key.value.asInstanceOf[YScalar].value.toString)
                case _ => Seq()
            }

            val element = request.astNode.get.asElement.get

            val isResponseBody = element.definition.isAssignableFrom("Response") || element.parent.flatMap(_.asElement).exists(_.definition.isAssignableFrom("Response"))
            var list = ListBuffer[String](
                "application/json",
                "application/xml"
            )
            if (!isResponseBody) {
                list += "multipart/formdata"
                list += "application/x-www-form-urlencoded"
            }
            result = list.filter(!existing.contains(_)).map(x => Suggestion(x + ":", id,
                x, request.prefix))
        }
        else {
            actualYamlLocation.keyValue match {
                case Some(v) => v.yPart match {
                    case sc:YScalar =>
                        if(sc.value.toString == "body"){
                            result = TypeReferencesCompletionPlugin.typeSuggestions(request, id)
                        }
                    case _ =>
                }
                case _ =>
            }
        }
        var response = CompletionResponse(result, LocationKind.VALUE_COMPLETION, request)
        Promise.successful(response).future

    }

    def isBody(request: ICompletionRequest): Boolean = {

        val isElement = request.astNode.isDefined && request.astNode.get.isElement
        if(!isElement){
            false
        }
        else {
            request.astNode.get.amfNode match {
                case pl:Payload =>
                    if(pl.fields.size != 1){
                        false
                    }
                    else if(Option(pl.schema).isEmpty){
                        false
                    }
                    else if(!pl.schema.isInstanceOf[AnyShape]){
                        false
                    }
                    else if(pl.schema.fields.size != 1){
                        false
                    }
                    else {
                        Option(pl.schema.name).map(_.value()).contains("default")
                    }
                case _ =>
                    val isPayloadKey = isElement &&
                        (request.astNode.get.asElement.get.definition.isAssignableFrom("MethodBase")
                            ||request.astNode.get.asElement.get.definition.isAssignableFrom("Response")) &&
                        request.actualYamlLocation.isDefined &&
                        request.actualYamlLocation.get.parentStack.length >= 3 &&
                        request.actualYamlLocation.get.parentStack(2).keyValue.isDefined &&
                        request.actualYamlLocation.get.parentStack(2).keyValue.get.yPart.isInstanceOf[YScalar] &&
                        request.actualYamlLocation.get.parentStack(2).keyValue.get.yPart.asInstanceOf[YScalar].text == "body" &&
                        request.actualYamlLocation.get.value.isDefined &&
                        request.actualYamlLocation.get.value.get.yPart.isInstanceOf[YScalar]
                    isPayloadKey
            }
        }
    }
}

object BodyCompletionPlugin {
    val ID = "body.completion";

    val supportedLanguages: List[Vendor] = List(Raml08,Raml10);

    def apply(): BodyCompletionPlugin = new BodyCompletionPlugin();
}

