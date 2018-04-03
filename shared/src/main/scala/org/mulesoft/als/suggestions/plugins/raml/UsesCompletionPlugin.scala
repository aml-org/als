package org.mulesoft.als.suggestions.plugins.raml

import amf.core.remote.{Raml10, Vendor}
import org.mulesoft.als.suggestions.implementation.{PathCompletion, Suggestion}
import org.mulesoft.als.suggestions.interfaces.{ICompletionPlugin, ICompletionRequest, ISuggestion}
import org.yaml.model.YScalar
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

class UsesCompletionPlugin extends ICompletionPlugin {

  override def id: String = UsesCompletionPlugin.ID;

  override def languages: Seq[Vendor] = UsesCompletionPlugin.supportedLanguages;

  override def isApplicable(request:ICompletionRequest): Boolean = request.config.astProvider match {
    case Some(astProvider) => languages.indexOf(astProvider.language) >= 0 && isUses(request);

    case _ => false;
  }

  override def suggest(request: ICompletionRequest): Future[Seq[ISuggestion]] = {

    val baseDir = request.astNode.get.astUnit.project.rootPath

    PathCompletion.complete(baseDir, request.prefix, request.config.fsProvider.get)
      .map(paths=>{
        paths.map(path=>Suggestion(path, id, path, request.prefix))
      })
  }

  def isUses(request: ICompletionRequest): Boolean = {
    request.astNode.get.isElement && request.astNode.get.asElement.get.definition.isAssignableFrom("uses");
  }
}

object UsesCompletionPlugin {
  val ID = "uses.completion";

  val supportedLanguages: List[Vendor] = List(Raml10);

  def apply(): UsesCompletionPlugin = new UsesCompletionPlugin();
}