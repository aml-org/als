package org.mulesoft.amfintegration.dialect.dialects.asyncapi20.bindings

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.apicontract.internal.metamodel.domain.bindings.{
  Amqp091OperationBindingModel,
  HttpOperationBindingModel,
  KafkaOperationBindingModel,
  MqttOperationBindingModel,
  OperationBindingModel,
  OperationBindingsModel
}
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.{xsdBoolean, xsdInteger, xsdString}
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.DialectNode

object OperationBindingObjectNode extends BindingObjectNode {

  override def name: String = "OperationBindingObjectNode"

  override def nodeTypeMapping: String = OperationBindingModel.`type`.head.iri()
}

object OperationBindingsObjectNode extends DialectNode {
  override def name: String = "OperationBindingsObjectNode"

  override def nodeTypeMapping: String = OperationBindingsModel.`type`.head.iri()

  override def properties: Seq[PropertyMapping] = Nil
}

object HttpOperationBindingObjectNode extends DialectNode {
  override def name: String = "HttpOperationBindingObjectNode"

  override def nodeTypeMapping: String = HttpOperationBindingModel.`type`.head.iri()

  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/type")
      .withName("type")
      .withNodePropertyMapping(HttpOperationBindingModel.OperationType.value.iri()) // todo: http node mappings?
      .withLiteralRange(xsdString.iri())
      .withEnum(Seq("request", "response")),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/method")
      .withName("method")
      .withNodePropertyMapping(HttpOperationBindingModel.Method.value.iri()) // todo: http node mappings?
      .withLiteralRange(xsdString.iri())
      .withEnum(Seq("GET", "POST", "PUT", "PATCH", "DELETE", "HEAD", "OPTIONS", "CONNECT", "TRACE")),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/bindingVersion")
      .withName("bindingVersion")
      .withNodePropertyMapping(HttpOperationBindingModel.BindingVersion.value.iri()) // todo: http node mappings?
      .withLiteralRange(xsdString.iri())
  )
}

object KafkaOperationBindingObjectNode extends DialectNode {
  override def name: String = "KafkaOperationBindingObjectNode"

  override def nodeTypeMapping: String = KafkaOperationBindingModel.`type`.head.iri()

  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/groupId")
      .withName("groupId")
      .withNodePropertyMapping(KafkaOperationBindingModel.GroupId.value.iri()) // todo: http node mappings?
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/clientId")
      .withName("clientId")
      .withNodePropertyMapping(KafkaOperationBindingModel.ClientId.value.iri()) // todo: http node mappings?
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/bindingVersion")
      .withName("bindingVersion")
      .withNodePropertyMapping(KafkaOperationBindingModel.BindingVersion.value.iri()) // todo: http node mappings?
      .withLiteralRange(xsdString.iri())
  )
}

object AmqpOperationBindingObjectNode extends DialectNode {
  override def name: String = "AmqpOperationBindingObjectNode"

  override def nodeTypeMapping: String = Amqp091OperationBindingModel.`type`.head.iri()

  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/expiration")
      .withName("expiration")
      .withNodePropertyMapping(Amqp091OperationBindingModel.Expiration.value.iri()) // todo: http node mappings?
      .withLiteralRange(xsdInteger.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/userId")
      .withName("userId")
      .withNodePropertyMapping(Amqp091OperationBindingModel.UserId.value.iri()) // todo: http node mappings?
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/cc")
      .withName("cc")
      .withNodePropertyMapping(Amqp091OperationBindingModel.CC.value.iri()) // todo: http node mappings?
      .withAllowMultiple(true)
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/priority")
      .withName("priority")
      .withNodePropertyMapping(Amqp091OperationBindingModel.Priority.value.iri()) // todo: http node mappings?
      .withLiteralRange(xsdInteger.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/deliveryMode")
      .withName("deliveryMode")
      .withNodePropertyMapping(Amqp091OperationBindingModel.DeliveryMode.value.iri()) // todo: http node mappings?
      .withLiteralRange(xsdInteger.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/mandatory")
      .withName("mandatory")
      .withNodePropertyMapping(Amqp091OperationBindingModel.Mandatory.value.iri()) // todo: http node mappings?
      .withAllowMultiple(true)
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/bcc")
      .withName("bcc")
      .withNodePropertyMapping(Amqp091OperationBindingModel.BCC.value.iri()) // todo: http node mappings?
      .withAllowMultiple(true)
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/timestamp")
      .withName("timestamp")
      .withNodePropertyMapping(Amqp091OperationBindingModel.Timestamp.value.iri()) // todo: http node mappings?
      .withLiteralRange(xsdBoolean.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/ack")
      .withName("ack")
      .withNodePropertyMapping(Amqp091OperationBindingModel.Ack.value.iri()) // todo: http node mappings?
      .withLiteralRange(xsdBoolean.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/bindingVersion")
      .withName("bindingVersion")
      .withNodePropertyMapping(Amqp091OperationBindingModel.BindingVersion.value.iri()) // todo: http node mappings?
      .withLiteralRange(xsdString.iri())
  )
}

object MqttOperationBindingObjectNode extends DialectNode {
  override def name: String = "MqttOperationBindingObjectNode"

  override def nodeTypeMapping: String = MqttOperationBindingModel.`type`.head.iri()

  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/qos")
      .withName("qos")
      .withNodePropertyMapping(MqttOperationBindingModel.Qos.value.iri()) // todo: http node mappings?
      .withLiteralRange(xsdInteger.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/retain")
      .withName("retain")
      .withNodePropertyMapping(MqttOperationBindingModel.Retain.value.iri()) // todo: http node mappings?
      .withLiteralRange(xsdBoolean.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/bindingVersion")
      .withName("bindingVersion")
      .withNodePropertyMapping(MqttOperationBindingModel.BindingVersion.value.iri()) // todo: http node mappings?
      .withLiteralRange(xsdString.iri())
  )
}
