package org.mulesoft.als.suggestions.plugins.aml.webapi.oas.oas20.structure

import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.ResolveIfApplies

import scala.concurrent.Future

object ResolveParameterPayload extends ResolveIfApplies with ParameterKnowledge {
  override def resolve(request: AmlCompletionRequest): Option[Future[Seq[RawSuggestion]]] =
    request.amfObject match {
      case _ if isInParameter(request.amfObject) =>
        applies(Future.successful(Seq()))
      case _ => notApply
    }
}
