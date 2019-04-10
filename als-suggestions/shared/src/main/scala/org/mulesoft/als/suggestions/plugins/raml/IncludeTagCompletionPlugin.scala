package org.mulesoft.als.suggestions.plugins.raml

import amf.core.remote.{Raml08, Raml10, Vendor}
import org.mulesoft.als.suggestions.implementation.{CompletionResponse, Suggestion}
import org.mulesoft.als.suggestions.interfaces._

import scala.concurrent.{Future, Promise}

class IncludeTagCompletionPlugin extends ICompletionPlugin {

  override def id: String = IncludeTagCompletionPlugin.ID

  override def languages: Seq[Vendor] = IncludeTagCompletionPlugin.supportedLanguages

  override def isApplicable(request: ICompletionRequest): Boolean =
    !((request.config.astProvider.isEmpty) ||
      (languages.indexOf(request.config.astProvider.get.language) < 0) ||
      (request.kind == LocationKind.KEY_COMPLETION) ||
      (!Option(request.prefix).exists(_.startsWith("!"))) ||
      (request.actualYamlLocation.exists(_.inKey(request.position))))

  override def suggest(request: ICompletionRequest): Future[ICompletionResponse] = {
    val suggestions = List(Suggestion("!include", "'!include' tag", "!include", request.prefix))
    val response    = CompletionResponse(suggestions, LocationKind.VALUE_COMPLETION, request)
    Promise.successful(response).future
  }
}

object IncludeTagCompletionPlugin {
  val ID = "include.tag.completion"

  val supportedLanguages: List[Vendor] = List(Raml08, Raml10)

  def apply(): IncludeTagCompletionPlugin = new IncludeTagCompletionPlugin()
}
