package org.mulesoft.amfintegration.dialect.dialects.metadialect

import amf.core.vocabulary.Namespace.XsdTypes._
import amf.dialects.oas.nodes.DialectNode
import amf.plugins.document.vocabularies.metamodel.domain.PublicNodeMappingModel
import amf.plugins.document.vocabularies.model.domain.PropertyMapping

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
