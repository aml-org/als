package org.mulesoft.als.suggestions.plugins.raml

import amf.core.remote.{Raml10, Vendor}
import org.mulesoft.als.suggestions.implementation.{PathCompletion, Suggestion}
import org.mulesoft.als.suggestions.interfaces.{ICompletionPlugin, ICompletionRequest, ISuggestion}
import org.yaml.model.YScalar

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class IncludeCompletionPlugin extends ICompletionPlugin {

  override def id: String = IncludeCompletionPlugin.ID;

  override def languages: Seq[Vendor] = IncludeCompletionPlugin.supportedLanguages;

  override def isApplicable(request:ICompletionRequest): Boolean = request.config.astProvider match {

    case Some(astProvider) => languages.indexOf(astProvider.language) >= 0 &&
      isInInclude(request);

    case _ => false;
  }

  override def suggest(request: ICompletionRequest): Future[Seq[ISuggestion]] = {

    val baseDir = request.astNode.get.astUnit.project.rootPath


    val prefixPath = request.prefix.substring("!include".length + 1)

    PathCompletion.complete(baseDir, prefixPath, request.config.fsProvider.get)
      .map(paths=>{
        paths.map(path=>Suggestion(path, id, path, request.prefix))
      })
  }

  def isInInclude(request: ICompletionRequest): Boolean = {
    if(request.actualYamlLocation.get == null) {
      return false;
    }

    if(request.actualYamlLocation.get.value.get == null) {
      return false;
    }

    val valueText =
      request.actualYamlLocation.get.value.get.yPart.asInstanceOf[YScalar].text

    valueText.startsWith("!include")
  }
}

object IncludeCompletionPlugin {

  val ID = "include.completion";

  val supportedLanguages: List[Vendor] = List(Raml10);

  def apply(): IncludeCompletionPlugin = new IncludeCompletionPlugin();
}