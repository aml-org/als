package org.mulesoft.amfintegration.dialect.dialects.metadialect

import amf.core.vocabulary.Namespace.XsdTypes._
import amf.plugins.document.vocabularies.metamodel.document.DialectModel
import amf.plugins.document.vocabularies.model.domain.PropertyMapping
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.DialectNode

object DialectRootEncodedNode extends DialectNode {
  override def name: String = "DialectEncodedNode"

  override def nodeTypeMapping: String = DialectModel.`type`.head.iri()

  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/dialect")
      .withNodePropertyMapping(DialectModel.Name.value.iri())
      .withName("dialect")
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/version")
      .withNodePropertyMapping(DialectModel.Version.value.iri())
      .withName("version")
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/usage")
      .withNodePropertyMapping(DialectModel.Usage.value.iri())
      .withName("usage")
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/documents")
      .withNodePropertyMapping(DialectModel.Documents.value.iri())
      .withName("documents")
      .withObjectRange(Seq(DocumentsObjectNode.id)),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/external")
      .withNodePropertyMapping(DialectModel.References.value.iri())
      .withName("external")
      .withObjectRange(Seq(ExternalObjectNode.id))
      .withMapKeyProperty("name")
      .withMapValueProperty("value")
  )
}
