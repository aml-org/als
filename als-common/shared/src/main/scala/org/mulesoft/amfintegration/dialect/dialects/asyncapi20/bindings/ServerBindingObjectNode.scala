package org.mulesoft.amfintegration.dialect.dialects.asyncapi20.bindings
import amf.aml.client.scala.model.domain.PropertyMapping
import amf.apicontract.internal.metamodel.domain.bindings.{
  KafkaServerBindingModel,
  MqttServerBindingModel,
  MqttServerLastWillModel,
  ServerBindingModel,
  ServerBindingsModel
}
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.{xsdBoolean, xsdInteger, xsdString}
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.DialectNode

object ServerBindingObjectNode extends BindingObjectNode {
  override def name: String = "ServerBindingObjectNode"

  override def nodeTypeMapping: String = ServerBindingModel.`type`.head.iri()
}

object ServerBindingsObjectNode extends DialectNode {
  override def name: String = "ServerBindingsObjectNode"

  override def nodeTypeMapping: String = ServerBindingsModel.`type`.head.iri()

  override def properties: Seq[PropertyMapping] = Nil
}

object MqttServerBindingObjectNode extends DialectNode {
  override def name: String = "MqttServerBindingObjectNode"

  override def nodeTypeMapping: String = MqttServerBindingModel.`type`.head.iri()

  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/clientId")
      .withName("clientId")
      .withNodePropertyMapping(MqttServerBindingModel.ClientId.value.iri()) // todo: http node mappings?
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/cleanSession")
      .withName("cleanSession")
      .withNodePropertyMapping(MqttServerBindingModel.CleanSession.value.iri()) // todo: http node mappings?
      .withLiteralRange(xsdBoolean.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/lastWill")
      .withName("lastWill")
      .withNodePropertyMapping(MqttServerBindingModel.LastWill.value.iri()) // todo: http node mappings?
      .withObjectRange(Seq(LastWillMqttServerBindingObject.id)),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/keepAlive")
      .withName("keepAlive")
      .withNodePropertyMapping(MqttServerBindingModel.KeepAlive.value.iri()) // todo: http node mappings?
      .withLiteralRange(xsdInteger.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/bindingVersion")
      .withName("bindingVersion")
      .withNodePropertyMapping(MqttServerBindingModel.BindingVersion.value.iri()) // todo: http node mappings?
      .withLiteralRange(xsdString.iri())
  )
}

object LastWillMqttServerBindingObject extends DialectNode {
  override def name: String = "LastWillMqttServerBindingModel"

  override def nodeTypeMapping: String = MqttServerLastWillModel.`type`.head.iri()

  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/topic")
      .withName("topic")
      .withNodePropertyMapping(MqttServerLastWillModel.Topic.value.iri()) // todo: http node mappings?
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/qos")
      .withName("qos")
      .withNodePropertyMapping(MqttServerLastWillModel.Qos.value.iri()) // todo: http node mappings?
      .withLiteralRange(xsdInteger.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/retain")
      .withName("retain")
      .withNodePropertyMapping(MqttServerLastWillModel.Retain.value.iri()) // todo: http node mappings?
      .withLiteralRange(xsdBoolean.iri())
  )
}

object KafkaServerBindingObject extends DialectNode {
  override def name: String = "KafkaServerBindingObject"

  override def nodeTypeMapping: String = KafkaServerBindingModel.`type`.head.iri()

  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/schemaRegistryUrl")
      .withName("schemaRegistryUrl")
      .withNodePropertyMapping(KafkaServerBindingModel.SchemaRegistryUrl.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/schemaRegistryVendor")
      .withName("schemaRegistryVendor")
      .withNodePropertyMapping(KafkaServerBindingModel.SchemaRegistryVendor.value.iri())
      .withLiteralRange(xsdString.iri())
  )
}
