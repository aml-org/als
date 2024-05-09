package org.mulesoft.amfintegration.dialect.dialects.asyncapi26.bindings

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.apicontract.internal.metamodel.domain.bindings.{
  AnypointMQMessageBindingModel,
  GooglePubSubMessageBindingModel,
  GooglePubSubSchemaDefinitionModel,
  IBMMQMessageBindingModel,
  MessageBindingModel
}
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.{xsdInteger, xsdString}
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.schema.BaseShapeAsync2Node
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.DialectNode

object MessageBinding26ObjectNode extends BindingObjectNode26 {
  override protected def keys: Seq[String] = super.keys ++ Seq(
    "ibmmq",
    "googlepubsub",
    "anypointmq"
  )
  override def name: String = "MessageBindingObjectNode"

  override def nodeTypeMapping: String = MessageBindingModel.`type`.head.iri()

}

object IBMMQMessageBindingObject extends DialectNode {
  override def name: String = "IBMMQMessageBindingObject"

  override def nodeTypeMapping: String = IBMMQMessageBindingModel.`type`.head.iri()

  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/messageType")
      .withName("messageType")
      .withNodePropertyMapping(IBMMQMessageBindingModel.MessageType.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/headers")
      .withName("headers")
      .withNodePropertyMapping(IBMMQMessageBindingModel.Headers.value.iri())
      .withAllowMultiple(true)
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/description")
      .withName("description")
      .withNodePropertyMapping(IBMMQMessageBindingModel.Description.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/expiry")
      .withName("expiry")
      .withNodePropertyMapping(IBMMQMessageBindingModel.Expiry.value.iri())
      .withLiteralRange(xsdInteger.iri())
  )
}

object GooglePubSubMessageBindingObject extends DialectNode {
  override def name: String = "GooglePubSubMessageBindingObject"

  override def nodeTypeMapping: String = GooglePubSubMessageBindingModel.`type`.head.iri()

  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/attributes")
      .withName("attributes")
      .withNodePropertyMapping(GooglePubSubMessageBindingModel.Attributes.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/orderingKey")
      .withName("orderingKey")
      .withNodePropertyMapping(GooglePubSubMessageBindingModel.OrderingKey.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/schema")
      .withName("schema")
      .withNodePropertyMapping(GooglePubSubMessageBindingModel.Schema.value.iri())
      .withObjectRange(Seq(GooglePubSubSchemaDefinitionObject.id))
  )
}
object GooglePubSubSchemaDefinitionObject extends DialectNode {
  override def name: String = "GooglePubSubSchemaDefinitionObject"

  override def nodeTypeMapping: String = GooglePubSubSchemaDefinitionModel.`type`.head.iri()

  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/name")
      .withName("name")
      .withNodePropertyMapping(GooglePubSubSchemaDefinitionModel.Name.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/fieldType")
      .withName("fieldType")
      .withNodePropertyMapping(GooglePubSubSchemaDefinitionModel.FieldType.value.iri())
      .withLiteralRange(xsdString.iri())
  )
}
