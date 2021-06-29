package org.mulesoft.amfintegration.dialect.dialects.metadialect

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.aml.internal.metamodel.domain.PublicNodeMappingModel
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.{xsdString, xsdUri}
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.DialectNode

object PublicNodeMappingObjectNode extends DialectNode {
  override def name: String = "PublicNodeMappingObjectNode"

  override def nodeTypeMapping: String = PublicNodeMappingModel.`type`.head.iri()

  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/name")
      .withNodePropertyMapping(PublicNodeMappingModel.Name.value.iri())
      .withName("name")
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/mappedNode")
      .withNodePropertyMapping(PublicNodeMappingModel.MappedNode.value.iri())
      .withName("mappedNode")
      .withLiteralRange(xsdUri.iri())
  )
}
