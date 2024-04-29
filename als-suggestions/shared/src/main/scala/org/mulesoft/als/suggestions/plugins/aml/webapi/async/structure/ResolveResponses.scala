package org.mulesoft.als.suggestions.plugins.aml.webapi.async.structure

import amf.apicontract.client.scala.model.domain.Response
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.ResolveIfApplies
import org.mulesoft.als.suggestions.plugins.aml.webapi.async.MessageKnowledge

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object ResolveResponses extends ResolveIfApplies {
  override def resolve(request: AmlCompletionRequest): Option[Future[Seq[RawSuggestion]]] =
    request.amfObject match {
      case _: Response
          if !MessageKnowledge.isRootMessageBlock(request) && !request.astPartBranch.isKeyDescendantOf("headers") =>
        applies(Future(Seq()))
      case _ => notApply
    }
}
