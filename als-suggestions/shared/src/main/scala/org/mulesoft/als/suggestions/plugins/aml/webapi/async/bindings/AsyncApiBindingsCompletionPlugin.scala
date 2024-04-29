package org.mulesoft.als.suggestions.plugins.aml.webapi.async.bindings

import amf.apicontract.client.scala.model.domain.bindings.{
  ChannelBindings,
  MessageBindings,
  OperationBindings,
  ServerBindings
}
import amf.core.client.scala.model.domain.AmfObject
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.plugins.aml.EnumSuggestions
import org.mulesoft.als.suggestions.plugins.aml.webapi.ExceptionPlugin
import org.mulesoft.als.suggestions.{ObjectRange, RawSuggestion}
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.bindings.DynamicBindingObjectNode

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object AsyncApiBindingsCompletionPlugin extends ExceptionPlugin with EnumSuggestions {
  override def id: String = "AsyncApiBindingsCompletionPlugin"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    if (applies(request)) {
      Future {
        suggestMappingWithEnum(DynamicBindingObjectNode.`type`)
          .map(r => r.copy(options = r.options.copy(isKey = true, rangeKind = ObjectRange)))
      }
    } else emptySuggestion
  }

  private def isBinding(obj: AmfObject) = {
    obj.isInstanceOf[ChannelBindings] || obj.isInstanceOf[OperationBindings] || obj
      .isInstanceOf[MessageBindings] || obj.isInstanceOf[ServerBindings]
  }

  override def applies(request: AmlCompletionRequest): Boolean =
    isBinding(request.amfObject) && request.fieldEntry.isEmpty && request.astPartBranch.isKey
}
