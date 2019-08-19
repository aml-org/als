package org.mulesoft.als.suggestions.plugins.aml.patched

import amf.plugins.domain.webapi.metamodel.{PayloadModel, ResponseModel, WebApiModel}
import org.mulesoft.typesystem.definition.system.{OasCommonMediaTypes, OasResponseCodes}

case class PatchedSuggestion(text: String, description: Option[String] = None)

case class FieldForClass(classTerm: String, propertyTerm: String)

object PatchedSuggestionsForDialect {

  private val classes: Map[FieldForClass, Map[String, Seq[PatchedSuggestion]]] =
    Map(
      FieldForClass(ResponseModel.`type`.head.iri(), ResponseModel.StatusCode.value.iri()) ->
        Map("KnownValues" -> OasResponseCodes.all.map(PatchedSuggestion(_))),
      FieldForClass(ResponseModel.`type`.head.iri(), ResponseModel.Name.value.iri()) ->
        Map("KnownValues" -> OasResponseCodes.all.map(PatchedSuggestion(_))),
      FieldForClass(WebApiModel.`type`.head.iri(), WebApiModel.Accepts.value.iri()) ->
        Map("KnownValues" -> OasCommonMediaTypes.all.map(PatchedSuggestion(_))),
      FieldForClass(WebApiModel.`type`.head.iri(), WebApiModel.ContentType.value.iri()) ->
        Map("KnownValues" -> OasCommonMediaTypes.all.map(PatchedSuggestion(_))),
      FieldForClass(PayloadModel.`type`.head.iri(), PayloadModel.MediaType.value.iri()) ->
        Map("KnownValues" -> OasCommonMediaTypes.all.map(PatchedSuggestion(_)))
    )

  def getKnownValues(classTerm: String, propertyTerm: String): Seq[PatchedSuggestion] =
    getPatchedValues(classTerm, propertyTerm, Some("KnownValues"))

  private def getPatchedValues(classTerm: String,
                               propertyTerm: String,
                               patchType: Option[String]): Seq[PatchedSuggestion] =
    patchType match {
      case Some(t) =>
        classes
          .getOrElse(FieldForClass(classTerm, propertyTerm), Map.empty)
          .getOrElse(t, Nil)
      case _ =>
        classes
          .getOrElse(FieldForClass(classTerm, propertyTerm), Map.empty)
          .flatMap(_._2)
          .toSeq
    }

}
