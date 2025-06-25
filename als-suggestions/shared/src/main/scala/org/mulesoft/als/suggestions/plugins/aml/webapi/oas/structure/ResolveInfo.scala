package org.mulesoft.als.suggestions.plugins.aml.webapi.oas.structure

import amf.apicontract.client.scala.model.document.ComponentModule
import amf.apicontract.client.scala.model.domain.api.{AsyncApi, WebApi}
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.ResolveIfApplies
import org.mulesoft.als.suggestions.plugins.aml._
import org.mulesoft.amfintegration.amfconfiguration.DocumentDefinition
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.{AMLInfoObject => AsyncInfoObject}
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.{AMLInfoObject => WebAPIInfoObject}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object ResolveInfo extends ResolveIfApplies {
  override def resolve(request: AmlCompletionRequest): Option[Future[Seq[RawSuggestion]]] = {
    request.amfObject match {
      case _: ComponentModule if request.astPartBranch.isKeyDescendantOf("info") =>
        applies(componentInfoSuggestions(request.actualDocumentDefinition))
      case _: WebApi if request.astPartBranch.isKeyDescendantOf("info") =>
        applies(webApiInfoSuggestions(request.actualDocumentDefinition))
      case _: AsyncApi if request.astPartBranch.isKeyDescendantOf("info") =>
        applies(asyncInfoSuggestions(request.actualDocumentDefinition))
      case _ => notApply
    }
  }

  private def webApiInfoSuggestions(d: DocumentDefinition): Future[Seq[RawSuggestion]] =
    Future(WebAPIInfoObject.Obj.propertiesRaw(Some("docs"), d))

  private def componentInfoSuggestions(d: DocumentDefinition): Future[Seq[RawSuggestion]] =
    Future(
      WebAPIInfoObject.Obj
        .propertiesMapping()
        .filterNot(_.name().isNullOrEmpty)
        .filter(pm => pm.minCount().value() > 0)
        .map(_.toRaw("docs"))
    )

  private def asyncInfoSuggestions(d: DocumentDefinition): Future[Seq[RawSuggestion]] =
    Future(AsyncInfoObject.Obj.propertiesRaw(Some("docs"), d))
}
