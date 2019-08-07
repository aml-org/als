package org.mulesoft.als.suggestions.plugins.aml

import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.{AMLCompletionPlugin, CompletionPlugin}
import org.mulesoft.als.suggestions.plugins.aml.patched.{PatchedSuggestion, PatchedSuggestionsForDialect}

import scala.concurrent.Future

class AMLKnownValueCompletions(params: AmlCompletionRequest) extends AMLSuggestionsHelper {

  private def getSuggestions: Seq[PatchedSuggestion] =
    params.fieldEntry
      .flatMap(
        fe =>
          params.amfObject.meta.`type`.headOption.map(classTerm =>
            PatchedSuggestionsForDialect
              .getKnownValues(classTerm.ns.base + classTerm.name, fe.field.toString)))
      .getOrElse(Nil)

  def resolve(): Future[Seq[RawSuggestion]] =
    Future.successful({
      val whitespaces =
        s"\n${getIndentation(params.baseUnit, params.position)}"
      getSuggestions.map(s =>
        // TODO: isKey is not consistent due to ContentPatch: review if field is Scalar?
        RawSuggestion(s.text, s.text, s.description.getOrElse(s.text), Seq(), params.yPartBranch.isKey, whitespaces))
    })
}

object AMLKnownValueCompletionPlugin extends AMLCompletionPlugin {
  override def id = "AMLKnownValueCompletionPlugin"

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    new AMLKnownValueCompletions(params)
      .resolve()
  }
}
