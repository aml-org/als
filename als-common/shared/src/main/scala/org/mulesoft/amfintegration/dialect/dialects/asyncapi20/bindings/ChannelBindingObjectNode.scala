package org.mulesoft.amfintegration.dialect.dialects.asyncapi20.bindings

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.apicontract.internal.metamodel.domain.bindings.{
  Amqp091ChannelBinding010Model,
  Amqp091ChannelBinding020Model,
  Amqp091ChannelBindingModel,
  Amqp091ChannelExchange010Model,
  Amqp091ChannelExchange020Model,
  Amqp091ChannelExchangeModel,
  Amqp091Queue010Model,
  Amqp091Queue020Model,
  Amqp091QueueModel,
  ChannelBindingModel,
  ChannelBindingsModel,
  KafkaChannelBinding030Model,
  KafkaChannelBinding040Model,
  KafkaChannelBinding050Model,
  KafkaChannelBindingModel,
  KafkaTopicConfiguration040Model,
  KafkaTopicConfiguration050Model,
  KafkaTopicConfigurationModel,
  WebSocketsChannelBindingModel
}
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.{xsdBoolean, xsdInteger, xsdString}
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.schema.BaseShapeAsync2Node
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.DialectNode

object ChannelBindingObjectNode extends BindingObjectNode {

  override def name: String = "ChannelBindingObjectNode"

  override def nodeTypeMapping: String = ChannelBindingModel.`type`.head.iri()
}

object ChannelBindingsObjectNode extends DialectNode {
  override def name: String = "ChannelBindingsObjectNode"

  override def nodeTypeMapping: String = ChannelBindingsModel.`type`.head.iri()

  override def properties: Seq[PropertyMapping] = Nil
}

object WsChannelBindingObject extends DialectNode with BindingVersionPropertyMapping {
  override def name: String = "WsChannelBindingObject"

  override def nodeTypeMapping: String = WebSocketsChannelBindingModel.`type`.head.iri()

  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/method")
      .withName("method")
      .withNodePropertyMapping(WebSocketsChannelBindingModel.Method.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/query")
      .withName("query")
      .withNodePropertyMapping(WebSocketsChannelBindingModel.Query.value.iri())
      .withObjectRange(Seq(BaseShapeAsync2Node.id)), // id of schemas
    PropertyMapping()
      .withId(location + s"#/declarations/$name/headers")
      .withName("headers")
      .withNodePropertyMapping(WebSocketsChannelBindingModel.Headers.value.iri())
      .withObjectRange(Seq(BaseShapeAsync2Node.id)) // id of schemas
  ) :+ bindingVersion
}

trait BaseAmqpChannelBindingObject extends DialectNode with BindingVersionPropertyMapping {
  override def name: String = "AmqpChannelBindingObject"

  override def nodeTypeMapping: String = Amqp091ChannelBindingModel.`type`.head.iri()

  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/is")
      .withName("is")
      .withNodePropertyMapping(Amqp091ChannelBindingModel.Is.value.iri())
      .withLiteralRange(xsdString.iri())
      .withEnum(Seq("queue", "routingKey"))
  ) :+ bindingVersion
}

object AmqpChannelBindingObject extends BaseAmqpChannelBindingObject
object AmqpChannel010BindingObject extends BaseAmqpChannelBindingObject {
  override def nodeTypeMapping: String = Amqp091ChannelBinding010Model.`type`.head.iri()

}
object AmqpChannel020BindingObject extends BaseAmqpChannelBindingObject {
  override def nodeTypeMapping: String = Amqp091ChannelBinding020Model.`type`.head.iri()

}

trait BaseRoutingKeyAmqpChannelBinding extends DialectNode {
  override def name: String = "RoutingKeyAmqpChannelBinding"

