package org.mulesoft.amfintegration.dialect.dialects.asyncapi20.bindings

import amf.dialects.oas.nodes.DialectNode
import amf.plugins.document.vocabularies.model.domain.PropertyMapping
import amf.plugins.domain.webapi.metamodel.bindings.{BindingType, DynamicBindingModel}
import amf.core.vocabulary.Namespace.XsdTypes._

trait BindingObjectNode extends DialectNode {

  override def properties: Seq[PropertyMapping] =
    Seq(
      PropertyMapping()
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
          )))
}

object DynamicBindingObjectNode extends BindingObjectNode {
  override def name: String = "DynamicBindingObjectNode"

  override def nodeTypeMapping: String = DynamicBindingModel.`type`.head.iri()
}

object NonPropsBindingPropertyNode extends DialectNode {
  override def name: String = "NonPropsBindingNode"

  override def nodeTypeMapping: String = "NonPropsBindingNode"

  override def properties: Seq[PropertyMapping] = Seq()
}
