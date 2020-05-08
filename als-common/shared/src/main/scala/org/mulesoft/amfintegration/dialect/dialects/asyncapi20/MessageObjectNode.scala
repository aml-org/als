package org.mulesoft.amfintegration.dialect.dialects.asyncapi20

import amf.core.vocabulary.Namespace.XsdTypes._
import amf.plugins.document.vocabularies.model.domain.PropertyMapping
import amf.plugins.domain.webapi.metamodel.MessageModel

object MessageObjectNode extends MessageAbstractObjectNode {
  override def name: String = "MessageObjectNode"

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
