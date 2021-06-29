package org.mulesoft.als.suggestions.plugins.aml.webapi.oas.structure

import amf.apicontract.client.scala.model.domain.{Operation, Tag}
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.ResolveIfApplies

import scala.concurrent.Future

object ResolveTag extends ResolveIfApplies {
  override def resolve(request: AmlCompletionRequest): Option[Future[Seq[RawSuggestion]]] =
    request.amfObject match {
      case _: Tag if request.branchStack.headOption.exists(_.isInstanceOf[Operation]) =>
        applies(Future.successful(Seq()))
      case _ => notApply
    }
}