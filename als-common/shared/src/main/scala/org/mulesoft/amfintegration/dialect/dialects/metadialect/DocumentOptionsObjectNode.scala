package org.mulesoft.amfintegration.dialect.dialects.metadialect

import amf.core.vocabulary.Namespace.XsdTypes._
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.DialectNode
import amf.plugins.document.vocabularies.metamodel.domain.DocumentsModelModel
import amf.plugins.document.vocabularies.model.domain.PropertyMapping

object DocumentOptionsObjectNode extends DialectNode {
  override def name: String = "DocumentOptionsObjectNode"

  override def nodeTypeMapping: String = "NonExisting"

  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/selfEncoded")
      .withNodePropertyMapping(DocumentsModelModel.SelfEncoded.value.iri())
      .withName("selfEncoded")
      .withLiteralRange(xsdBoolean.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/declarationsPath")
      .withNodePropertyMapping(DocumentsModelModel.DeclarationsPath.value.iri())
      .withName("declarationsPath")
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/keyProperty")
      .withNodePropertyMapping(DocumentsModelModel.KeyProperty.value.iri())
      .withName("keyProperty")
      .withLiteralRange(xsdBoolean.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/referenceStyle")
      .withNodePropertyMapping(DocumentsModelModel.ReferenceStyle.value.iri())
      .withName("referenceStyle")
      .withLiteralRange(xsdString.iri())
      .withEnum(Seq("RamlStyle", "JsonSchemaStyle"))
  )
}
