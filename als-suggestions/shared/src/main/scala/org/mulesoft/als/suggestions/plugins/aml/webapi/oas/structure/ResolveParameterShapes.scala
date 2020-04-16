package org.mulesoft.als.suggestions.plugins.aml.webapi.oas.structure

import amf.core.model.domain.Shape
import amf.plugins.domain.webapi.models._
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.ResolveIfApplies

import scala.concurrent.Future

object ResolveParameterShapes extends ResolveIfApplies {
  override def resolve(request: AmlCompletionRequest): Option[Future[Seq[RawSuggestion]]] =
    request.amfObject match {
      case _: Parameter | _: Shape => applies(Future.successful(Seq()))
      case _                       => notApply
    }
}