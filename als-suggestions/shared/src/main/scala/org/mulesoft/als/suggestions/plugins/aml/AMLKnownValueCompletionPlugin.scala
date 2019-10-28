package org.mulesoft.als.suggestions.plugins.aml

import amf.core.metamodel.Field
import amf.core.metamodel.Type.{ArrayLike, Scalar}
import amf.plugins.document.vocabularies.model.document.Dialect
import amf.plugins.domain.webapi.metamodel.{OperationModel, RequestModel, ResponseModel}
import org.mulesoft.als.suggestions._
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.patched.{PatchedSuggestion, PatchedSuggestionsForDialect}

import scala.concurrent.Future

class AMLKnownValueCompletions(field: Field, classTerm: String, dialect: Dialect, isKey: Boolean, inArray: Boolean) {

  private def getSuggestions: Seq[PatchedSuggestion] =
    PatchedSuggestionsForDialect
      .getKnownValues(dialect.id, classTerm, field.toString)

  def resolve(): Future[Seq[RawSuggestion]] =
    Future.successful({
      getSuggestions.map(
        s =>
          if (field.`type`.isInstanceOf[ArrayLike] && !inArray)
            RawSuggestion.valueInArray(s.text, s.description.getOrElse(s.text), "unknown", isKey)
          else
            RawSuggestion(
              s.text,
              s.text,
              s.description.getOrElse(s.text),
              Seq(),
              "unknown",
              None,
              SuggestionStructure(rangeKind = fieldRange(s), isKey = isKey && !inArray, keyRange(s.text))
          ))
    })

  def keyRange(input: String): ScalarRange = {
    if ((field == ResponseModel.StatusCode || field == ResponseModel.Name) && input.forall(_.isDigit))
      NumberScalarRange
    else StringScalarRange
  }

  def fieldRange(s: PatchedSuggestion): RangeKind = {
    if (s.isObj) ObjectRange
    else if (inArray) StringScalarRange
    else
      field.`type` match {
        case _: Scalar                => StringScalarRange
        case _: ArrayLike if !inArray => ArrayRange
        case _                        => ObjectRange
      }
  }

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
          params.yPartBranch.isArray || params.yPartBranch.isInArray
        ).resolve()
      case _ => emptySuggestion
    }
  }
}
