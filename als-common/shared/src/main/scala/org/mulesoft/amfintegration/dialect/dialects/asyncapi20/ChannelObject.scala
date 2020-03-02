package org.mulesoft.amfintegration.dialect.dialects.asyncapi20

import amf.dialects.oas.nodes.DialectNode
import amf.plugins.document.vocabularies.model.domain.PropertyMapping
import amf.plugins.domain.webapi.metamodel.{EndPointModel, OperationModel}
import amf.core.vocabulary.Namespace.XsdTypes.xsdString

object ChannelObject extends DialectNode {
  override def name: String = "ChannelObject"

  override def nodeTypeMapping: String = EndPointModel.`type`.head.iri()

  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + "#/declarations/Channel/channelPath")
      .withName("channelPath")
      .withNodePropertyMapping(EndPointModel.Path.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + "#/declarations/Channel/description")
      .withName("description")
      .withNodePropertyMapping(EndPointModel.Description.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + "#/declarations/Channel/subscribe")
      .withName("subscribe")
      .withNodePropertyMapping(OperationModel.`type`.head.iri())
      .withObjectRange(Seq(
        ))
  )
}
