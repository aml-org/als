package org.mulesoft.amfintegration.dialect.dialects.asyncapi26.bindings

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.apicontract.internal.metamodel.domain.bindings.{IBMMQMessageBindingModel, MessageBindingModel}
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.{xsdInteger, xsdString}
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
