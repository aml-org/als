package org.mulesoft.als.suggestions.plugins.aml.webapi.async.structure

import amf.plugins.domain.webapi.models.{Parameter, Response}
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.ResolveIfApplies
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.schema.{AnyShapeAsync2Node, BaseShapeAsync2Node}
import org.mulesoft.als.suggestions.plugins.aml.NodeMappingWrapper

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object Async2HeadersSchema extends ResolveIfApplies {
  override def resolve(request: AmlCompletionRequest): Option[Future[Seq[RawSuggestion]]] = {
    if (request.amfObject
          .isInstanceOf[Parameter] && request.yPartBranch.isKeyDescendantOf("headers") && request.branchStack.headOption
          .exists(_.isInstanceOf[Response]))
      Some(Future(AnyShapeAsync2Node.Obj.propertiesRaw(d = request.actualDialect)))
    else notApply
  }
}
