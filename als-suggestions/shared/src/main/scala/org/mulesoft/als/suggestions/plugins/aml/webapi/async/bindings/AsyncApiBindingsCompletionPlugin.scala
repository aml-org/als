package org.mulesoft.als.suggestions.plugins.aml.webapi.async.bindings

import amf.aml.client.scala.model.domain.PropertyMapping
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
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.bindings.{
  ChannelBindingObjectNode,
  DynamicBindingObjectNode,
  MessageBindingObjectNode,
  OperationBindingObjectNode,
  ServerBindingObjectNode
}
import org.mulesoft.amfintegration.dialect.dialects.asyncapi26.bindings.{
  ChannelBinding26ObjectNode,
  MessageBinding26ObjectNode,
  OperationBinding26ObjectNode,
  ServerBinding26ObjectNode
}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait AsyncApiBindingsCompletionPlugin extends ExceptionPlugin with EnumSuggestions {
  override def id: String = "AsyncApiBindingsCompletionPlugin"

  protected val channel: PropertyMapping   = ChannelBindingObjectNode.`type`
  protected val message: PropertyMapping   = MessageBindingObjectNode.`type`
  protected val operation: PropertyMapping = OperationBindingObjectNode.`type`
  protected val server: PropertyMapping    = ServerBindingObjectNode.`type`

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    if (applies(request)) {
      Future {
        request.amfObject match {
          case _: ChannelBindings   => suggestEnums(channel)
          case _: OperationBindings => suggestEnums(operation)
          case _: MessageBindings   => suggestEnums(message)
          case _: ServerBindings    => suggestEnums(server)
          case _                    => Seq.empty
        }
      }
    } else emptySuggestion
  }

  private def suggestEnums(value: PropertyMapping): Seq[RawSuggestion] = {
    suggestMappingWithEnum(value)
      .map(r => r.copy(options = r.options.copy(isKey = true, rangeKind = ObjectRange)))
  }

  private def isBinding(obj: AmfObject) = {
    obj.isInstanceOf[ChannelBindings] || obj.isInstanceOf[OperationBindings] || obj
      .isInstanceOf[MessageBindings] || obj.isInstanceOf[ServerBindings]
  }

  override def applies(request: AmlCompletionRequest): Boolean =
    isBinding(request.amfObject) && request.fieldEntry.isEmpty && request.astPartBranch.isKey
}

object AsyncApi20BindingsCompletionPlugin extends AsyncApiBindingsCompletionPlugin
object AsyncApi26BindingsCompletionPlugin extends AsyncApiBindingsCompletionPlugin {
  override protected val channel: PropertyMapping   = ChannelBinding26ObjectNode.`type`
  override protected val message: PropertyMapping   = MessageBinding26ObjectNode.`type`
  override protected val operation: PropertyMapping = OperationBinding26ObjectNode.`type`
  override protected val server: PropertyMapping    = ServerBinding26ObjectNode.`type`
}
