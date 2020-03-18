package org.mulesoft.amfintegration.dialect.dialects.asyncapi20

import amf.dialects.oas.nodes._
import amf.plugins.document.vocabularies.ReferenceStyles
import amf.plugins.document.vocabularies.model.domain.DocumentsModel
import org.mulesoft.amfintegration.dialect.BaseDialect
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.bindings.{
  ChannelBindingsObjectNode,
  MessageBindingObjectNode,
  OperationBindingsObjectNode,
  ServerBindingObjectNode
}
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.schema._

object AsyncApi20Dialect extends BaseDialect {

  override def DialectLocation: String = "file://vocabularies/dialects/asyncapi20.yaml"

  override val declares: Seq[DialectNode] = Seq(
    AsyncApi20SecuritySchemeObject,
    AsyncAPI20ApiKeySecurityObject,
    AsyncAPI20HttpApiKeySecurityObject,
    AsyncAPI20HttpSecurityObject,
    AsyncAPI20Auth20SecurityObject,
    AsyncAPI20penIdConnectUrl,
    Oauth2FlowObject,
    AsyncAPI20FlowObject,
    ChannelObject,
    CorrelationIdObjectNode,
    MessageObjectNode,
    MessageTraitsObjectNode,
    OperationObject,
    OperationTraitsObjectNode,
    ParameterObjectNode,
    ChannelBindingsObjectNode,
    ServerBindingObjectNode,
    MessageBindingObjectNode,
    OperationBindingsObjectNode,
    BaseShapeAsync2Node,
    AnyShapeAsync2Node,
    ArrayShapeAsync2Node,
    NodeShapeAsync2Node,
    NumberShapeAsync2Node,
    StringShapeAsync2Node
  )

  override def emptyDocument: DocumentsModel =
    DocumentsModel()
      .withId(DialectLocation + "#/documents")
      .withKeyProperty(true)
      .withReferenceStyle(ReferenceStyles.JSONSCHEMA)
      .withDeclarationsPath("components")

  override def encodes: DialectNode = Oas30WebApiNode

  override def declaredNodes: Map[String, DialectNode] = Map(
    "securitySchemes" -> AsyncApi20SecuritySchemeObject,
    "messages"        -> MessageObjectNode,
//    "parameters"        -> ParameterObjectNode,
//    "correlationsIds"   -> CorrelationIdObjectNode,
    "operationTraits"   -> OperationTraitsObjectNode,
    "messageTraits"     -> MessageTraitsObjectNode,
    "serverBindings"    -> ServerBindingObjectNode,
    "channelBindings"   -> ChannelBindingsObjectNode,
    "operationBindings" -> OperationBindingsObjectNode,
    "messageBindings"   -> MessageBindingObjectNode,
    "schemas"           -> BaseShapeAsync2Node
  )

  override protected val name: String    = "asyncapi"
  override protected val version: String = "2.0.0"
}
