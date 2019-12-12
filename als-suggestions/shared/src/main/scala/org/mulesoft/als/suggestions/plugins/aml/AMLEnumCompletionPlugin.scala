package org.mulesoft.als.suggestions.plugins.aml

import amf.plugins.document.vocabularies.model.domain.PropertyMapping
import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
object AMLEnumCompletionPlugin extends AMLCompletionPlugin {
  override def id = "AMLEnumCompletionPlugin"

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] =
    Future {
      if (params.yPartBranch.isValue || params.yPartBranch.isInArray)
        getSuggestions(params.propertyMapping, params.yPartBranch)
      else Nil
    }

  def getSuggestions(propertyMapping: List[PropertyMapping], yPartBranch: YPartBranch): Seq[RawSuggestion] = {
    propertyMapping match {
      case head :: Nil => suggestMapping(head)
      case Nil         => Nil
      case _ =>
        yPartBranch.parentEntry match {
          case Some(entry) =>
            propertyMapping
              .find(pm => entry.key.asScalar.exists(s => pm.name().option().contains(s.text)))
              .map(suggestMapping)
              .getOrElse(Nil)
          case None => Nil
        }
    }
  }

  def suggestMapping(pm: PropertyMapping): Seq[RawSuggestion] =
    pm.enum()
      .flatMap(_.option().map(e => {
        val raw = pm.toRaw("unknown")
        raw.copy(newText = e.toString,
                 displayText = e.toString,
                 description = e.toString,
                 options = raw.options.copy(isKey = false))
      }))
}
