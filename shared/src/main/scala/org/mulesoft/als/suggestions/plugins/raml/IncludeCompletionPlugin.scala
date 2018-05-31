package org.mulesoft.als.suggestions.plugins.raml

import amf.core.remote.{Raml10, Vendor}
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

            Suggestion(pathStartingWithPrefix, id,
              pathStartingWithPrefix, request.prefix)
          })
          CompletionResponse(suggestions,LocationKind.VALUE_COMPLETION, request)
        })
    }
  }

  def isInInclude(request: ICompletionRequest): Boolean = {

    request.actualYamlLocation.isDefined &&
      request.actualYamlLocation.get.node.isDefined &&
      request.actualYamlLocation.get.node.get.yPart.isInstanceOf[YNode] &&
      request.actualYamlLocation.get.node.get.yPart.asInstanceOf[YNode].tagType == YType.Include &&
      request.actualYamlLocation.get.value.isDefined &&
      request.actualYamlLocation.get.value.get.yPart.isInstanceOf[YScalar]
  }
}

object IncludeCompletionPlugin {

  val ID = "include.completion";

  val supportedLanguages: List[Vendor] = List(Raml10);

  def apply(): IncludeCompletionPlugin = new IncludeCompletionPlugin();
}