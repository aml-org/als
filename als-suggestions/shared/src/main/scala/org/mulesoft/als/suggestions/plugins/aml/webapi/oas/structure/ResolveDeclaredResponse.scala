package org.mulesoft.als.suggestions.plugins.aml.webapi.oas.structure

import amf.apicontract.client.scala.model.domain.Response
import amf.core.internal.annotations.DeclaredElement
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.ResolveIfApplies
import org.mulesoft.als.suggestions.plugins.aml._
import org.mulesoft.amfintegration.AmfImplicits._
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.Oas20ResponseObject

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object ResolveDeclaredResponse extends ResolveIfApplies {
  override def resolve(request: AmlCompletionRequest): Option[Future[Seq[RawSuggestion]]] =
    request.amfObject match {
      case r: Response if r.annotations.contains(classOf[DeclaredElement]) && request.fieldEntry.isEmpty =>
        applies(declaredResponse(request))
      case _ => notApply
    }

  private def declaredResponse(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = Future {
    new AMLStructureCompletionsPlugin(
      request.propertyMapping.filter(_.id != Oas20ResponseObject.statusCodeProperty.id),
      request.actualDocumentDefinition
    )
      .resolve(request.amfObject.metaURIs.head)
  }
}
