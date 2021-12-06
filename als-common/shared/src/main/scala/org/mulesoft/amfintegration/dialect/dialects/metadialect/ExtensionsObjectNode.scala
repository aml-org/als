package org.mulesoft.amfintegration.dialect.dialects.metadialect

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.xsdString
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.DialectNode

object ExtensionsObjectNode extends DialectNode {
  override def name: String = "ExtensionsObjectNode"

  override def nodeTypeMapping: String = "FakeId"

  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/name")
      .withNodePropertyMapping("NameExtension")
      .withName("name")
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/range")
      .withNodePropertyMapping("RangeExtension")
      .withName("range")
      .withLiteralRange(xsdString.iri())
  )
}
