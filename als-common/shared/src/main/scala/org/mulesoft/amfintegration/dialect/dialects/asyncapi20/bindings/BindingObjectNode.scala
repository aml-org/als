package org.mulesoft.amfintegration.dialect.dialects.asyncapi20.bindings

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.apicontract.internal.metamodel.domain.bindings.{BindingType, BindingVersion, GooglePubSubMessageBindingModel}
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.xsdString
import org.mulesoft.amfintegration.dialect.dialects.asyncapi26.bindings.GooglePubSubMessageBinding10Object.{
  location,
  name
}
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.DialectNode

trait BindingObjectNode extends DialectNode {

  protected def keys: Seq[String] = Seq(
    "http",
    "ws",
    "kafka",
    "amqp",
    "amqp1",
    "mqtt",
    "mqtt5",
    "nats",
    "jms",
    "sns",
    "sqs",
    "stomp",
    "redis"
  )
  val `type`: PropertyMapping = PropertyMapping()
    .withId(location + s"#/declarations/$name/type")
    .withName("type")
    .withNodePropertyMapping(BindingType.Type.value.iri()) // todo: http node mappings?
    .withObjectRange(Seq(NonPropsBindingPropertyNode.id))
    .withEnum(
      keys
    )
  override def properties: Seq[PropertyMapping] = Seq(`type`)
}

trait BindingVersionPropertyMapping {
  val bindingVersion: PropertyMapping =
    PropertyMapping()
      .withId(location + s"#/declarations/$name/bindingVersion")
      .withName("bindingVersion")
      .withNodePropertyMapping(BindingVersion.BindingVersion.value.iri())
      .withLiteralRange(xsdString.iri())
}

object DynamicBindingObjectNode extends BindingObjectNode {
  override def name: String = "DynamicBindingObjectNode"

  override def nodeTypeMapping: String = "DynamicBindingModel"
}

object NonPropsBindingPropertyNode extends DialectNode {
  override def name: String = "NonPropsBindingNode"

  override def nodeTypeMapping: String = "NonPropsBindingNode"

  override def properties: Seq[PropertyMapping] = Seq()
}
