package org.mulesoft.amfintegration.dialect.dialects.asyncapi20.bindings
import amf.core.vocabulary.Namespace.XsdTypes._
import amf.dialects.oas.nodes.DialectNode
import amf.plugins.document.vocabularies.model.domain.PropertyMapping
import amf.plugins.domain.webapi.metamodel.bindings.ServerBindingModel

object ServerBindingObjectNode extends BindingObjectNode {

  override val httpNode: DialectNode  = NonPropsBindingPropertyNode
  override val wsNode: DialectNode    = NonPropsBindingPropertyNode
  override val kafkaNode: DialectNode = NonPropsBindingPropertyNode
  override val amqpNode: DialectNode  = NonPropsBindingPropertyNode
  override val amqp1Node: DialectNode = NonPropsBindingPropertyNode
  override val mqttNode: DialectNode  = MqttServerBindingObjectNode
  override val mqtt5Node: DialectNode = NonPropsBindingPropertyNode
  override val natsNode: DialectNode  = NonPropsBindingPropertyNode
  override val jmsNode: DialectNode   = NonPropsBindingPropertyNode
  override val snsNode: DialectNode   = NonPropsBindingPropertyNode
  override val sqsNode: DialectNode   = NonPropsBindingPropertyNode
  override val stompNode: DialectNode = NonPropsBindingPropertyNode
  override val redisNode: DialectNode = NonPropsBindingPropertyNode

  override def name: String = "ServerBindingObjectNode"

  override def nodeTypeMapping: String = ServerBindingModel.`type`.head.iri()
}

object MqttServerBindingObjectNode extends DialectNode {
  override def name: String = "MqttServerBindingObjectNode"

  override def nodeTypeMapping: String = ???

  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/clientId")
      .withName("clientId")
      .withNodePropertyMapping("") // todo: http node mappings?
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/cleanSession")
      .withName("cleanSession")
      .withNodePropertyMapping("") // todo: http node mappings?
      .withLiteralRange(xsdBoolean.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/lastWill")
      .withName("lastWill")
      .withNodePropertyMapping("") // todo: http node mappings?
      .withObjectRange(Seq("MockLastWillObject.id")),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/lastWill.topic")
      .withName("lastWill.topic")
      .withNodePropertyMapping("") // todo: http node mappings?
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/lastWill.qos")
      .withName("lastWill.qos")
      .withNodePropertyMapping("") // todo: http node mappings?
      .withLiteralRange(xsdInteger.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/lastWill.retain")
      .withName("lastWill.retain")
      .withNodePropertyMapping("") // todo: http node mappings?
      .withLiteralRange(xsdBoolean.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/keepAlive")
      .withName("keepAlive")
      .withNodePropertyMapping("") // todo: http node mappings?
      .withLiteralRange(xsdInteger.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/bindingVersion")
      .withName("bindingVersion")
      .withNodePropertyMapping("") // todo: http node mappings?
      .withLiteralRange(xsdString.iri())
  )
}
