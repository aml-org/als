package org.mulesoft.amfintegration.dialect.dialects.asyncapi20

import amf.aml.client.scala.model.domain.DocumentsModel
import amf.plugins.document.vocabularies.plugin.ReferenceStyles
import org.mulesoft.amfintegration.dialect.BaseDialect
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.bindings._
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.schema._
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes._
import org.mulesoft.amfintegration.dialect.dialects.raml.raml10.Raml10TypesDialect

object AsyncApi20Dialect extends BaseDialect {
  private val _                        = Raml10TypesDialect().id // hack for ExampleNode.id
  override def DialectLocation: String = "file://vocabularies/dialects/asyncapi20.yaml"

  override val declares: Seq[DialectNode] = Seq(
    AsyncApi20ApiNode,
    AsyncApi20SecuritySettingsObject,
    AsyncApi20SecuritySchemeObject,
    AsyncAPI20ApiKeySecurityObject,
    AsyncAPI20HttpApiKeySecurityObject,
    AsyncAPI20HttpSecurityObject,
    AsyncAPI20Auth20SecurityObject,
    AsyncAPI20penIdConnectUrl,
    Oauth2FlowObject,
    AsyncAPI20FlowObject,
    Channel20Object,
    CorrelationIdObjectNode,
    MessageObjectNode,
    ResponseMessageObjectNode,
    RequestMessageObjectNode,
    MessageTraitsObjectNode,
    Operation20Object,
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
    StringShapeAsync2Node,
    ChannelBindingObjectNode,
    ServerBindingObjectNode,
    MessageBindingObjectNode,
    OperationBindingObjectNode,
    ServerBindingsObjectNode,
    OperationBindingsObjectNode,
    ChannelBindingsObjectNode,
    MessageBindingsObjectNode,
    AmqpChannelBindingObject,
    WsChannelBindingObject,
    LastWillMqttServerBindingObject,
    MqttServerBindingObjectNode,
    AmqpMessageBindingObjectNode,
    KafkaMessageBindingObjectNode,
    MqttMessageBindingObjectNode,
    HttpMessageBindingObjectNode,
    AmqpOperationBindingObjectNode,
    KafkaOperationBindingObjectNode,
    HttpOperationBindingObjectNode,
    MqttOperationBindingObjectNode,
    QueueAmqpChannelBinding,
    RoutingKeyAmqpChannelBinding,
    StringShapeAsync2Node,
    AMLExternalDocumentationObject,
    AMLInfoObject,
    AMLContactObject,
    AMLLicenseObject,
    AMLTagObject,
    AsyncApiServerObject
  )

  override def emptyDocument: DocumentsModel =
    DocumentsModel()
      .withId(DialectLocation + "#/documents")
      .withKeyProperty(true)
      .withReferenceStyle(ReferenceStyles.JSONSCHEMA)
      .withDeclarationsPath("components")

  override def encodes: DialectNode = AsyncApi20ApiNode

  override def declaredNodes: Map[String, DialectNode] = Map(
    "securitySchemes"   -> AsyncApi20SecuritySchemeObject,
    "messages"          -> MessageObjectNode,
    "parameters"        -> ParameterObjectNode,
    "correlationIds"    -> CorrelationIdObjectNode,
    "operationTraits"   -> OperationTraitsObjectNode,
    "messageTraits"     -> MessageTraitsObjectNode,
    "serverBindings"    -> ServerBindingsObjectNode,
    "channelBindings"   -> ChannelBindingsObjectNode,
    "operationBindings" -> OperationBindingsObjectNode,
    "messageBindings"   -> MessageBindingsObjectNode,
    "schemas"           -> BaseShapeAsync2Node
  )

  override protected val name: String    = "asyncapi"
  override protected val version: String = "2.0.0"
}
