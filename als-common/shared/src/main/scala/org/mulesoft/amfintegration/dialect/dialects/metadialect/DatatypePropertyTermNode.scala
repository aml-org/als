package org.mulesoft.amfintegration.dialect.dialects.metadialect

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.aml.internal.metamodel.domain.DatatypePropertyTermModel
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.xsdUri

object DatatypePropertyTermNode extends TermObjectNode {
  override def name: String = "DatatypePropertyTermNode"

  override def nodeTypeMapping: String = DatatypePropertyTermModel.`type`.head.iri();

  override def properties: Seq[PropertyMapping] = super.properties ++ Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/range")
      .withNodePropertyMapping(DatatypePropertyTermModel.Range.value.iri())
      .withName("range")
      .withLiteralRange(xsdUri.iri())
      .withEnum(Seq("string", "integer", "float", "boolean", "double", "uri", "any"))
  )
}
