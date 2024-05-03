package org.mulesoft.amfintegration.dialect.dialects.asyncapi26

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.apicontract.internal.metamodel.domain.{MessageModel, RequestModel, ResponseModel}
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.xsdString
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.ConcreteMessageObjectNode
import org.mulesoft.amfintegration.dialect.dialects.asyncapi26.RequestMessage26ObjectNode.location

trait Async21MessageMappings extends Async21ExampleMessageMappings {
  override protected def mappingsMessages21: Seq[PropertyMapping] = super.mappingsMessages21 ++ Seq(
    PropertyMapping()
      .withId(location + "#/declarations/Message/messageId")
      .withName("messageId")
      .withNodePropertyMapping(MessageModel.MessageId.value.iri())
      .withLiteralRange(xsdString.iri())
  )
}

object RequestMessage26ObjectNode extends ConcreteMessageObjectNode with Async21MessageMappings {
  override def name: String = "RequestMessageObjectNode"

  override def nodeTypeMapping: String = RequestModel.`type`.head.iri()

  override val exampleProperty: PropertyMapping = PropertyMapping()
    .withId(location + "#/declarations/Message/examples")
    .withName("examples")
    .withNodePropertyMapping(MessageModel.Examples.value.iri())
    .withObjectRange(Seq(Async21MessageExampleNode.id))

  override def properties: Seq[PropertyMapping] = super.properties ++ mappingsMessages21

  override val specVersion: String = "2.6.0"
}

object ResponseMessage26ObjectNode extends ConcreteMessageObjectNode with Async21MessageMappings {
  override def name: String = "ResponseMessageObjectNode"

  override def nodeTypeMapping: String = ResponseModel.`type`.head.iri()

  override val exampleProperty: PropertyMapping = PropertyMapping()
    .withId(location + "#/declarations/Message/examples")
    .withName("examples")
    .withNodePropertyMapping(MessageModel.Examples.value.iri())
    .withObjectRange(Seq(Async21MessageExampleNode.id))
  override def properties: Seq[PropertyMapping] = super.properties ++ mappingsMessages21

  override val specVersion: String = "2.6.0"
}

object Message26ObjectNode extends ConcreteMessageObjectNode with Async21MessageMappings {

  override val specVersion: String = "2.6.0"

  override def nodeTypeMapping: String = MessageModel.`type`.head.iri()

  override def name: String = "MessageObjectNode"

  override val exampleProperty: PropertyMapping = PropertyMapping()
    .withId(location + "#/declarations/Message/examples")
    .withName("examples")
    .withNodePropertyMapping(MessageModel.Examples.value.iri())
    .withObjectRange(Seq(Async21MessageExampleNode.id))
  override def properties: Seq[PropertyMapping] = super.properties ++ mappingsMessages21
}
