package org.mulesoft.als.suggestions.plugins.raml

import org.mulesoft.als.suggestions.implementation.{CompletionResponse, PathCompletion, Suggestion}
import org.mulesoft.als.suggestions.interfaces.{
  ICompletionPlugin,
  ICompletionRequest,
  ICompletionResponse,
  LocationKind
}
import org.yaml.model.{YNode, YScalar}

import scala.concurrent.{Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global

trait InclusionSuggestion extends ICompletionPlugin {

  protected val description: String
  override def suggest(request: ICompletionRequest): Future[ICompletionResponse] = {

    val baseDir = request.astNode.get.astUnit.project.rootPath
    val relativePath = request.actualYamlLocation.get.node.get.yPart match {
      case node: YNode.MutRef =>
        node.origValue match {
          case scalar: YScalar => scalar.text;

          case _ => request.actualYamlLocation.get.value.get.yPart.asInstanceOf[YScalar].text;
        }

      case _ => request.actualYamlLocation.get.value.get.yPart.asInstanceOf[YScalar].text;
    }

    if (!relativePath.endsWith(request.prefix)) {
      val response = CompletionResponse(LocationKind.VALUE_COMPLETION, request)
      Promise.successful(response).future
    } else {

      PathCompletion
        .complete(baseDir, relativePath, request.config.platform)
        .map(paths => {
          val suggestions = paths.map(path => {

            val pathStartingWithPrefix = decorate({
              relativePath.split("/").lastOption match {
                case Some(last) => request.prefix + path.stripPrefix(last)
                case _          => path.stripPrefix(relativePath)
              }
            }, request.prefix)

            Suggestion(pathStartingWithPrefix, description, pathStartingWithPrefix, request.prefix)
          })
          CompletionResponse(suggestions, LocationKind.VALUE_COMPLETION, request)
        })
    }
  }

  protected def decorate(path: String, prefix: String): String = path

  override def isApplicable(request: ICompletionRequest): Boolean = request.config.astProvider match {

    case Some(astProvider) =>
      languages.indexOf(astProvider.language) >= 0 && isRightTypeInclusion(request);

    case _ => false;
  }
  protected def isRightTypeInclusion(request: ICompletionRequest): Boolean
}
