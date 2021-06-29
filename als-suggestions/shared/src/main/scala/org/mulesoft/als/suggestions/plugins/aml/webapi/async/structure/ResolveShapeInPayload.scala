package org.mulesoft.als.suggestions.plugins.aml.webapi.async.structure

import amf.core.client.scala.model.domain.Shape
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.ResolveIfApplies

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object ResolveShapeInPayload extends ResolveIfApplies {
  override def resolve(request: AmlCompletionRequest): Option[Future[Seq[RawSuggestion]]] =
    request.amfObject match {
      case _: Shape =>
        applies(Future(Seq()))
      case _ =>
        notApply
    }
}