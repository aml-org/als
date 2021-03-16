package org.mulesoft.amfintegration.dialect.dialects.metadialect

import amf.core.vocabulary.Namespace.XsdTypes.xsdString
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.DialectNode
import amf.plugins.document.vocabularies.model.domain.PropertyMapping

object ExternalObjectNode extends DialectNode {
  override def name: String = "ExternalObjectNode"

  override def nodeTypeMapping: String = "FakeId"

  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/name")
      .withNodePropertyMapping("NameExternal")
      .withName("name")
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/value")
      .withNodePropertyMapping("ValueExternal")
      .withName("value")
      .withLiteralRange(xsdString.iri())
  )
}
