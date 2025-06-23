package org.mulesoft.als.suggestions.plugins.aml.webapi.oas.oas30.structure

import amf.apicontract.client.scala.model.domain.api.WebApi
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.ResolveIfApplies
import org.mulesoft.als.suggestions.plugins.aml.NodeMappingWrapper
import org.mulesoft.als.suggestions.plugins.aml.webapi.oas.OASRefTag.refSuggestion
import org.mulesoft.amfintegration.amfconfiguration.DocumentDefinition
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.AMLRequestBodyObject
import org.yaml.model.YMapEntry

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object ResolveRequestBody extends ResolveIfApplies {
  override def resolve(request: AmlCompletionRequest): Option[Future[Seq[RawSuggestion]]] = {
    request.amfObject match {
      case _: WebApi =>
        // due to how recovery works, json creates more entries in between
        val idxParent = if (request.astPartBranch.strict) 6 else 3
        request.astPartBranch.getAncestor(idxParent) match {
          case Some(yme: YMapEntry) if yme.key.asScalar.map(_.text).contains("requestBodies") =>
            Some(requestBodySuggestions(request.actualDocumentDefinition))
          case _ => notApply
        }
      case _ => notApply
    }
  }
  private def requestBodySuggestions(d: DocumentDefinition): Future[Seq[RawSuggestion]] =
    Future {
      AMLRequestBodyObject.Obj.propertiesRaw(None, d) ++ refSuggestion
    }
}
