package org.mulesoft.amfintegration.dialect.dialects.metadialect

import amf.core.vocabulary.Namespace.XsdTypes.xsdString
import amf.plugins.document.vocabularies.metamodel.domain.DatatypePropertyTermModel
import amf.plugins.document.vocabularies.model.domain.PropertyMapping
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.DialectNode

trait TermObjectNode extends DialectNode{
  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/displayName")
      .withNodePropertyMapping(DatatypePropertyTermModel.DisplayName.value.iri())
      .withName("displayName")
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/description")
      .withNodePropertyMapping(DatatypePropertyTermModel.Description.value.iri())
      .withName("description")
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/extends")
      .withNodePropertyMapping(DatatypePropertyTermModel.Extends.value.iri())
      .withName("extends")
      .withLiteralRange(xsdString.iri()),
  )
}
