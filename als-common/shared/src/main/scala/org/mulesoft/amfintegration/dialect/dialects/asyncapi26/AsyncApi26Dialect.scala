package org.mulesoft.amfintegration.dialect.dialects.asyncapi26

import amf.aml.client.scala.model.domain.DocumentsModel
import amf.plugins.document.vocabularies.plugin.ReferenceStyles
import org.mulesoft.amfintegration.dialect.BaseDialect
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.bindings._
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.schema._
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.{
  AMLInfoObject,
  AsyncAPI20ApiKeySecurityObject,
  AsyncAPI20Auth20SecurityObject,
  AsyncAPI20FlowObject,
  AsyncAPI20HttpApiKeySecurityObject,
  AsyncAPI20HttpSecurityObject,
  AsyncAPI20penIdConnectUrl,
  AsyncApiVariableObject,
  CorrelationIdObjectNode,
  Oauth2FlowObject,
  OperationTraitsObjectNode,
  ParameterObjectNode
}
import org.mulesoft.amfintegration.dialect.dialects.asyncapi26.bindings.{
  ChannelBinding26ObjectNode,
  GooglePubSubChannelBindingObject,
  GooglePubSubMessageStoragePolicyObject,
  GooglePubSubSchemaSettingsObject,
  IBMMQChannelBindingObject,
  IBMMQChannelQueueObject,
  IBMMQChannelTopicObject,
  MessageBinding26ObjectNode,
  OperationBinding26ObjectNode,
  ServerBinding26ObjectNode
}
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes._
import org.mulesoft.amfintegration.dialect.dialects.raml.raml10.Raml10TypesDialect

object AsyncApi26Dialect extends BaseDialect {
  private val _                        = Raml10TypesDialect().id // hack for ExampleNode.id
  override def DialectLocation: String = "file://vocabularies/dialects/asyncapi26.yaml"

  override val declares: Seq[DialectNode] = Seq(
    AsyncApi26ApiNode,
    AsyncApi26SecuritySettingsObject,
    AsyncApi26SecuritySchemeObject,
    AsyncAPI20ApiKeySecurityObject,
    AsyncAPI20HttpApiKeySecurityObject,
    AsyncAPI20HttpSecurityObject,
    AsyncAPI20Auth20SecurityObject,
    AsyncAPI20penIdConnectUrl,
    Oauth2FlowObject,
    AsyncAPI20FlowObject,
    Channel26Object,
    CorrelationIdObjectNode,
    Message26ObjectNode,
    ResponseMessage26ObjectNode,
    RequestMessage26ObjectNode,
    MessageTraits26ObjectNode,
    Operation26Object,
    OperationTraitsObjectNode,
    ParameterObjectNode,
    ServerBindingObjectNode,
    BaseShapeAsync2Node,
    AnyShapeAsync2Node,
    ArrayShapeAsync2Node,
    NodeShapeAsync2Node,
    NumberShapeAsync2Node,
    StringShapeAsync2Node,
    ChannelBinding26ObjectNode,
    ServerBinding26ObjectNode,
    MessageBinding26ObjectNode,
    OperationBinding26ObjectNode,
    ServerBindingsObjectNode,
    OperationBindingsObjectNode,
    ChannelBindingsObjectNode,
    MessageBindingsObjectNode,
    AmqpChannelBindingObject,
    AmqpChannel010BindingObject,
    AmqpChannel020BindingObject,
    WsChannelBindingObject,
    LastWillMqttServerBindingObject,
    KafkaServerBindingObject,
    MqttServerBindingObjectNode,
    AmqpMessageBindingObjectNode,
    KafkaMessageBindingObjectNode,
    KafkaMessageBinding010ObjectNode,
    KafkaMessageBinding030ObjectNode,
    MqttMessageBindingObjectNode,
    HttpMessageBindingObjectNode,
    Amqp091OperationBindingObjectNode,
    Amqp091OperationBinding010ObjectNode,
    Amqp091OperationBinding030ObjectNode,
    KafkaOperationBindingObjectNode,
    HttpOperationBindingObjectNode,
    MqttOperationBindingObjectNode,
    QueueAmqpChannelBinding,
    QueueAmqpChannel010Binding,
    QueueAmqpChannel020Binding,
    KafkaChannelBinding,
    Kafka030ChannelBinding,
    Kafka040ChannelBinding,
    Kafka050ChannelBinding,
    KafkaTopicConfiguration040Object,
    KafkaTopicConfiguration050Object,
    RoutingKeyAmqpChannelBinding,
    RoutingKeyAmqpChannel010Binding,
    RoutingKeyAmqpChannel020Binding,
    StringShapeAsync2Node,
    AMLExternalDocumentationObject,
    AMLInfoObject,
    AMLContactObject,
    AMLLicenseObject,
    AMLTagObject,
    AsyncApi26ServerObject,
    IBMMQChannelBindingObject,
    IBMMQChannelQueueObject,
    IBMMQChannelTopicObject,
    GooglePubSubChannelBindingObject,
    GooglePubSubMessageStoragePolicyObject,
    GooglePubSubSchemaSettingsObject
  )

  override def emptyDocument: DocumentsModel =
    DocumentsModel()
      .withId(DialectLocation + "#/documents")
      .withKeyProperty(true)
      .withReferenceStyle(ReferenceStyles.JSONSCHEMA)
      .withDeclarationsPath("components")

  override def encodes: DialectNode = AsyncApi26ApiNode

  override def declaredNodes: Map[String, DialectNode] = Map(
    "serverVariables"   -> AsyncApiVariableObject,
    "servers"           -> AsyncApi26ServerObject,
    "channels"          -> Channel26Object,
    "securitySchemes"   -> AsyncApi26SecuritySchemeObject,
    "messages"          -> Message26ObjectNode,
    "parameters"        -> ParameterObjectNode,
    "correlationIds"    -> CorrelationIdObjectNode,
    "operationTraits"   -> OperationTraitsObjectNode,
    "messageTraits"     -> MessageTraits26ObjectNode,
    "serverBindings"    -> ServerBindingsObjectNode,
    "channelBindings"   -> ChannelBindingsObjectNode,
    "operationBindings" -> OperationBindingsObjectNode,
    "messageBindings"   -> MessageBindingsObjectNode,
    "schemas"           -> BaseShapeAsync2Node
  )

  override protected val name: String    = "asyncapi"
  override protected val version: String = "2.6.0"
}
