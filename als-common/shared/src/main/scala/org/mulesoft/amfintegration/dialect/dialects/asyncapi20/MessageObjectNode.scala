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
  override def name: String = "RequestMessageObjectNode"

  override def nodeTypeMapping: String = RequestModel.`type`.head.iri()
}

object ResponseMessageObjectNode extends ConcreteMessageObjectNode {
  override def name: String = "ResponseMessageObjectNode"

  override def nodeTypeMapping: String = ResponseModel.`type`.head.iri()
}

object MessageObjectNode extends ConcreteMessageObjectNode {

  override def nodeTypeMapping: String = MessageModel.`type`.head.iri()

  override def name: String = "MessageObjectNode"
}
