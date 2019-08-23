package org.mulesoft.als.suggestions.plugins.aml

import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.{AMLCompletionParams, RawSuggestion}

import scala.concurrent.Future

class AMLEnumCompletionsPlugin(params: AMLCompletionParams) extends AMLSuggestionsHelper {

  private def getSuggestions: Seq[String] =
    params.propertyMappings.headOption
      .map(_.enum()
        .flatMap(_.option().map(_.toString)))
      .getOrElse(Nil)

  def resolve(): Future[Seq[RawSuggestion]] =
    Future.successful(
      getSuggestions
        .map(s => RawSuggestion(s, isAKey = false)))
}

object AMLEnumCompletionPlugin extends AMLCompletionPlugin {
  override def id = "AMLEnumCompletionPlugin"

  override def resolve(params: AMLCompletionParams): Future[Seq[RawSuggestion]] =
    if (params.yPartBranch.isValue || params.yPartBranch.isInArray)
      new AMLEnumCompletionsPlugin(params).resolve()
    else emptySuggestion
}
