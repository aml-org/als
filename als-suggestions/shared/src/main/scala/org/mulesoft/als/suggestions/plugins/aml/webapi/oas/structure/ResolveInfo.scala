package org.mulesoft.als.suggestions.plugins.aml.webapi.oas.structure

import amf.plugins.document.vocabularies.model.document.Dialect
import amf.plugins.domain.webapi.models.api.{AsyncApi, WebApi}
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.ResolveIfApplies
import org.mulesoft.als.suggestions.plugins.aml._
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.{AMLInfoObject => WebAPIInfoObject}
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.{AMLInfoObject => AsyncInfoObject}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object ResolveInfo extends ResolveIfApplies {
  override def resolve(request: AmlCompletionRequest): Option[Future[Seq[RawSuggestion]]] =
    request.amfObject match {
      case _: WebApi if request.yPartBranch.isKeyDescendantOf("info") =>
        applies(webApiInfoSuggestions(request.actualDialect))
      case _: AsyncApi if request.yPartBranch.isKeyDescendantOf("info") =>
        applies(asyncInfoSuggestions(request.actualDialect))
      case _ => notApply
    }

  private def webApiInfoSuggestions(d: Dialect): Future[Seq[RawSuggestion]] =
    Future(WebAPIInfoObject.Obj.propertiesRaw(Some("docs"), d))

  private def asyncInfoSuggestions(d: Dialect): Future[Seq[RawSuggestion]] =
    Future(AsyncInfoObject.Obj.propertiesRaw(Some("docs"), d))
}
