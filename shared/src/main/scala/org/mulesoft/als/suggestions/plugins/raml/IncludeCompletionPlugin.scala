package org.mulesoft.als.suggestions.plugins.raml

import amf.core.remote.{Raml08, Raml10, Vendor}
import org.mulesoft.als.suggestions.implementation.{CompletionResponse, PathCompletion, Suggestion}
import org.mulesoft.als.suggestions.interfaces._
import org.yaml.model.{YNode, YScalar, YType}

import scala.concurrent.{Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global

class IncludeCompletionPlugin extends ICompletionPlugin {

  override def id: String = IncludeCompletionPlugin.ID;

  override def languages: Seq[Vendor] = IncludeCompletionPlugin.supportedLanguages;

  override def isApplicable(request:ICompletionRequest): Boolean = request.config.astProvider match {

    case Some(astProvider) => languages.indexOf(astProvider.language) >= 0 &&
      isInInclude(request);

    case _ => false;
  }

  override def suggest(request: ICompletionRequest): Future[ICompletionResponse] = {

    val baseDir = request.astNode.get.astUnit.project.rootPath

    val relativePath = request.actualYamlLocation.get.value.get.yPart.asInstanceOf[YScalar].text

    if (!relativePath.endsWith(request.prefix)) {
      var response = CompletionResponse(LocationKind.VALUE_COMPLETION, request)
      Promise.successful(response).future
    } else {

      val diff = relativePath.length - request.prefix.length

      PathCompletion.complete(baseDir, relativePath, request.config.fsProvider.get)
        .map(paths=>{
          var suggestions = paths.map(path=>{

            val pathStartingWithPrefix = if(diff != 0) path.substring(diff) else path

            Suggestion(pathStartingWithPrefix, "File path",
              pathStartingWithPrefix, request.prefix)
          })
          CompletionResponse(suggestions,LocationKind.VALUE_COMPLETION, request)
        })
    }
  }

  def isInInclude(request: ICompletionRequest): Boolean = {

    if(request.actualYamlLocation.isEmpty){
        false
    }
    else if(request.actualYamlLocation.get.node.isEmpty){
        false
    }
    else if(!request.actualYamlLocation.get.node.get.yPart.isInstanceOf[YNode]){
        false
    }
    else{

        if(request.actualYamlLocation.get.value.isEmpty){
            false
        }
        else if(!request.actualYamlLocation.get.value.get.yPart.isInstanceOf[YScalar]){
            false
        }
        else {
            var nodePart = request.actualYamlLocation.get.node.get.yPart.asInstanceOf[YNode]
            var valuePart = request.actualYamlLocation.get.value.get.yPart.asInstanceOf[YScalar]
            val tagText = nodePart.tag.text
            val valueString = Option(valuePart.value).map(_.toString).getOrElse("")
            if (tagText != "!include" && !valueString.startsWith("!include")) {
                false
            }
            else {
                true
            }
        }
    }
  }
}

object IncludeCompletionPlugin {

  val ID = "include.completion";

  val supportedLanguages: List[Vendor] = List(Raml08, Raml10);

  def apply(): IncludeCompletionPlugin = new IncludeCompletionPlugin();
}