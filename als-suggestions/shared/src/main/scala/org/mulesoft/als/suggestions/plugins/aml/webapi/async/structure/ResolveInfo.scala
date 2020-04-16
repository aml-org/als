package org.mulesoft.als.suggestions.plugins.aml.webapi.async.structure

import amf.dialects.oas.nodes.AMLInfoObject
import amf.plugins.domain.webapi.models.WebApi
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.ResolveIfApplies
import org.mulesoft.als.suggestions.plugins.aml._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object ResolveInfo extends ResolveIfApplies {

  def infoSuggestions(): Future[Seq[RawSuggestion]] =
    Future(AMLInfoObject.Obj.propertiesRaw(Some("docs")))

  override def resolve(request: AmlCompletionRequest): Option[Future[Seq[RawSuggestion]]] =
    request.amfObject match {
      case _: WebApi if request.yPartBranch.isKeyDescendantOf("info") =>
        applies(infoSuggestions())
      case _ => notApply
    }
}
