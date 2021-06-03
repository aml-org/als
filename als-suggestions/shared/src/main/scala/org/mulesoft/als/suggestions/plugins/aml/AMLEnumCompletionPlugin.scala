package org.mulesoft.als.suggestions.plugins.aml

import amf.plugins.document.vocabularies.model.domain.PropertyMapping
import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.suggestions.{ObjectRange, RawSuggestion, StringScalarRange}
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
object AMLEnumCompletionPlugin extends AMLCompletionPlugin with EnumSuggestions {
  override def id = "AMLEnumCompletionPlugin"

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] =
    Future {
      if (params.yPartBranch.isValue || params.yPartBranch.isInArray && params.fieldEntry.isDefined)
        getSuggestions(params.propertyMapping, params.yPartBranch)
      else Nil
    }

  def getSuggestions(propertyMapping: List[PropertyMapping], yPartBranch: YPartBranch): Seq[RawSuggestion] = {
    propertyMapping match {
      case head :: Nil => suggestMappingWithEnum(head)
      case Nil         => Nil
      case _ =>
        yPartBranch.parentEntry match {
          case Some(entry) =>
            propertyMapping
              .find(pm => entry.key.asScalar.exists(s => pm.name().option().contains(s.text)))
              .map(suggestMappingWithEnum)
              .getOrElse(Nil)
          case None => Nil
        }
    }
  }
}
