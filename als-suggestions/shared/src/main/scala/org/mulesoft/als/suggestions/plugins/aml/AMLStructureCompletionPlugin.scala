package org.mulesoft.als.suggestions.plugins.aml

import amf.core.model.domain.AmfObject
import amf.plugins.document.vocabularies.model.document.Dialect
import amf.plugins.document.vocabularies.model.domain.{NodeMapping, PropertyMapping}
import org.mulesoft.als.common.AmfSonElementFinder._
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AMLStructureCompletionsPlugin(propertyMapping: Seq[PropertyMapping], indentation: String) {

  private def extractText(mapping: PropertyMapping): (String, String) = {
    val cleanText = mapping.name().value()
    val whiteSpaces =
      if (mapping.literalRange().isNullOrEmpty) indentation
      else ""
    (cleanText, whiteSpaces)
  }

  private def startsWithLetter(string: String) = { // TODO: move to single object responsible for presentation
    val validSet: Set[Char] =
      (('a' to 'z') ++ ('A' to 'Z') ++ "\"" ++ "\'").toSet
    if (string.headOption.exists(validSet.contains)) true
    else false
  }

  private def getSuggestions: Seq[(String, String)] = propertyMapping.map(extractText)

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

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future {
      if (params.yPartBranch.isKey && !isInFieldValue(params)) {
        val isEncoded = isEncodes(params.amfObject, params.actualDialect)
        if ((isEncoded && params.yPartBranch.isAtRoot) || !isEncoded)
          new AMLStructureCompletionsPlugin(params.propertyMapping, params.indentation).resolve()
        else Seq()
      } else Seq()
    }
  }

  private def isInFieldValue(params: AmlCompletionRequest): Boolean = {
    params.fieldEntry
      .exists(
        _.value.value
          .position()
          .exists(li => li.contains(params.position)))
  }

  private def isEncodes(amfObject: AmfObject, dialect: Dialect) = {
    val iri = amfObject.meta.`type`.head.iri()

    dialect.declares
      .find(nm => dialect.documents().root().encoded().option().contains(nm.id))
      .collectFirst({ case d: NodeMapping if d.nodetypeMapping.option().contains(iri) => d })
      .isDefined
  }
}
