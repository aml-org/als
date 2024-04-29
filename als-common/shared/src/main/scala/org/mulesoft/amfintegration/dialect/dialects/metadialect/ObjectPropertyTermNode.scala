package org.mulesoft.amfintegration.dialect.dialects.metadialect

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.aml.internal.metamodel.domain.ObjectPropertyTermModel
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.xsdUri

object ObjectPropertyTermNode extends TermObjectNode {
  override def name: String = "ObjectPropertyTermNode"

  override def nodeTypeMapping: String = ObjectPropertyTermModel.`type`.head.iri();

  override def properties: Seq[PropertyMapping] = super.properties ++ Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/range")
      .withNodePropertyMapping(ObjectPropertyTermModel.Range.value.iri())
      .withName("range")
      .withLiteralRange(xsdUri.iri())
      .withEnum(Seq("string", "integer", "float", "boolean", "double", "uri", "any"))
  )
}
