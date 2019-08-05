package org.mulesoft.als.suggestions.plugins.aml

import amf.core.model.domain.AmfObject
import amf.plugins.document.vocabularies.model.document.Dialect
import amf.plugins.document.vocabularies.model.domain.PropertyMapping
import org.mulesoft.als.common.AmfSonElementFinder.AlsLexicalInformation
import org.mulesoft.als.suggestions.{AMLCompletionParams, RawSuggestion}
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AMLStructureCompletionsPlugin(params: AMLCompletionParams) extends AMLSuggestionsHelper {

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
    params.propertyMappings.map(extractText(_, getIndentation(params.baseUnit, params.position)))

  def resolve(): Seq[RawSuggestion] =
    getSuggestions
      .map(
        s =>
          RawSuggestion(if (startsWithLetter(s._1)) s._1
                        else s""""${s._1}"""",
                        s._1,
                        s._1,
                        Seq(),
                        isKey = true,
                        s._2))
}

object AMLStructureCompletionPlugin extends AMLCompletionPlugin {
  override def id = "AMLStructureCompletionPlugin"

  override def resolve(params: AMLCompletionParams): Future[Seq[RawSuggestion]] = {
    Future {
      if (params.yPartBranch.isKey && !isInFieldValue(params)) {
        val isEncoded = isEncodes(params.amfObject, params.dialect)
        if ((isEncoded && params.yPartBranch.isAtRoot) || !isEncoded)
          new AMLStructureCompletionsPlugin(params).resolve()
        else Seq()
      } else Seq()
    }
  }

  private def isInFieldValue(params: AMLCompletionParams): Boolean = {
    params.fieldEntry
      .exists(
        _.value.value
          .position()
          .exists(li => li.contains(params.position)))
  }

  private def isEncodes(amfObject: AmfObject, dialect: Dialect) = {
    amfObject.meta.`type`.exists(t => dialect.documents().root().encoded().option().contains(t.iri()))
  }
}