  override def nodeTypeMapping: String = Amqp091ChannelExchangeModel.`type`.head.iri()

  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/name")
      .withName("name")
      .withNodePropertyMapping(Amqp091ChannelExchangeModel.Name.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/type")
      .withName("type")
      .withNodePropertyMapping(Amqp091ChannelExchangeModel.Type.value.iri())
      .withLiteralRange(xsdString.iri())
      .withEnum(Seq("topic", "direct", "fanout", "default", "headers")),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/durable")
      .withName("durable")
      .withNodePropertyMapping(Amqp091ChannelExchangeModel.Durable.value.iri())
      .withLiteralRange(xsdBoolean.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/autoDelete")
      .withName("autoDelete")
      .withNodePropertyMapping(Amqp091ChannelExchangeModel.AutoDelete.value.iri())
      .withLiteralRange(xsdBoolean.iri())
  )
}

object RoutingKeyAmqpChannelBinding extends BaseRoutingKeyAmqpChannelBinding
object RoutingKeyAmqpChannel010Binding extends BaseRoutingKeyAmqpChannelBinding {
  override def nodeTypeMapping: String = Amqp091ChannelExchange010Model.`type`.head.iri()

}

object RoutingKeyAmqpChannel020Binding extends BaseRoutingKeyAmqpChannelBinding {
  override def nodeTypeMapping: String = Amqp091ChannelExchange020Model.`type`.head.iri()

  override def properties: Seq[PropertyMapping] = super.properties ++ Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/vhost")
      .withName("vhost")
      .withNodePropertyMapping(Amqp091ChannelExchange020Model.VHost.value.iri())
      .withLiteralRange(xsdString.iri())
  )
}

trait BaseQueueAmqpChannelBinding extends DialectNode {
  override def name: String = "QueueAmqpChannelBinding"

  override def nodeTypeMapping: String = Amqp091QueueModel.`type`.head.iri()

  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/name")
      .withName("name")
      .withNodePropertyMapping(Amqp091QueueModel.Name.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/durable")
      .withName("durable")
      .withNodePropertyMapping(Amqp091QueueModel.Durable.value.iri())
      .withLiteralRange(xsdBoolean.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/exclusive")
      .withName("exclusive")
      .withNodePropertyMapping(Amqp091QueueModel.Exclusive.value.iri())
      .withLiteralRange(xsdBoolean.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/autoDelete")
      .withName("autoDelete")
      .withNodePropertyMapping(Amqp091QueueModel.AutoDelete.value.iri())
      .withLiteralRange(xsdBoolean.iri())
  )
}

object QueueAmqpChannelBinding extends BaseQueueAmqpChannelBinding
object QueueAmqpChannel010Binding extends BaseQueueAmqpChannelBinding {
  override def nodeTypeMapping: String = Amqp091Queue010Model.`type`.head.iri()
}
object QueueAmqpChannel020Binding extends BaseQueueAmqpChannelBinding {
  override def nodeTypeMapping: String = Amqp091Queue020Model.`type`.head.iri()
  override def properties: Seq[PropertyMapping] = super.properties ++ Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/vhost")
      .withName("vhost")
      .withNodePropertyMapping(Amqp091Queue020Model.VHost.value.iri())
      .withLiteralRange(xsdString.iri())
  )
}

trait BaseKafkaChannelBinding extends DialectNode with BindingVersionPropertyMapping {
  override def name: String = "KafkaChannelBinding"

  override def nodeTypeMapping: String = KafkaChannelBindingModel.`type`.head.iri()

  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/topic")
      .withName("topic")
      .withNodePropertyMapping(KafkaChannelBindingModel.Topic.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/partitions")
      .withName("partitions")
      .withNodePropertyMapping(KafkaChannelBindingModel.Partitions.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/replicas")
      .withName("replicas")
      .withNodePropertyMapping(KafkaChannelBindingModel.Replicas.value.iri())
      .withLiteralRange(xsdString.iri())
  ) :+ bindingVersion
}

object KafkaChannelBinding extends BaseKafkaChannelBinding
object Kafka030ChannelBinding extends BaseKafkaChannelBinding {
  override def nodeTypeMapping: String = KafkaChannelBinding030Model.`type`.head.iri()
}
object Kafka040ChannelBinding extends BaseKafkaChannelBinding {
  override def nodeTypeMapping: String = KafkaChannelBinding040Model.`type`.head.iri()

