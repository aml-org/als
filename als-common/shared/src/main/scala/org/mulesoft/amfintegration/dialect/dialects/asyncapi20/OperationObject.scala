package org.mulesoft.amfintegration.dialect.dialects.asyncapi20

import amf.plugins.document.vocabularies.model.domain.PropertyMapping
import amf.plugins.domain.webapi.metamodel.{MessageModel, OperationModel}

object OperationObject extends OperationAbstractObjectNode {
  override def name: String = "OperationObject"

  override def properties: Seq[PropertyMapping] = super.properties ++ Seq(
    PropertyMapping()
      .withId(location + "#/declarations/Operation/traits")
      .withName("traits")
      .withNodePropertyMapping(OperationModel.Extends.value.iri())
      .withAllowMultiple(true)
      .withObjectRange(Seq(OperationModel.Extends.value.iri())),
    PropertyMapping()
      .withId(location + "#/declarations/Operation/message")
      .withName("message")
      .withNodePropertyMapping(MessageModel.`type`.head.iri())
      .withObjectRange(Seq(MessageObjectNode.id))
  )
}
