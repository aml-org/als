package org.mulesoft.als.suggestions.plugins.aml.vocabulary

import amf.aml.client.scala.model.domain.{ClassTerm, PropertyTerm}
import amf.core.client.scala.model.document.{BaseUnit, DeclaresModel}
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object PropertyTermUriCompletionPlugin extends AMLCompletionPlugin {
  override def id: String = "PropertyTermUriCompletionPlugin"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] =
    if (isPropertyRange(request)) {
      Future {
        propertyTerms(request.baseUnit)
          .map(RawSuggestion.apply(_, isAKey = false))
      }
    } else emptySuggestion

  private def isPropertyRange(request: AmlCompletionRequest): Boolean =
    request.amfObject match {
      case _: ClassTerm => request.yPartBranch.parentEntryIs("properties") && request.yPartBranch.isInArray
      case _            => false
    }

  private def propertyTerms(bu: BaseUnit): Seq[String] =
    bu match {
      case d: DeclaresModel => d.declares.collect({ case pt: PropertyTerm => pt.name.option() }).flatten
      case _                => Nil
    }
}
