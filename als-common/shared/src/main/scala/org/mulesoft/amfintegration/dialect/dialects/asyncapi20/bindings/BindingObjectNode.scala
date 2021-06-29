package org.mulesoft.amfintegration.dialect.dialects.asyncapi20.bindings

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.apicontract.internal.metamodel.domain.bindings.BindingType
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.DialectNode

trait BindingObjectNode extends DialectNode {

  val `type`: PropertyMapping = PropertyMapping()
    .withId(location + s"#/declarations/$name/type")
    .withName("type")
    .withNodePropertyMapping(BindingType.Type.value.iri()) // todo: http node mappings?
    .withObjectRange(Seq(NonPropsBindingPropertyNode.id))
    .withEnum(
      Seq(
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
      ))
  override def properties: Seq[PropertyMapping] = Seq(`type`)
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
