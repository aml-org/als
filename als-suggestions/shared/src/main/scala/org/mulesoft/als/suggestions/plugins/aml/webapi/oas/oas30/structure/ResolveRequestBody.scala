package org.mulesoft.als.suggestions.plugins.aml.webapi.oas.oas30.structure

import amf.aml.client.scala.model.document.Dialect
import amf.apicontract.client.scala.model.domain.api.WebApi
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.ResolveIfApplies
import org.mulesoft.als.suggestions.plugins.NonPatchHacks
import org.mulesoft.als.suggestions.plugins.aml.NodeMappingWrapper
import org.mulesoft.als.suggestions.plugins.aml.webapi.oas.OASRefTag.refSuggestion
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.AMLRequestBodyObject
import org.yaml.model.YMapEntry

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object ResolveRequestBody extends ResolveIfApplies with NonPatchHacks {
  override def resolve(request: AmlCompletionRequest): Option[Future[Seq[RawSuggestion]]] = {
    request.amfObject match {
      case _: WebApi if jsonPatchHack(request.yPartBranch) =>
        request.yPartBranch.getAncestor(6) match {
          case Some(yme: YMapEntry) if yme.key.asScalar.map(_.text).contains("requestBodies") =>
            Some(requestBodySuggestions(request.actualDialect))
          case _ => notApply
        }
      case _: WebApi =>
        request.yPartBranch.getAncestor(3) match {
          case Some(yme: YMapEntry) if yme.key.asScalar.map(_.text).contains("requestBodies") =>
            Some(requestBodySuggestions(request.actualDialect))
          case _ => notApply
        }
      case _ => notApply
    }
  }
  private def requestBodySuggestions(d: Dialect): Future[Seq[RawSuggestion]] =
    Future {
      AMLRequestBodyObject.Obj.propertiesRaw(None, d) ++ refSuggestion
    }
}
