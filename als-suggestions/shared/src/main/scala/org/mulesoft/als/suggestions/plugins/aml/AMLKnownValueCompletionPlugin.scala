package org.mulesoft.als.suggestions.plugins.aml

import amf.core.metamodel.Field
import amf.core.model.domain.AmfObject
import amf.core.parser.FieldEntry
import amf.plugins.document.vocabularies.model.document.Dialect
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.patched.{PatchedSuggestion, PatchedSuggestionsForDialect}

import scala.concurrent.Future

class AMLKnownValueCompletions(field: Field, classTerm: String, dialect: Dialect, isKey: Boolean, indentation: String) {

  private def getSuggestions: Seq[PatchedSuggestion] =
    PatchedSuggestionsForDialect
      .getKnownValues(dialect.id, classTerm, field.toString)

  def resolve(): Future[Seq[RawSuggestion]] =
    Future.successful({
      getSuggestions.map(s =>
        RawSuggestion(s.text, s.text, s.description.getOrElse(s.text), Seq(), isKey, indentation))
    })
}

object AMLKnownValueCompletionPlugin extends AMLCompletionPlugin {
  override def id = "AMLKnownValueCompletionPlugin"

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    params.fieldEntry match {
      case Some(fe) =>
        new AMLKnownValueCompletions(fe.field,
                                     params.amfObject.meta.`type`.head.iri(),
                                     params.actualDialect,
                                     params.yPartBranch.isKey,
                                     params.indentation)
          .resolve()
      case _ => emptySuggestion
    }
  }
}
