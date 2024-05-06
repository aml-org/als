package org.mulesoft.amfintegration.dialect.dialects.asyncapi20

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.apicontract.internal.metamodel.domain.{MessageModel, RequestModel, ResponseModel}

trait ConcreteMessageObjectNode extends MessageAbstractObjectNode {

  override def properties: Seq[PropertyMapping] = super.properties ++ Seq(
    PropertyMapping()
      .withId(location + "#/declarations/Message/payload")
      .withName("payload")
      .withNodePropertyMapping(MessageModel.Payloads.value.iri())
      .withObjectRange(Seq("")), // todo: schema value
    PropertyMapping()
      .withId(location + "#/declarations/Message/traits")
      .withName("traits")
      .withNodePropertyMapping(MessageModel.Extends.value.iri()) // ???
      .withObjectRange(Seq(MessageTraitsObjectNode.id))
      .withAllowMultiple(true)
  )
}

object RequestMessageObjectNode extends ConcreteMessageObjectNode {
  override val specVersion: String = "2.0.0"
  override def name: String        = "RequestMessageObjectNode"

  override def nodeTypeMapping: String = RequestModel.`type`.head.iri()

  override val exampleProperty: PropertyMapping = PropertyMapping()
    .withId(location + "#/declarations/Message/examples")
    .withName("examples")
    .withNodePropertyMapping(MessageModel.Examples.value.iri())
    .withObjectRange(Seq(Async20MessageExampleNode.id))
}

object ResponseMessageObjectNode extends ConcreteMessageObjectNode {

  override val specVersion: String = "2.0.0"
  override def name: String        = "ResponseMessageObjectNode"

  override def nodeTypeMapping: String = ResponseModel.`type`.head.iri()

  override val exampleProperty: PropertyMapping = PropertyMapping()
    .withId(location + "#/declarations/Message/examples")
    .withName("examples")
    .withNodePropertyMapping(MessageModel.Examples.value.iri())
    .withObjectRange(Seq(Async20MessageExampleNode.id))
}

object MessageObjectNode extends ConcreteMessageObjectNode {
  override val specVersion: String     = "2.0.0"
  override def nodeTypeMapping: String = MessageModel.`type`.head.iri()

  override def name: String = "MessageObjectNode"

  override val exampleProperty: PropertyMapping = PropertyMapping()
    .withId(location + "#/declarations/Message/examples")
    .withName("examples")
    .withNodePropertyMapping(MessageModel.Examples.value.iri())
    .withObjectRange(Seq(Async20MessageExampleNode.id))
}
