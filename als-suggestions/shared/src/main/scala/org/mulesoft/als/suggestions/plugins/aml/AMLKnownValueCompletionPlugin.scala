package org.mulesoft.als.suggestions.plugins.aml

import amf.aml.client.scala.model.document.Dialect
import amf.apicontract.internal.metamodel.domain.ResponseModel
import amf.core.internal.metamodel.Field
import amf.core.internal.metamodel.Type.{ArrayLike, Scalar}
import org.mulesoft.als.suggestions._
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.patched.{PatchedSuggestion, PatchedSuggestionsForDialect}
import org.mulesoft.amfintegration.AmfImplicits._
import org.mulesoft.amfintegration.dialect.dialects.oas.OAS30Dialect

import scala.concurrent.Future

sealed class AMLKnownValueCompletions(
    field: Field,
    classTerm: String,
    dialect: Dialect,
    isKey: Boolean,
    inArray: Boolean,
    obj: Boolean
) {

  private def getSuggestions: Seq[PatchedSuggestion] =
    PatchedSuggestionsForDialect
      .getKnownValues(dialect.id, classTerm, field.toString)

  def resolve(): Future[Seq[RawSuggestion]] =
    Future.successful({
      getSuggestions.map(s =>
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
            SuggestionStructure(
              rangeKind = fieldRange(s),
              isKey = isKey && !inArray,
              keyRange(s.text),
              nonPlain = s.nonPlain
            )
          )
      )
    })

  def keyRange(input: String): ScalarRange = {
    if (
      (field == ResponseModel.StatusCode || field == ResponseModel.Name) && input.forall(
        _.isDigit
      ) && dialect.id != OAS30Dialect.dialect.id
    ) // hack for oas 3.0 status codes
      NumberScalarRange
    else StringScalarRange
  }

  def fieldRange(s: PatchedSuggestion): RangeKind = {
    if (s.isObj || obj) ObjectRange
    else if (inArray) StringScalarRange
    else
      field.`type` match {
        case _: Scalar                => StringScalarRange
        case _: ArrayLike if !inArray => ArrayRange
        case _                        => ObjectRange
      }
  }

}

trait AbstractKnownValueCompletionPlugin extends AMLCompletionPlugin {
  override final def id = "KnownValueCompletionPlugin"

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] =
    params.fieldEntry match {
      case Some(fe)
          if params.yPartBranch.isKey || params.propertyMapping
            .exists(_.nodePropertyMapping().value() == fe.field.value.iri()) =>
        innerResolver(params, fe.field, params.amfObject.metaURIs.head)
      case _ => emptySuggestion
    }

  protected final def innerResolver(
      params: AmlCompletionRequest,
      field: Field,
      classTerm: String
  ): Future[Seq[RawSuggestion]] =
    new AMLKnownValueCompletions(
      field,
      classTerm,
      params.actualDialect,
      params.yPartBranch.isKey,
      params.yPartBranch.isArray || (params.yPartBranch.isJson && params.yPartBranch.isInArray),
      params.isKeyMapping
    ).resolve()
}

object AMLKnownValueCompletionPlugin extends AbstractKnownValueCompletionPlugin
