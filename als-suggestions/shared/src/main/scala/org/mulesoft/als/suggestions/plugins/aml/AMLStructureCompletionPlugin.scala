package org.mulesoft.als.suggestions.plugins.aml

import amf.plugins.document.vocabularies.model.domain.PropertyMapping
import org.mulesoft.als.common.AmfSonElementFinder._
import org.mulesoft.als.suggestions.interfaces.{CompletionParams, CompletionPlugin, RawSuggestion}
import org.mulesoft.lsp.edit.TextEdit

import scala.concurrent.Future

class AMLStructureCompletionsPlugin(params: CompletionParams) extends AMLSuggestionsHelper {

  private def extractText(mapping: PropertyMapping, indent: String): (String, String) = {
    val cleanText = mapping.name().value()
    val whiteSpaces =
      if (mapping.literalRange().isNullOrEmpty) s"\n$indent"
      else ""
    (cleanText, whiteSpaces)
  }

  private def startsWithLetter(string: String) = { // TODO: move to single object responsible for presentation
    val validSet: Set[Char] =
      (('a' to 'z') ++ ('A' to 'Z') ++ "\"" ++ "\'").toSet
    if (string.headOption.exists(validSet.contains)) true
    else false
  }

  private def getSuggestions: Seq[(String, String)] =
    params.propertyMappings.map(extractText(_, getIndentation(params.currentBaseUnit, params.position)))

  def resolve(): Future[Seq[RawSuggestion]] =
    Future.successful(
      getSuggestions
        .map(s =>
          new RawSuggestion {
            override def newText: String =
              if (startsWithLetter(s._1)) s._1 else s""""${s._1}""""

            override def displayText: String = s._1

            override def description: String = s._1

            override def textEdits: Seq[TextEdit] = Seq()

            override def whiteSpacesEnding: String = s._2

            override def isKey: Boolean = true
        }))
}

object AMLStructureCompletionPlugin extends CompletionPlugin {
  override def id = "AMLStructureCompletionPlugin"

  override def resolve(params: CompletionParams): Future[Seq[RawSuggestion]] = {
    if (params.yPartBranch.isKey && !params.fieldEntry
          .exists(_.value.value
            .position()
            .exists(li => li.contains(params.position))))
      new AMLStructureCompletionsPlugin(params).resolve()
    else Future.successful(Seq())
  }
}
