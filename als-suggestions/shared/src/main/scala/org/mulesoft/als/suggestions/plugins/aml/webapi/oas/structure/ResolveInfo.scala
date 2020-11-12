package org.mulesoft.als.suggestions.plugins.aml.webapi.oas.structure

import amf.plugins.document.vocabularies.model.document.Dialect
import amf.plugins.domain.webapi.models._
import amf.plugins.domain.webapi.models.api.WebApi
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.ResolveIfApplies
import org.mulesoft.als.suggestions.plugins.aml._
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.AMLInfoObject

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object ResolveInfo extends ResolveIfApplies {
  override def resolve(request: AmlCompletionRequest): Option[Future[Seq[RawSuggestion]]] =
    request.amfObject match {
      case _: WebApi if request.yPartBranch.isKeyDescendantOf("info") =>
        applies(infoSuggestions(request.actualDialect))
      case _ => notApply
    }

  private def infoSuggestions(d: Dialect): Future[Seq[RawSuggestion]] =
    Future(AMLInfoObject.Obj.propertiesRaw(Some("docs"), d))
}
