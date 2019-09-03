package org.mulesoft.als.suggestions.plugins.aml.patched

import amf.dialects.OAS20Dialect
import amf.plugins.domain.shapes.metamodel.ExampleModel
import amf.plugins.domain.webapi.metamodel.{OperationModel, PayloadModel, ResponseModel, WebApiModel}
import org.mulesoft.als.suggestions.plugins.aml.webapi.raml.raml08.Raml08TypesDialect
import org.mulesoft.als.suggestions.plugins.aml.webapi.raml.raml10.Raml10TypesDialect
import org.mulesoft.als.suggestions.plugins.aml.webapi.{
  OasCommonMediaTypes,
  OasResponseCodes,
  RamlCommonMediaTypes,
  RamlResponseCodes
}

case class PatchedSuggestion(text: String, description: Option[String] = None)

case class FieldForClass(classTerm: String, propertyTerm: String)

object PatchedSuggestionsForDialect {

  private val oas20Classes: Map[FieldForClass, Map[String, Seq[PatchedSuggestion]]] =
    Map(
      FieldForClass(WebApiModel.`type`.head.iri(), WebApiModel.Accepts.value.iri()) ->
        Map("KnownValues" -> OasCommonMediaTypes.all.map(PatchedSuggestion(_))),
      FieldForClass(WebApiModel.`type`.head.iri(), WebApiModel.ContentType.value.iri()) ->
        Map("KnownValues" -> OasCommonMediaTypes.all.map(PatchedSuggestion(_))),
      FieldForClass(OperationModel.`type`.head.iri(), OperationModel.ContentType.value.iri()) ->
        Map("KnownValues" -> OasCommonMediaTypes.all.map(PatchedSuggestion(_))),
      FieldForClass(OperationModel.`type`.head.iri(), OperationModel.Accepts.value.iri()) ->
        Map("KnownValues" -> OasCommonMediaTypes.all.map(PatchedSuggestion(_))),
      FieldForClass(ExampleModel.`type`.head.iri(), ExampleModel.MediaType.value.iri()) ->
        Map("KnownValues" -> OasCommonMediaTypes.all.map(PatchedSuggestion(_))),
      FieldForClass(PayloadModel.`type`.head.iri(), PayloadModel.MediaType.value.iri()) ->
        Map("KnownValues" -> OasCommonMediaTypes.all.map(PatchedSuggestion(_))),
      FieldForClass(ResponseModel.`type`.head.iri(), ResponseModel.StatusCode.value.iri()) ->
        Map("KnownValues" -> OasResponseCodes.all.map(PatchedSuggestion(_))),
      FieldForClass(ResponseModel.`type`.head.iri(), ResponseModel.Name.value.iri()) ->
        Map("KnownValues" -> OasResponseCodes.all.map(PatchedSuggestion(_)))
    )

  private val raml10Classes: Map[FieldForClass, Map[String, Seq[PatchedSuggestion]]] =
    Map(
      FieldForClass(WebApiModel.`type`.head.iri(), WebApiModel.Accepts.value.iri()) ->
        Map("KnownValues" -> RamlCommonMediaTypes.all.map(PatchedSuggestion(_))),
      FieldForClass(WebApiModel.`type`.head.iri(), WebApiModel.ContentType.value.iri()) ->
        Map("KnownValues" -> RamlCommonMediaTypes.all.map(PatchedSuggestion(_))),
      FieldForClass(PayloadModel.`type`.head.iri(), PayloadModel.MediaType.value.iri()) ->
        Map("KnownValues" -> RamlCommonMediaTypes.all.map(PatchedSuggestion(_))),
      FieldForClass(ResponseModel.`type`.head.iri(), ResponseModel.StatusCode.value.iri()) ->
        Map("KnownValues" -> RamlResponseCodes.all.map(PatchedSuggestion(_)))
    )

  private val classesByDialect: Map[String, Map[FieldForClass, Map[String, Seq[PatchedSuggestion]]]] =
    Map(Raml10TypesDialect.dialect.id -> raml10Classes,
        Raml08TypesDialect.dialect.id -> raml10Classes,
        OAS20Dialect.dialect.id       -> oas20Classes)

  def getKnownValues(dialect: String, classTerm: String, propertyTerm: String): Seq[PatchedSuggestion] =
    getPatchedValues(dialect, classTerm, propertyTerm, Some("KnownValues"))

  private def getPatchedValues(dialect: String,
                               classTerm: String,
                               propertyTerm: String,
                               patchType: Option[String]): Seq[PatchedSuggestion] =
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
