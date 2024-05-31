package org.mulesoft.amfintegration.dialect.dialects.asyncapi20.bindings

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.apicontract.internal.metamodel.domain.bindings.{
  Amqp091MessageBindingModel,
  HttpMessageBinding020Model,
  HttpMessageBinding030Model,
  HttpMessageBindingModel,
  KafkaMessageBinding010Model,
  KafkaMessageBinding030Model,
  KafkaMessageBindingModel,
  MessageBindingModel,
  MessageBindingsModel,
  MqttMessageBinding010Model,
  MqttMessageBinding020Model,
  MqttMessageBindingModel
}
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.{xsdInteger, xsdString}
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.bindings.AmqpMessageBindingObjectNode.{location, name}
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.schema.NodeShapeAsync2Node
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

object HttpMessageBinding20ObjectNode extends BaseHttpMessageBindingObjectNode {
  override def nodeTypeMapping: String = HttpMessageBinding020Model.`type`.head.iri()

}
object HttpMessageBinding30ObjectNode extends BaseHttpMessageBindingObjectNode {
  override def nodeTypeMapping: String = HttpMessageBinding030Model.`type`.head.iri()
  override def properties: Seq[PropertyMapping] = super.properties ++ Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/statusCode")
      .withName("statusCode")
      .withNodePropertyMapping(HttpMessageBinding030Model.StatusCode.value.iri())
      .withLiteralRange(xsdInteger.iri())
  )
}
trait BaseHttpMessageBindingObjectNode extends DialectNode with BindingVersionPropertyMapping {
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

object MqttMessageBinding10ObjectNode extends BaseMqttMessageBindingObjectNode {
  override def nodeTypeMapping: String = MqttMessageBinding010Model.`type`.head.iri()
}
object MqttMessageBinding20ObjectNode extends BaseMqttMessageBindingObjectNode {
  override def nodeTypeMapping: String = MqttMessageBinding020Model.`type`.head.iri()

  override def properties: Seq[PropertyMapping] = super.properties ++ Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/payloadFormatIndicator")
      .withName("payloadFormatIndicator")
      .withNodePropertyMapping(MqttMessageBinding020Model.PayloadFormatIndicator.value.iri())
      .withLiteralRange(xsdInteger.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/correlationData")
      .withName("correlationData")
      .withNodePropertyMapping(MqttMessageBinding020Model.CorrelationData.value.iri())
      .withObjectRange(Seq(NodeShapeAsync2Node.id)),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/contentType")
      .withName("contentType")
      .withNodePropertyMapping(MqttMessageBinding020Model.ContentType.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/responseTopic")
      .withName("responseTopic")
      .withNodePropertyMapping(MqttMessageBinding020Model.ResponseTopic.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/responseTopicSchema")
      .withName("responseTopicSchema")
      .withNodePropertyMapping(MqttMessageBinding020Model.ResponseTopicSchema.value.iri())
      .withObjectRange(Seq(NodeShapeAsync2Node.id))
  )
}
trait BaseMqttMessageBindingObjectNode extends DialectNode with BindingVersionPropertyMapping {
  override def name: String = "MqttMessageBindingObjectNode"

  override def nodeTypeMapping: String = MqttMessageBindingModel.`type`.head.iri()

  override def properties: Seq[PropertyMapping] = Seq(bindingVersion)
}
