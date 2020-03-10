package org.mulesoft.amfintegration.dialect.dialects.asyncapi20.bindings

import amf.core.vocabulary.Namespace.XsdTypes._
import amf.dialects.oas.nodes.DialectNode
import amf.plugins.document.vocabularies.model.domain.PropertyMapping
import amf.plugins.domain.webapi.metamodel.bindings.MessageBindingModel

object MessageBindingObjectNode extends BindingObjectNode {
  override def name: String = "MessageBindingObjectNode"

  override def nodeTypeMapping: String = MessageBindingModel.`type`.head.iri()

  override val httpNode: DialectNode  = HttpMessageBindingObjectNode
  override val wsNode: DialectNode    = NonPropsBindingPropertyNode
  override val kafkaNode: DialectNode = KafkaMessageBindingObjectNode
  override val amqpNode: DialectNode  = AmqpMessageBindingObjectNode
  override val amqp1Node: DialectNode = NonPropsBindingPropertyNode
  override val mqttNode: DialectNode  = MqttServerBindingObjectNode
  override val mqtt5Node: DialectNode = NonPropsBindingPropertyNode
  override val natsNode: DialectNode  = NonPropsBindingPropertyNode
  override val jmsNode: DialectNode   = NonPropsBindingPropertyNode
  override val snsNode: DialectNode   = NonPropsBindingPropertyNode
  override val sqsNode: DialectNode   = NonPropsBindingPropertyNode
  override val stompNode: DialectNode = NonPropsBindingPropertyNode
  override val redisNode: DialectNode = NonPropsBindingPropertyNode
}

object HttpMessageBindingObjectNode extends DialectNode {
  override def name: String = "HttpMessageBindingObjectNode"

  override def nodeTypeMapping: String = ???

  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/headers")
      .withName("headers")
      .withNodePropertyMapping("") // todo: http node mappings?
      .withObjectRange(Seq()), // todo: schema object
    PropertyMapping()
      .withId(location + s"#/declarations/$name/bindingVersion")
      .withName("bindingVersion")
      .withNodePropertyMapping("") // todo: http node mappings?
      .withLiteralRange(xsdString.iri())
  )
}

object KafkaMessageBindingObjectNode extends DialectNode {
  override def name: String = "KafkaMessageBindingObjectNode"

  override def nodeTypeMapping: String = ???

  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/key")
      .withName("key")
      .withNodePropertyMapping("") // todo: http node mappings?
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/bindingVersion")
      .withName("bindingVersion")
      .withNodePropertyMapping("") // todo: http node mappings?
      .withLiteralRange(xsdString.iri())
  )
}

object AmqpMessageBindingObjectNode extends DialectNode {
  override def name: String = "AmqpMessageBindingObjectNode"

  override def nodeTypeMapping: String = ???

  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/contentEncoding")
      .withName("contentEncoding")
      .withNodePropertyMapping("") // todo: http node mappings?
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/messageType")
      .withName("messageType")
      .withNodePropertyMapping("") // todo: http node mappings?
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/bindingVersion")
      .withName("bindingVersion")
      .withNodePropertyMapping("") // todo: http node mappings?
      .withLiteralRange(xsdString.iri())
  )
}

object MqttMessageBindingObjectNode extends DialectNode {
  override def name: String = "MqttMessageBindingObjectNode"

  override def nodeTypeMapping: String = ???

  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/bindingVersion")
      .withName("bindingVersion")
      .withNodePropertyMapping("") // todo: http node mappings?
      .withLiteralRange(xsdString.iri())
  )
}
