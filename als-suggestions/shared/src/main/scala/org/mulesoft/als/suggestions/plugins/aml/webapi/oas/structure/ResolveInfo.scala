package org.mulesoft.als.suggestions.plugins.aml.webapi.oas.structure

import amf.aml.client.scala.model.document.Dialect
import amf.apicontract.client.scala.model.domain.api.{AsyncApi, WebApi}
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.ResolveIfApplies
import org.mulesoft.als.suggestions.plugins.aml._
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.{AMLInfoObject => AsyncInfoObject}
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.{AMLInfoObject => WebAPIInfoObject}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object ResolveInfo extends ResolveIfApplies {
  override def resolve(request: AmlCompletionRequest): Option[Future[Seq[RawSuggestion]]] =
    request.amfObject match {
      case _: WebApi if request.astPartBranch.isKeyDescendantOf("info") =>
        applies(webApiInfoSuggestions(request.actualDialect))
      case _: AsyncApi if request.astPartBranch.isKeyDescendantOf("info") =>
        applies(asyncInfoSuggestions(request.actualDialect))
      case _ => notApply
    }

  private def webApiInfoSuggestions(d: Dialect): Future[Seq[RawSuggestion]] =
    Future(WebAPIInfoObject.Obj.propertiesRaw(Some("docs"), d))

  private def asyncInfoSuggestions(d: Dialect): Future[Seq[RawSuggestion]] =
    Future(AsyncInfoObject.Obj.propertiesRaw(Some("docs"), d))
}
