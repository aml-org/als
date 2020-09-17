package org.mulesoft.als.suggestions.plugins.aml.webapi.oas.structure

import amf.core.annotations.DeclaredElement
import amf.plugins.domain.webapi.models._
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.ResolveIfApplies
import org.mulesoft.als.suggestions.plugins.aml._
import org.mulesoft.amfintegration.AmfImplicits._
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.Oas20ResponseObject

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object IriTemplateMappingIgnore extends ResolveIfApplies {
  override def resolve(request: AmlCompletionRequest): Option[Future[Seq[RawSuggestion]]] =
    if (request.amfObject.isInstanceOf[IriTemplateMapping]) Some(Future.successful(Nil))
    else notApply

}
