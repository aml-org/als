package org.mulesoft.als.suggestions.plugins.aml.patched

import org.mulesoft.typesystem.definition.system.{OasCommonMediaTypes, OasResponseCodes}

case class PatchedSuggestion(text: String, description: Option[String] = None)

case class FieldForClass(classTerm: String, propertyTerm: String)

object PatchedSuggestionsForDialect {

  private val classes: Map[FieldForClass, Map[String, Seq[PatchedSuggestion]]] =
    Map(
      FieldForClass("http://a.ml/vocabularies/http#Response", "http://schema.org/name") ->
        Map("KnownValues" -> OasResponseCodes.all.map(PatchedSuggestion(_))),
      FieldForClass("http://schema.org/WebAPI", "http://a.ml/vocabularies/http#contentType") ->
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
