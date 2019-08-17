package org.mulesoft.als.suggestions.plugins.aml

import amf.plugins.document.vocabularies.model.domain.PropertyMapping
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AMLEnumCompletionsPlugin(params: AmlCompletionRequest, indentation: String) {

  def presentArray(value: String): String =
    s"\n$indentation- $value"

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

  private def suggestMapping(pm: PropertyMapping): Seq[String] = pm.enum().flatMap(_.option().map(e => e.toString))

  def resolve(): Seq[RawSuggestion] =
    getSuggestions
      .map(s => RawSuggestion(s, isAKey = false))
}

object AMLEnumCompletionPlugin extends AMLCompletionPlugin {
  override def id = "AMLEnumCompletionPlugin"

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] =
    if (params.yPartBranch.isValue || params.yPartBranch.isInArray)
      Future { new AMLEnumCompletionsPlugin(params, params.indentation).resolve() } else emptySuggestion
}
