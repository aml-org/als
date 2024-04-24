package org.mulesoft.als.suggestions.plugins.aml.patched

import amf.apicontract.internal.metamodel.domain.api.WebApiModel
import amf.apicontract.internal.metamodel.domain.{OperationModel, ParameterModel, PayloadModel, ResponseModel}
import amf.shapes.internal.domain.metamodel.ExampleModel
import org.mulesoft.als.suggestions.plugins.aml.webapi._
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.AsyncApi20Dialect
import org.mulesoft.amfintegration.dialect.dialects.asyncapi26.AsyncApi26Dialect
import org.mulesoft.amfintegration.dialect.dialects.oas.{OAS20Dialect, OAS30Dialect}
import org.mulesoft.amfintegration.dialect.dialects.raml.raml08.Raml08TypesDialect
import org.mulesoft.amfintegration.dialect.dialects.raml.raml10.Raml10TypesDialect

/** @param nonPlain
  *   nonPlain suggestions will have double quotes added to them
  */
case class PatchedSuggestion(
    text: String,
    description: Option[String] = None,
    isObj: Boolean = false,
    nonPlain: Boolean = false
)

case class FieldForClass(classTerm: String, propertyTerm: String)

object PatchedSuggestionsForDialect {

  private val webApiClasses: Map[FieldForClass, Map[String, Seq[PatchedSuggestion]]] =
    Map(
      FieldForClass(ParameterModel.`type`.head.iri(), ParameterModel.Name.value.iri()) ->
        Map("KnownValues" -> CommonHeadersValues.all.map(PatchedSuggestion(_)))
    )

  private val oas20Classes: Map[FieldForClass, Map[String, Seq[PatchedSuggestion]]] =
    Map(
      FieldForClass(WebApiModel.`type`.head.iri(), WebApiModel.Accepts.value.iri()) ->
        Map("KnownValues" -> Oas20CommonMediaTypes.all.map(PatchedSuggestion(_))),
      FieldForClass(WebApiModel.`type`.head.iri(), WebApiModel.ContentType.value.iri()) ->
        Map("KnownValues" -> Oas20CommonMediaTypes.all.map(PatchedSuggestion(_))),
      FieldForClass(OperationModel.`type`.head.iri(), OperationModel.ContentType.value.iri()) ->
        Map("KnownValues" -> Oas20CommonMediaTypes.all.map(PatchedSuggestion(_))),
      FieldForClass(OperationModel.`type`.head.iri(), OperationModel.Accepts.value.iri()) ->
        Map("KnownValues" -> Oas20CommonMediaTypes.all.map(PatchedSuggestion(_))),
      FieldForClass(ExampleModel.`type`.head.iri(), ExampleModel.MediaType.value.iri()) ->
        Map("KnownValues" -> Oas20CommonMediaTypes.all.map(PatchedSuggestion(_))),
      FieldForClass(PayloadModel.`type`.head.iri(), PayloadModel.MediaType.value.iri()) ->
        Map("KnownValues" -> Oas20CommonMediaTypes.all.map(PatchedSuggestion(_))),
      FieldForClass(ResponseModel.`type`.head.iri(), ResponseModel.StatusCode.value.iri()) ->
        Map("KnownValues" -> OasResponseCodes.all.map(PatchedSuggestion(_, isObj = true))),
      FieldForClass(ResponseModel.`type`.head.iri(), ResponseModel.Name.value.iri()) ->
        Map("KnownValues" -> OasResponseCodes.all.map(PatchedSuggestion(_, isObj = true)))
    ) ++ webApiClasses

