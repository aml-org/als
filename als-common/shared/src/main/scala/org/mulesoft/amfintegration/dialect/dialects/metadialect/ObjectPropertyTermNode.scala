package org.mulesoft.amfintegration.dialect.dialects.metadialect

import amf.core.vocabulary.Namespace.XsdTypes.xsdUri
import amf.plugins.document.vocabularies.metamodel.domain.ObjectPropertyTermModel
import amf.plugins.document.vocabularies.model.domain.PropertyMapping

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
