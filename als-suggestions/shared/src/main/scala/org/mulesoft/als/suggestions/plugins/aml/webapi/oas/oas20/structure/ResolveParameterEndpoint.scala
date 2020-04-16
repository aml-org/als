package org.mulesoft.als.suggestions.plugins.aml.webapi.oas.oas20.structure

import amf.plugins.domain.webapi.models.EndPoint
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.ResolveIfApplies

import scala.concurrent.Future

object ResolveParameterEndpoint extends ResolveIfApplies with ParameterKnowledge {
  override def resolve(request: AmlCompletionRequest): Option[Future[Seq[RawSuggestion]]] =
    request.amfObject match {
      case _: EndPoint if isInParameter(request.yPartBranch) =>
        applies(Future.successful(Seq()))
      case _ => notApply
    }
}