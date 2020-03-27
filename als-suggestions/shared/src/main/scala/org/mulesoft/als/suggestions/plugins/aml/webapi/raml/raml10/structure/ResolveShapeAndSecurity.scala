package org.mulesoft.als.suggestions.plugins.aml.webapi.raml.raml10.structure

import amf.core.model.domain.Shape
import amf.plugins.domain.webapi.models.security.SecurityScheme
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.ResolveIfApplies

import scala.concurrent.Future

object ResolveShapeAndSecurity extends ResolveIfApplies {
  override def resolve(request: AmlCompletionRequest): Option[Future[Seq[RawSuggestion]]] = {
    request.amfObject match {
      case _: Shape | _: SecurityScheme => applies(Future.successful(Seq()))
      case _                            => notApply
    }
  }
}