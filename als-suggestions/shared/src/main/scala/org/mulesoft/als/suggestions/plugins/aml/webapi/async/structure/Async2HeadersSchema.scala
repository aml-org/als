package org.mulesoft.als.suggestions.plugins.aml.webapi.async.structure

import amf.apicontract.client.scala.model.domain.{Parameter, Response}
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.ResolveIfApplies
import org.mulesoft.als.suggestions.plugins.aml.NodeMappingWrapper
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.schema.AnyShapeAsync2Node

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Async2HeadersSchema extends ResolveIfApplies {
  override def resolve(request: AmlCompletionRequest): Option[Future[Seq[RawSuggestion]]] = {
    if (
      request.amfObject
        .isInstanceOf[Parameter] && request.astPartBranch.isKeyDescendantOf("headers") && request.branchStack.headOption
        .exists(_.isInstanceOf[Response])
    )
      Some(Future(AnyShapeAsync2Node.Obj.propertiesRaw(fromDefinition = request.actualDocumentDefinition)))
    else notApply
  }
}