  override def properties: Seq[PropertyMapping] = super.properties ++ Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/topicConfiguration040")
      .withName("topicConfiguration")
      .withNodePropertyMapping(KafkaChannelBinding040Model.TopicConfiguration.value.iri())
      .withObjectRange(Seq(KafkaTopicConfiguration040Object.id))
  )
}
object Kafka050ChannelBinding extends BaseKafkaChannelBinding {
  override def nodeTypeMapping: String = KafkaChannelBinding050Model.`type`.head.iri()

  override def properties: Seq[PropertyMapping] = super.properties ++ Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/topicConfiguration050")
      .withName("topicConfiguration")
      .withNodePropertyMapping(KafkaChannelBinding050Model.TopicConfiguration.value.iri())
      .withObjectRange(Seq(KafkaTopicConfiguration050Object.id))
  )
}

trait BaseKafkaTopicConfigurationObject extends DialectNode {
  override def name: String            = "BaseKafkaTopicConfigurationObject"
  override def nodeTypeMapping: String = KafkaTopicConfigurationModel.`type`.head.iri()
  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/cleanupPolicy")
      .withName("cleanupPolicy")
      .withNodePropertyMapping(KafkaTopicConfigurationModel.CleanupPolicy.value.iri())
      .withAllowMultiple(true)
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/retentionMs")
      .withName("retentionMs")
      .withNodePropertyMapping(KafkaTopicConfigurationModel.RetentionMs.value.iri())
      .withLiteralRange(xsdInteger.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/retentionBytes")
      .withName("retentionBytes")
      .withNodePropertyMapping(KafkaTopicConfigurationModel.RetentionBytes.value.iri())
      .withLiteralRange(xsdInteger.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/deleteRetentionMs")
      .withName("deleteRetentionMs")
      .withNodePropertyMapping(KafkaTopicConfigurationModel.DeleteRetentionMs.value.iri())
      .withLiteralRange(xsdInteger.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/maxMessageBytes")
      .withName("maxMessageBytes")
      .withNodePropertyMapping(KafkaTopicConfigurationModel.MaxMessageBytes.value.iri())
      .withLiteralRange(xsdInteger.iri())
  )
}
object KafkaTopicConfiguration040Object extends BaseKafkaTopicConfigurationObject {
  override def nodeTypeMapping: String = KafkaTopicConfiguration040Model.`type`.head.iri()
}
object KafkaTopicConfiguration050Object extends BaseKafkaTopicConfigurationObject {
  override def nodeTypeMapping: String = KafkaTopicConfiguration050Model.`type`.head.iri()

  override def properties: Seq[PropertyMapping] = super.properties ++ Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/confluentKeySchemaValidation")
      .withName("confluentKeySchemaValidation")
      .withNodePropertyMapping(KafkaTopicConfiguration050Model.ConfluentKeySchemaValidation.value.iri())
      .withLiteralRange(xsdBoolean.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/confluentKeySubjectNameStrategy")
      .withName("confluentKeySubjectNameStrategy")
      .withNodePropertyMapping(KafkaTopicConfiguration050Model.ConfluentKeySubjectNameStrategy.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/confluentValueSchemaValidation")
      .withName("confluentValueSchemaValidation")
      .withNodePropertyMapping(KafkaTopicConfiguration050Model.ConfluentValueSchemaValidation.value.iri())
      .withLiteralRange(xsdBoolean.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/confluentValueSubjectNameStrategy")
      .withName("confluentValueSubjectNameStrategy")
      .withNodePropertyMapping(KafkaTopicConfiguration050Model.ConfluentValueSubjectNameStrategy.value.iri())
      .withLiteralRange(xsdString.iri())
  )
}
