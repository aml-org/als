package org.mulesoft.amfintegration.dialect.dialects.asyncapi20.bindings

import amf.core.vocabulary.Namespace.XsdTypes._
import amf.dialects.oas.nodes.DialectNode
import amf.plugins.document.vocabularies.model.domain.PropertyMapping
import amf.plugins.domain.webapi.metamodel.bindings.{Amqp091ChannelBindingModel, Amqp091QueueModel, ChannelBindingModel, ChannelBindingsModel, WebSocketsChannelBindingModel}
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.bindings.AmqpChannelBindingObject.{location, name}

object ChannelBindingObjectNode extends BindingObjectNode {

  override def name: String = "ChannelBindingObjectNode"

  override def nodeTypeMapping: String = ChannelBindingModel.`type`.head.iri()
}

object ChannelBindingsObjectNode extends DialectNode{
  override def name: String = "ChannelBindingsObjectNode"

  override def nodeTypeMapping: String = ChannelBindingsModel.`type`.head.iri()

  override def properties: Seq[PropertyMapping] = Nil
}

object WsChannelBindingObject extends DialectNode {
  override def name: String = "WsChannelBindingObject"

  override def nodeTypeMapping: String = WebSocketsChannelBindingModel.`type`.head.iri()

  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/method")
      .withName("method")
      .withNodePropertyMapping(WebSocketsChannelBindingModel.Method.value.iri()) // todo: http node mappings?
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/query")
      .withName("query")
      .withNodePropertyMapping(WebSocketsChannelBindingModel.Query.value.iri()) // todo: http node mappings?
      .withObjectRange(Seq()), //id of schemas
    PropertyMapping()
      .withId(location + s"#/declarations/$name/headers")
      .withName("headers")
      .withNodePropertyMapping(WebSocketsChannelBindingModel.Headers.value.iri()) // todo: http node mappings?
      .withObjectRange(Seq()), //id of schemas
    PropertyMapping()
      .withId(location + s"#/declarations/$name/bindingVersion")
      .withName("bindingVersion")
      .withNodePropertyMapping(WebSocketsChannelBindingModel.BindingVersion.value.iri()) // todo: http node mappings?
      .withLiteralRange(xsdString.iri())
  )
}

object AmqpChannelBindingObject extends DialectNode {
  override def name: String = "AmqpChannelBindingObject"

  override def nodeTypeMapping: String = Amqp091ChannelBindingModel.`type`.head.iri()

  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/is")
      .withName("is")
      .withNodePropertyMapping(Amqp091ChannelBindingModel.Is.value.iri()) // todo: http node mappings?
      .withLiteralRange(xsdString.iri())
      .withEnum(Seq("queue", "routingKey"))
  )
}

object RoutingKeyAmqpChannelBinding extends DialectNode {
  override def name: String = "RoutingKeyAmqpChannelBinding"

  override def nodeTypeMapping: String = Amqp091QueueModel.`type`.head.iri()

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

  override def nodeTypeMapping: String = Amqp091QueueModel.`type`.head.iri()

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
