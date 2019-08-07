package org.mulesoft.als.suggestions.plugins.aml

import amf.core.annotations.SourceAST
import amf.core.model.document.Document
import amf.plugins.document.vocabularies.model.domain.PropertyMapping
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.yaml.model.YPart

import scala.concurrent.Future

class AMLEnumCompletionsPlugin(params: AmlCompletionRequest) extends AMLSuggestionsHelper {

  def presentArray(value: String): String =
    s"\n${getIndentation(params.baseUnit, params.position)}- $value"

  private def getSuggestions: Seq[String] = {
    params.propertyMapping match {
      case head :: Nil => suggestMapping(head)
      case Nil         => Nil
      case list =>
        params.yPartBranch.parentEntry match {
          case Some(entry) =>
            params.propertyMapping
              .find(pm => entry.key.asScalar.exists(s => pm.name().option().contains(s.text)))
              .map(suggestMapping)
              .getOrElse(Nil)
          case None => Nil
        }
    }
  }

  private def suggestMapping(pm: PropertyMapping): Seq[String] = {
    pm.enum()
      .flatMap(_.option().map(e => {

        if (pm.allowMultiple()
              .value() && params.prefix.isEmpty && !params.yPartBranch.isArray && !params.yPartBranch.isInArray)
          presentArray(e.toString)
        else e.toString
      }))
  }

  def resolve(): Seq[RawSuggestion] =
    getSuggestions
      .map(s => RawSuggestion(s, isAKey = false))
}

object AMLEnumCompletionPlugin extends AMLCompletionPlugin {
  override def id = "AMLEnumCompletionPlugin"

  override def resolve(params: AMLCompletionParams): Future[Seq[RawSuggestion]] =
    if (params.yPartBranch.isValue || params.yPartBranch.isInArray)
      new AMLEnumCompletionsPlugin(params).resolve()
    else emptySuggestion
}
