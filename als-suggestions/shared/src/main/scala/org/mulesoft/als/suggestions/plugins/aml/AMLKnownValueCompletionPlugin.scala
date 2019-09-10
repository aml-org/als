package org.mulesoft.als.suggestions.plugins.aml

import amf.core.metamodel.Field
import amf.core.metamodel.Type.ArrayLike
import amf.plugins.document.vocabularies.model.document.Dialect
import org.mulesoft.als.suggestions.{RawSuggestion, SuggestionOptions}
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.patched.{PatchedSuggestion, PatchedSuggestionsForDialect}

import scala.concurrent.Future

class AMLKnownValueCompletions(field: Field,
                               classTerm: String,
                               dialect: Dialect,
                               isKey: Boolean,
                               indentation: String,
                               inArray: Boolean) {

  private def getSuggestions: Seq[PatchedSuggestion] =
    PatchedSuggestionsForDialect
      .getKnownValues(dialect.id, classTerm, field.toString)

  def resolve(): Future[Seq[RawSuggestion]] =
    Future.successful({
      getSuggestions.map(
        s =>
          if (field.`type`.isInstanceOf[ArrayLike] && !inArray)
            RawSuggestion.valueInArray(s.text, indentation, s.description.getOrElse(s.text), "unknown", isKey)
          else
            RawSuggestion(s.text,
                          s.text,
                          s.description.getOrElse(s.text),
                          Seq(),
                          indentation,
                          "unknown",
                          None,
                          SuggestionOptions(isKey = isKey)))
    })
}

object AMLKnownValueCompletionPlugin extends AMLCompletionPlugin {
  override def id = "AMLKnownValueCompletionPlugin"

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    params.fieldEntry match {
      case Some(fe)
          if params.yPartBranch.isKey || params.propertyMapping
            .exists(_.nodePropertyMapping().value() == fe.field.value.iri()) =>
        new AMLKnownValueCompletions(
          fe.field,
          params.amfObject.meta.`type`.head.iri(),
          params.actualDialect,
          params.yPartBranch.isKey,
          params.indentation,
          params.yPartBranch.isArray || params.yPartBranch.isInArray
        ).resolve()
      case _ => emptySuggestion
    }
  }
}
