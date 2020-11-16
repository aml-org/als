package org.mulesoft.als.suggestions.plugins.aml.webapi.async.structure

import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.ResolveIfApplies

import scala.concurrent.Future

object ResolveServers extends ResolveIfApplies {
  // todo: delete?
  override def resolve(request: AmlCompletionRequest): Option[Future[Seq[RawSuggestion]]] = notApply
}
