package org.mulesoft.als.suggestions.plugins.aml.vocabulary

import amf.core.model.document.{BaseUnit, DeclaresModel}
import amf.plugins.document.vocabularies.model.domain.{ClassTerm, ObjectPropertyTerm}
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object ClassTermUriCompletionPlugin extends AMLCompletionPlugin {
  override def id: String = "ClassTermUriCompletionPlugin"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] =
    if (isPropertyRange(request)) {
      Future {
        classTerms(request.baseUnit)
          .map(RawSuggestion.apply(_, isAKey = false))
      }
    } else emptySuggestion

  private def isPropertyRange(request: AmlCompletionRequest): Boolean =
    request.amfObject match {
      case _: ObjectPropertyTerm => request.yPartBranch.parentEntryIs("range") && request.yPartBranch.isValue
      case _                     => false
    }

  private def classTerms(bu: BaseUnit): Seq[String] =
    bu match {
      case d: DeclaresModel => d.declares.collect({ case ct: ClassTerm => ct.name.option() }).flatten
      case _                => Nil
    }
}
