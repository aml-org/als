package org.mulesoft.amfintegration.dialect.dialects.metadialect

import amf.core.vocabulary.Namespace.XsdTypes.xsdString
import amf.dialects.oas.nodes.DialectNode
import amf.plugins.document.vocabularies.model.domain.PropertyMapping

object ExternalObjectNode extends DialectNode {
  override def name: String = "ExternalObjectNode"

  override def nodeTypeMapping: String = "FakeId"

  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/documents")
      .withNodePropertyMapping("NameExternal")
      .withName("name")
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/documents")
      .withNodePropertyMapping("ValueExternal")
      .withName("value")
      .withLiteralRange(xsdString.iri())
  )
}