  private val oas30Classes: Map[FieldForClass, Map[String, Seq[PatchedSuggestion]]] =
    Map(
      FieldForClass(WebApiModel.`type`.head.iri(), WebApiModel.Accepts.value.iri()) ->
        Map("KnownValues" -> Oas30CommonMediaTypes.all.map(PatchedSuggestion(_))),
      FieldForClass(WebApiModel.`type`.head.iri(), WebApiModel.ContentType.value.iri()) ->
        Map("KnownValues" -> Oas30CommonMediaTypes.all.map(PatchedSuggestion(_))),
      FieldForClass(OperationModel.`type`.head.iri(), OperationModel.ContentType.value.iri()) ->
        Map("KnownValues" -> Oas30CommonMediaTypes.all.map(PatchedSuggestion(_))),
      FieldForClass(OperationModel.`type`.head.iri(), OperationModel.Accepts.value.iri()) ->
        Map("KnownValues" -> Oas30CommonMediaTypes.all.map(PatchedSuggestion(_))),
      FieldForClass(ExampleModel.`type`.head.iri(), ExampleModel.MediaType.value.iri()) ->
        Map("KnownValues" -> Oas30CommonMediaTypes.all.map(PatchedSuggestion(_))),
      FieldForClass(PayloadModel.`type`.head.iri(), PayloadModel.MediaType.value.iri()) ->
        Map("KnownValues" -> Oas30CommonMediaTypes.all.map(PatchedSuggestion(_))),
      FieldForClass(ResponseModel.`type`.head.iri(), ResponseModel.StatusCode.value.iri()) ->
        Map(
          "KnownValues" -> Oas30ResponseCodes.all.map(v =>
            PatchedSuggestion(v, isObj = true, nonPlain = !Oas30ResponseCodes.notQuoted.contains(v))
          )
        ),
      FieldForClass(ResponseModel.`type`.head.iri(), ResponseModel.Name.value.iri()) ->
        Map(
          "KnownValues" -> Oas30ResponseCodes.all.map(v =>
            PatchedSuggestion(v, isObj = true, nonPlain = !Oas30ResponseCodes.notQuoted.contains(v))
          )
        )
    ) ++ webApiClasses

  private val asyncApi20Classes: Map[FieldForClass, Map[String, Seq[PatchedSuggestion]]] = {
    Map(
      FieldForClass(PayloadModel.`type`.head.iri(), PayloadModel.MediaType.value.iri()) ->
        Map("KnownValues" -> Oas20CommonMediaTypes.all.map(PatchedSuggestion(_)))
    )
  }

  private val ramlClasses: Map[FieldForClass, Map[String, Seq[PatchedSuggestion]]] =
    Map(
      FieldForClass(WebApiModel.`type`.head.iri(), WebApiModel.Accepts.value.iri()) ->
        Map("KnownValues" -> RamlCommonMediaTypes.all.map(PatchedSuggestion(_))),
      FieldForClass(WebApiModel.`type`.head.iri(), WebApiModel.ContentType.value.iri()) ->
        Map("KnownValues" -> RamlCommonMediaTypes.all.map(PatchedSuggestion(_))),
      FieldForClass(PayloadModel.`type`.head.iri(), PayloadModel.MediaType.value.iri()) ->
        Map("KnownValues" -> RamlCommonMediaTypes.all.map(PatchedSuggestion(_))),
      FieldForClass(ResponseModel.`type`.head.iri(), ResponseModel.StatusCode.value.iri()) ->
        Map("KnownValues" -> RamlResponseCodes.all.map(PatchedSuggestion(_, isObj = true)))
    ) ++ webApiClasses

  private val classesByDialect: Map[String, Map[FieldForClass, Map[String, Seq[PatchedSuggestion]]]] =
    Map(
      Raml10TypesDialect.dialect.id -> ramlClasses,
      Raml08TypesDialect.dialect.id -> ramlClasses,
      OAS20Dialect.dialect.id       -> oas20Classes,
      OAS30Dialect.dialect.id       -> oas30Classes,
      AsyncApi20Dialect.dialect.id  -> asyncApi20Classes,
      AsyncApi26Dialect.dialect.id  -> asyncApi20Classes
    )

  def getKnownValues(dialect: String, classTerm: String, propertyTerm: String): Seq[PatchedSuggestion] =
    getPatchedValues(dialect, classTerm, propertyTerm, Some("KnownValues"))

  private def getPatchedValues(
      dialect: String,
      classTerm: String,
      propertyTerm: String,
      patchType: Option[String]
  ): Seq[PatchedSuggestion] =
    patchType match {
      case Some(t) =>
        classesByDialect
          .getOrElse(dialect, Map.empty)
          .getOrElse(FieldForClass(classTerm, propertyTerm), Map.empty)
          .getOrElse(t, Nil)
      case _ =>
        classesByDialect
          .getOrElse(dialect, Map.empty)
          .getOrElse(FieldForClass(classTerm, propertyTerm), Map.empty)
          .flatMap(_._2)
          .toSeq
    }

}
