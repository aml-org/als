package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.plugins.domain.webapi.models.EndPoint
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.ResolveIfApplies

import scala.concurrent.Future

object ResolveUriParameter extends ResolveIfApplies {
  override def resolve(request: AmlCompletionRequest): Option[Future[Seq[RawSuggestion]]] = {
    request.amfObject match {
      case _: EndPoint if request.yPartBranch.isKeyDescendantOf("uriParameters") =>
        applies(Future.successful(Seq()))
      case _ => notApply
    }
  }
}