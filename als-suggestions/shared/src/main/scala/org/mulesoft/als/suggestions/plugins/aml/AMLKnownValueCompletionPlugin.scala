package org.mulesoft.als.suggestions.plugins.aml

import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.patched.{PatchedSuggestion, PatchedSuggestionsForDialect}
import org.mulesoft.als.suggestions.{AMLCompletionParams, RawSuggestion}

import scala.concurrent.Future

class AMLKnownValueCompletionPlugin(params: AMLCompletionParams) extends AMLSuggestionsHelper {

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

  override def resolve(params: AMLCompletionParams): Future[Seq[RawSuggestion]] = {
    new AMLKnownValueCompletionPlugin(params)
      .resolve()
  }
}
