package org.mulesoft.amfintegration.dialect.dialects.asyncapi20

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.apicontract.internal.metamodel.domain.{MessageModel, OperationModel}

trait OperationObject extends OperationAbstractObjectNode {
  override def name: String = "OperationObject"

  override def properties: Seq[PropertyMapping] = super.properties ++ Seq(
    PropertyMapping()
      .withId(location + "#/declarations/Operation/traits")
      .withName("traits")
      .withNodePropertyMapping(OperationModel.Extends.value.iri())
      .withAllowMultiple(true)
      .withObjectRange(Seq(OperationTraitsObjectNode.id)),
    PropertyMapping()
      .withId(location + "#/declarations/Operation/message")
      .withName("message")
      .withNodePropertyMapping(MessageModel.`type`.head.iri())
      .withObjectRange(Seq(MessageObjectNode.id))
  )
}

object Operation20Object extends OperationObject
