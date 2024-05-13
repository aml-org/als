package org.mulesoft.amfintegration.dialect.dialects.asyncapi20.bindings

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.apicontract.internal.metamodel.domain.bindings.{
  Amqp091MessageBindingModel,
  HttpMessageBindingModel,
  KafkaMessageBinding010Model,
  KafkaMessageBinding030Model,
  KafkaMessageBindingModel,
  MessageBindingModel,
  MessageBindingsModel,
  MqttMessageBindingModel
}
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.xsdString
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.DialectNode

object MessageBindingObjectNode extends BindingObjectNode {
  override def name: String = "MessageBindingObjectNode"

  override def nodeTypeMapping: String = MessageBindingModel.`type`.head.iri()

}

object MessageBindingsObjectNode extends DialectNode {
  override def name: String = "MessageBindingsObjectNode"

  override def nodeTypeMapping: String = MessageBindingsModel.`type`.head.iri()

  override def properties: Seq[PropertyMapping] = Nil
}

object HttpMessageBindingObjectNode extends DialectNode with BindingVersionPropertyMapping {
  override def name: String = "HttpMessageBindingObjectNode"

  override def nodeTypeMapping: String = HttpMessageBindingModel.`type`.head.iri()

  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/headers")
      .withName("headers")
      .withNodePropertyMapping(HttpMessageBindingModel.Headers.value.iri()) // todo: http node mappings?
      .withObjectRange(Seq())
  ) :+ bindingVersion
}

trait BaseKafkaMessageBindingObjectNode extends DialectNode with BindingVersionPropertyMapping {
  override def name: String = "KafkaMessageBindingObjectNode"

  override def nodeTypeMapping: String = KafkaMessageBindingModel.`type`.head.iri()

  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/key")
      .withName("key")
      .withNodePropertyMapping(KafkaMessageBindingModel.MessageKey.value.iri()) // todo: http node mappings?
      .withLiteralRange(xsdString.iri())
  ) :+ bindingVersion
}
object KafkaMessageBindingObjectNode extends BaseKafkaMessageBindingObjectNode
object KafkaMessageBinding010ObjectNode extends BaseKafkaMessageBindingObjectNode {
  override def nodeTypeMapping: String = KafkaMessageBinding010Model.`type`.head.iri()
}
object KafkaMessageBinding030ObjectNode extends BaseKafkaMessageBindingObjectNode {
  override def nodeTypeMapping: String = KafkaMessageBinding030Model.`type`.head.iri()

  override def properties: Seq[PropertyMapping] = super.properties ++ Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/schemaIdLocation")
      .withName("schemaIdLocation")
      .withNodePropertyMapping(KafkaMessageBinding030Model.SchemaIdLocation.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/schemaIdPayloadEncoding")
      .withName("schemaIdPayloadEncoding")
      .withNodePropertyMapping(KafkaMessageBinding030Model.SchemaIdPayloadEncoding.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/schemaLookupStrategy")
      .withName("schemaLookupStrategy")
      .withNodePropertyMapping(KafkaMessageBinding030Model.SchemaLookupStrategy.value.iri())
      .withLiteralRange(xsdString.iri())
  )
}

object AmqpMessageBindingObjectNode extends DialectNode with BindingVersionPropertyMapping {
  override def name: String = "AmqpMessageBindingObjectNode"

  override def nodeTypeMapping: String = Amqp091MessageBindingModel.`type`.head.iri()

  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/contentEncoding")
      .withName("contentEncoding")
      .withNodePropertyMapping(Amqp091MessageBindingModel.ContentEncoding.value.iri()) // todo: http node mappings?
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/messageType")
      .withName("messageType")
      .withNodePropertyMapping(Amqp091MessageBindingModel.MessageType.value.iri()) // todo: http node mappings?
      .withLiteralRange(xsdString.iri())
  ) :+ bindingVersion
}

object MqttMessageBindingObjectNode extends DialectNode with BindingVersionPropertyMapping {
  override def name: String = "MqttMessageBindingObjectNode"

  override def nodeTypeMapping: String = MqttMessageBindingModel.`type`.head.iri()

  override def properties: Seq[PropertyMapping] = Seq(bindingVersion)
}
