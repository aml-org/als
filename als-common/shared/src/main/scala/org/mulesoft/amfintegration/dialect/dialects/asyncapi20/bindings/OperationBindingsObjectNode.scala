package org.mulesoft.amfintegration.dialect.dialects.asyncapi20.bindings

import amf.core.vocabulary.Namespace.XsdTypes._
import amf.dialects.oas.nodes.DialectNode
import amf.plugins.document.vocabularies.model.domain.PropertyMapping
import amf.plugins.domain.webapi.metamodel.bindings.OperationBindingModel

object OperationBindingsObjectNode extends BindingObjectNode {
  override val httpNode: DialectNode  = HttpOperationBindingObjectNode
  override val wsNode: DialectNode    = NonPropsBindingPropertyNode
  override val kafkaNode: DialectNode = KafkaOperationBindingObjectNode
  override val amqpNode: DialectNode  = AmqpOperationBindingObjectNode
  override val amqp1Node: DialectNode = NonPropsBindingPropertyNode
  override val mqttNode: DialectNode  = MqttOperationBindingObjectNode
  override val mqtt5Node: DialectNode = NonPropsBindingPropertyNode
  override val natsNode: DialectNode  = NonPropsBindingPropertyNode
  override val jmsNode: DialectNode   = NonPropsBindingPropertyNode
  override val snsNode: DialectNode   = NonPropsBindingPropertyNode
  override val sqsNode: DialectNode   = NonPropsBindingPropertyNode
  override val stompNode: DialectNode = NonPropsBindingPropertyNode
  override val redisNode: DialectNode = NonPropsBindingPropertyNode

  override def name: String = "OperationBindingObjectNode"

  override def nodeTypeMapping: String = OperationBindingModel.`type`.head.iri()
}

object HttpOperationBindingObjectNode extends DialectNode {
  override def name: String = "HttpOperationBindingObjectNode"

  override def nodeTypeMapping: String = ???

  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/type")
      .withName("type")
      .withNodePropertyMapping("") // todo: http node mappings?
      .withLiteralRange(xsdString.iri())
      .withEnum(Seq("request", "response")),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/method")
      .withName("method")
      .withNodePropertyMapping("") // todo: http node mappings?
      .withLiteralRange(xsdString.iri())
      .withEnum(Seq("GET", "POST", "PUT", "PATCH", "DELETE", "HEAD", "OPTIONS", "CONNECT", "TRACE")),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/method")
      .withName("method")
      .withNodePropertyMapping("") // todo: http node mappings?
      .withObjectRange(Seq()), // schema id
    PropertyMapping()
      .withId(location + s"#/declarations/$name/bindingVersion")
      .withName("bindingVersion")
      .withNodePropertyMapping("") // todo: http node mappings?
      .withLiteralRange(xsdString.iri())
  )
}

object KafkaOperationBindingObjectNode extends DialectNode {
  override def name: String = "KafkaOperationBindingObjectNode"

  override def nodeTypeMapping: String = ???

  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/groupId")
      .withName("groupId")
      .withNodePropertyMapping("") // todo: http node mappings?
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/clientId")
      .withName("clientId")
      .withNodePropertyMapping("") // todo: http node mappings?
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/bindingVersion")
      .withName("bindingVersion")
      .withNodePropertyMapping("") // todo: http node mappings?
      .withLiteralRange(xsdString.iri())
  )
}

object AmqpOperationBindingObjectNode extends DialectNode {
  override def name: String = "AmqpOperationBindingObjectNode"

  override def nodeTypeMapping: String = ???

  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/expiration")
      .withName("expiration")
      .withNodePropertyMapping("") // todo: http node mappings?
      .withLiteralRange(xsdInteger.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/userId")
      .withName("userId")
      .withNodePropertyMapping("") // todo: http node mappings?
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/cc")
      .withName("cc")
      .withNodePropertyMapping("") // todo: http node mappings?
      .withAllowMultiple(true)
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/priority")
      .withName("priority")
      .withNodePropertyMapping("") // todo: http node mappings?
      .withLiteralRange(xsdInteger.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/deliveryMode")
      .withName("deliveryMode")
      .withNodePropertyMapping("") // todo: http node mappings?
      .withLiteralRange(xsdInteger.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/mandatory")
      .withName("mandatory")
      .withNodePropertyMapping("") // todo: http node mappings?
      .withAllowMultiple(true)
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/bcc")
      .withName("bcc")
      .withNodePropertyMapping("") // todo: http node mappings?
      .withAllowMultiple(true)
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/replyTo")
      .withName("replyTo")
      .withNodePropertyMapping("") // todo: http node mappings?
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/timestamp")
      .withName("timestamp")
      .withNodePropertyMapping("") // todo: http node mappings?
      .withLiteralRange(xsdBoolean.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/ack")
      .withName("ack")
      .withNodePropertyMapping("") // todo: http node mappings?
      .withLiteralRange(xsdBoolean.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/bindingVersion")
      .withName("bindingVersion")
      .withNodePropertyMapping("") // todo: http node mappings?
      .withLiteralRange(xsdString.iri())
  )
}

object MqttOperationBindingObjectNode extends DialectNode {
  override def name: String = "MqttOperationBindingObjectNode"

  override def nodeTypeMapping: String = ???

  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/qos")
      .withName("qos")
      .withNodePropertyMapping("") // todo: http node mappings?
      .withLiteralRange(xsdInteger.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/retain")
      .withName("retain")
      .withNodePropertyMapping("") // todo: http node mappings?
      .withLiteralRange(xsdBoolean.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/bindingVersion")
      .withName("bindingVersion")
      .withNodePropertyMapping("") // todo: http node mappings?
      .withLiteralRange(xsdString.iri())
  )
}
