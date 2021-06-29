package org.mulesoft.amfintegration.dialect.dialects.asyncapi20.bindings

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.apicontract.internal.metamodel.domain.bindings.{
  Amqp091ChannelBindingModel,
  Amqp091ChannelExchangeModel,
  Amqp091QueueModel,
  ChannelBindingModel,
  ChannelBindingsModel,
  WebSocketsChannelBindingModel
}
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.{xsdBoolean, xsdString}
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

object WsChannelBindingObject extends DialectNode {
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
      .withObjectRange(Seq(BaseShapeAsync2Node.id)), //id of schemas
    PropertyMapping()
      .withId(location + s"#/declarations/$name/headers")
      .withName("headers")
      .withNodePropertyMapping(WebSocketsChannelBindingModel.Headers.value.iri())
      .withObjectRange(Seq(BaseShapeAsync2Node.id)), //id of schemas
    PropertyMapping()
      .withId(location + s"#/declarations/$name/bindingVersion")
      .withName("bindingVersion")
      .withNodePropertyMapping(WebSocketsChannelBindingModel.BindingVersion.value.iri())
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
      .withNodePropertyMapping(Amqp091ChannelBindingModel.Is.value.iri())
      .withLiteralRange(xsdString.iri())
      .withEnum(Seq("queue", "routingKey"))
  )
}

object RoutingKeyAmqpChannelBinding extends DialectNode {
  override def name: String = "RoutingKeyAmqpChannelBinding"

  override def nodeTypeMapping: String = Amqp091ChannelExchangeModel.`type`.head.iri()

  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/name")
      .withName("name	")
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
      .withLiteralRange(xsdBoolean.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/vhost")
      .withName("vhost")
      .withNodePropertyMapping(Amqp091ChannelExchangeModel.VHost.value.iri())
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
      .withLiteralRange(xsdBoolean.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/vhost")
      .withName("vhost")
      .withNodePropertyMapping(Amqp091QueueModel.VHost.value.iri())
      .withLiteralRange(xsdString.iri())
  )
}
