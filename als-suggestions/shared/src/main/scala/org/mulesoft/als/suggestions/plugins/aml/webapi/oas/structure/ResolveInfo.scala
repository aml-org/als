package org.mulesoft.als.suggestions.plugins.aml.webapi.oas.structure

import amf.dialects.oas.nodes.AMLInfoObject
import amf.plugins.domain.webapi.models._
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.ResolveIfApplies
import org.mulesoft.als.suggestions.plugins.aml._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object ResolveInfo extends ResolveIfApplies {
  override def resolve(request: AmlCompletionRequest): Option[Future[Seq[RawSuggestion]]] =
    request.amfObject match {
      case _: WebApi if request.yPartBranch.isKeyDescendantOf("info") =>
        applies(infoSuggestions())
      case _ => notApply
    }

  private def infoSuggestions() =
    Future(AMLInfoObject.Obj.propertiesRaw(Some("docs")))
}
