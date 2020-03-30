package org.mulesoft.als.suggestions.plugins.aml.webapi.async.structure

import amf.plugins.domain.shapes.models.AnyShape
import amf.plugins.domain.webapi.metamodel.MessageModel
import amf.plugins.domain.webapi.models.Response
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.ResolveIfApplies
import org.mulesoft.als.suggestions.plugins.aml.webapi.async.{Async20TypeFacetsCompletionPlugin, MessageKnowledge}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object ResolveResponses extends ResolveIfApplies {
  override def resolve(request: AmlCompletionRequest): Option[Future[Seq[RawSuggestion]]] =
    request.amfObject match {
      case r: Response if request.fieldEntry.map(_.field).contains(MessageModel.Headers) =>
        applies(
          Future.successful(Async20TypeFacetsCompletionPlugin
            .resolveShape(AnyShape(r.fields, r.annotations), Nil)))
      case _: Response if !MessageKnowledge.isRootMessageBlock(request) =>
        applies(Future(Seq()))
      case _ => notApply
    }
}