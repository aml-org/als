package org.mulesoft.als.suggestions.plugins.aml.patched

case class PatchedSuggestion(text: String, description: Option[String] = None)

case class PatchesForField(propertyTerm: String, suggestions: Set[PatchedSuggestion])

case class FieldForClass(classTerm: String, propertyTerm: String)

object PatchedSuggestionsForDialect {

  private val classes: Map[FieldForClass, Map[String, Set[PatchedSuggestion]]] =
    Map(
      FieldForClass("https://github.amlorg.com/visit#MeetingNode", "https://github.amlorg.com/visit#duration") ->
        Map("KnownValues" -> Set(PatchedSuggestion("1"), PatchedSuggestion("2"), PatchedSuggestion("3"))))

  def getKnownValues(classTerm: String, propertyTerm: String): Set[PatchedSuggestion] =
    getPatchedValues(classTerm, propertyTerm, Some("KnownValues"))

  private def getPatchedValues(classTerm: String,
                               propertyTerm: String,
                               patchType: Option[String]): Set[PatchedSuggestion] =
    patchType match {
      case Some(t) =>
        classes
          .getOrElse(FieldForClass(classTerm, propertyTerm), Map.empty)
          .getOrElse(t, Set())
      case _ =>
        classes
          .getOrElse(FieldForClass(classTerm, propertyTerm), Map.empty)
          .flatMap(_._2)
          .toSet
    }

}
