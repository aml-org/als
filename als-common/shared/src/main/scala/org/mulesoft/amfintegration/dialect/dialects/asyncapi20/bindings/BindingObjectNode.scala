package org.mulesoft.amfintegration.dialect.dialects.asyncapi20.bindings

import amf.dialects.oas.nodes.DialectNode
import amf.plugins.document.vocabularies.model.domain.PropertyMapping

trait BindingObjectNode extends DialectNode {

  val httpNode:DialectNode
  val wsNode:DialectNode
  val kafkaNode:DialectNode
  val amqpNode:DialectNode
  val amqp1Node:DialectNode
  val mqttNode:DialectNode
  val mqtt5Node:DialectNode
  val natsNode:DialectNode
  val jmsNode:DialectNode
  val snsNode:DialectNode
  val sqsNode:DialectNode
  val stompNode:DialectNode
  val redisNode:DialectNode

  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/http")
      .withName("http")
      .withNodePropertyMapping("") // todo: http node mappings?
      .withObjectRange(Seq(httpNode.id)),

    PropertyMapping()
      .withId(location + s"#/declarations/$name/ws")
      .withName("ws")
      .withNodePropertyMapping("") // todo: http node mappings?
      .withObjectRange(Seq(wsNode.id)),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/amqp")
      .withName("amqp")
      .withNodePropertyMapping("") // todo: http node mappings?
      .withObjectRange(Seq(amqpNode.id)),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/amqp1")
      .withName("amqp1")
      .withNodePropertyMapping("") // todo: http node mappings?
      .withObjectRange(Seq(amqp1Node.id)),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/mqtt")
      .withName("mqtt")
      .withNodePropertyMapping("") // todo: http node mappings?
      .withObjectRange(Seq(mqttNode.id)),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/mqtt5")
      .withName("mqtt5")
      .withNodePropertyMapping("") // todo: http node mappings?
      .withObjectRange(Seq(mqtt5Node.id)),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/nats")
      .withName("nats")
      .withNodePropertyMapping("") // todo: http node mappings?
      .withObjectRange(Seq(natsNode.id)),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/jms")
      .withName("jms")
      .withNodePropertyMapping("") // todo: http node mappings?
      .withObjectRange(Seq(jmsNode.id)),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/sns")
      .withName("sns")
      .withNodePropertyMapping("") // todo: http node mappings?
      .withObjectRange(Seq(snsNode.id)),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/sqs")
      .withName("sqs")
      .withNodePropertyMapping("") // todo: http node mappings?
      .withObjectRange(Seq(sqsNode.id)),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/stomp")
      .withName("stomp")
      .withNodePropertyMapping("") // todo: http node mappings?
      .withObjectRange(Seq(stompNode.id)),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/redis")
      .withName("redis")
      .withNodePropertyMapping("") // todo: http node mappings?
      .withObjectRange(Seq(redisNode.id)),
    )
}

object NonPropsBindingPropertyNode extends DialectNode{
  override def name: String = "NonPropsBindingNode"

  override def nodeTypeMapping: String = "NonPropsBindingNode"

  override def properties: Seq[PropertyMapping] = Seq()
}