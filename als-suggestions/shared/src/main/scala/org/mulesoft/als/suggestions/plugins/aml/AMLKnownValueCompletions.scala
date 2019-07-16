package org.mulesoft.als.suggestions.plugins.aml

import org.mulesoft.als.suggestions.interfaces.{CompletionParams, CompletionPlugin, RawSuggestion}
import org.mulesoft.als.suggestions.plugins.aml.patched.{PatchedSuggestion, PatchedSuggestionsForDialect}
import org.mulesoft.lsp.edit.TextEdit

import scala.concurrent.Future

class AMLKnownValueCompletions(params: CompletionParams) extends AMLSuggestionsHelper {

  private def getSuggestions: Seq[PatchedSuggestion] =
    params.fieldEntry
      .flatMap(
        fe =>
          params.amfObject.meta.`type`.headOption.map(classTerm =>
            PatchedSuggestionsForDialect
              .getKnownValues(classTerm.ns.base + classTerm.name, fe.field.toString)))
      .getOrElse(Nil)

  def resolve(): Future[Seq[RawSuggestion]] =
    Future.successful(getSuggestions.map(s =>
      new RawSuggestion {
        override def newText: String = s.text

        override def displayText: String = s.text

        override def description: String = s.description.getOrElse(s.text)

        override def textEdits: Seq[TextEdit] = Seq()

        override def whiteSpacesEnding: String = ""
    }))
}

object AMLKnownValueCompletions extends CompletionPlugin {
  override def id = "AMLKnownValueCompletions"

  override def resolve(params: CompletionParams): Future[Seq[RawSuggestion]] = {

    new AMLKnownValueCompletions(params).resolve()
  }
}
