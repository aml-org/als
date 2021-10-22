package org.mulesoft.als.suggestions.plugins.aml.webapi.oas.structure

import amf.shapes.client.scala.model.domain.IriTemplateMapping
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.ResolveIfApplies

import scala.concurrent.Future

object IriTemplateMappingIgnore extends ResolveIfApplies {
  override def resolve(request: AmlCompletionRequest): Option[Future[Seq[RawSuggestion]]] =
    if (request.amfObject.isInstanceOf[IriTemplateMapping]) Some(Future.successful(Nil))
    else notApply

}
