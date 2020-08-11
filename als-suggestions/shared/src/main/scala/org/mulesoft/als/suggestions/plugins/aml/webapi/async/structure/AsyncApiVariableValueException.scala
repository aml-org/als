package org.mulesoft.als.suggestions.plugins.aml.webapi.async.structure

import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.ResolveIfApplies
import org.mulesoft.als.suggestions.plugins.aml.webapi.async.Async2VariableValueParam
import org.mulesoft.als.suggestions.plugins.aml.webapi.oas.VariableValueParam

import scala.concurrent.Future

object AsyncApiVariableValueException extends ResolveIfApplies {
  override def resolve(request: AmlCompletionRequest): Option[Future[Seq[RawSuggestion]]] =
    if (Async2VariableValueParam.applies(request)) Some(Future.successful(Nil)) else notApply
}
