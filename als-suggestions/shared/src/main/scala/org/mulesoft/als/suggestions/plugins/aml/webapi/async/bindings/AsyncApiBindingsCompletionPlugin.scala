package org.mulesoft.als.suggestions.plugins.aml.webapi.async.bindings

import amf.core.model.domain.AmfObject
import amf.plugins.domain.webapi.metamodel.bindings.BindingType
import amf.plugins.domain.webapi.models.bindings.{ChannelBindings, MessageBindings, OperationBindings, ServerBindings}
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.{AMLEnumCompletionPlugin, EnumSuggestions}
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
    isBinding(request.amfObject) && request.fieldEntry.isEmpty && request.yPartBranch.isKey
}
