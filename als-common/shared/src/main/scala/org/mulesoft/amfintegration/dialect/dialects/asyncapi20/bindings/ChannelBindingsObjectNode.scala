package org.mulesoft.amfintegration.dialect.dialects.asyncapi20.bindings

import amf.core.vocabulary.Namespace.XsdTypes._
import amf.dialects.oas.nodes.DialectNode
import amf.plugins.document.vocabularies.model.domain.PropertyMapping
import amf.plugins.domain.webapi.metamodel.bindings.ChannelBindingModel

object ChannelBindingsObjectNode extends BindingObjectNode {
  override val httpNode: DialectNode  = NonPropsBindingPropertyNode
  override val wsNode: DialectNode    = WsChannelBindingObject
  override val kafkaNode: DialectNode = NonPropsBindingPropertyNode
  override val amqpNode: DialectNode  = AmqpChannelBindingObject
  override val amqp1Node: DialectNode = NonPropsBindingPropertyNode
  override val mqttNode: DialectNode  = NonPropsBindingPropertyNode
  override val mqtt5Node: DialectNode = NonPropsBindingPropertyNode
  override val natsNode: DialectNode  = NonPropsBindingPropertyNode
  override val jmsNode: DialectNode   = NonPropsBindingPropertyNode
  override val snsNode: DialectNode   = NonPropsBindingPropertyNode
  override val sqsNode: DialectNode   = NonPropsBindingPropertyNode
  override val stompNode: DialectNode = NonPropsBindingPropertyNode
  override val redisNode: DialectNode = NonPropsBindingPropertyNode

  override def name: String = "ChannelBindingObjectNode"

  override def nodeTypeMapping: String = ChannelBindingModel.`type`.head.iri()
}

object WsChannelBindingObject extends DialectNode {
  override def name: String = "WsChannelBindingObject"

  override def nodeTypeMapping: String = ???

  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/method")
      .withName("method")
      .withNodePropertyMapping("") // todo: http node mappings?
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/query")
      .withName("query")
      .withNodePropertyMapping("") // todo: http node mappings?
      .withObjectRange(Seq()), //id of schemas
    PropertyMapping()
      .withId(location + s"#/declarations/$name/headers")
      .withName("headers")
      .withNodePropertyMapping("") // todo: http node mappings?
      .withObjectRange(Seq()), //id of schemas
    PropertyMapping()
      .withId(location + s"#/declarations/$name/bindingVersion")
      .withName("bindingVersion")
      .withNodePropertyMapping("") // todo: http node mappings?
      .withLiteralRange(xsdString.iri())
  )
}

object AmqpChannelBindingObject extends DialectNode {
  override def name: String = "AmqpChannelBindingObject"

  override def nodeTypeMapping: String = ???

  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/is")
      .withName("is")
      .withNodePropertyMapping("") // todo: http node mappings?
      .withLiteralRange(xsdString.iri())
      .withEnum(Seq("queue", "routingKey")),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/exchange")
      .withName("exchange")
      .withNodePropertyMapping("") // todo: http node mappings?
      .withObjectRange(Seq(RoutingKeyAmqpChannelBinding.id)),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/queue")
      .withName("queue")
      .withNodePropertyMapping("") // todo: http node mappings?
      .withObjectRange(Seq(QueueAmqpChannelBinding.id))
  )
}

object RoutingKeyAmqpChannelBinding extends DialectNode {
  override def name: String = "RoutingKeyAmqpChannelBinding"

  override def nodeTypeMapping: String = ""

  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/name")
      .withName("name	")
      .withNodePropertyMapping("") // todo: http node mappings?
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/type")
      .withName("type")
      .withNodePropertyMapping("") // todo: http node mappings?
      .withLiteralRange(xsdString.iri())
      .withEnum(Seq("topic", "direct", "fanout", "default", "headers")),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/durable")
      .withName("durable")
      .withNodePropertyMapping("") // todo: http node mappings?
      .withLiteralRange(xsdBoolean.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/autoDelete")
      .withName("autoDelete")
      .withNodePropertyMapping("") // todo: http node mappings?
      .withLiteralRange(xsdBoolean.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/vhost")
      .withName("vhost")
      .withNodePropertyMapping("") // todo: http node mappings?
      .withLiteralRange(xsdString.iri())
  )
}

object QueueAmqpChannelBinding extends DialectNode {
  override def name: String = "QueueAmqpChannelBinding"

  override def nodeTypeMapping: String = ???

  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/name")
      .withName("name")
      .withNodePropertyMapping("") // todo: http node mappings?
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/durable")
      .withName("durable")
      .withNodePropertyMapping("") // todo: http node mappings?
      .withLiteralRange(xsdBoolean.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/exclusive")
      .withName("exclusive")
      .withNodePropertyMapping("") // todo: http node mappings?
      .withLiteralRange(xsdBoolean.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/autoDelete")
      .withName("autoDelete")
      .withNodePropertyMapping("") // todo: http node mappings?
      .withLiteralRange(xsdBoolean.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/vhost")
      .withName("vhost")
      .withNodePropertyMapping("") // todo: http node mappings?
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/bindingVersion")
      .withName("bindingVersion")
      .withNodePropertyMapping("") // todo: http node mappings?
      .withLiteralRange(xsdString.iri())
  )
}
