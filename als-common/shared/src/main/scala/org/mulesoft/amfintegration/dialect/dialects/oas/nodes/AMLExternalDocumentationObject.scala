package org.mulesoft.amfintegration.dialect.dialects.oas.nodes

import amf.core.vocabulary.Namespace.XsdTypes.xsdString
import amf.plugins.document.vocabularies.model.domain.PropertyMapping
import amf.plugins.domain.shapes.metamodel.CreativeWorkModel
import org.mulesoft.amfintegration.dialect.dialects.oas.OasBaseDialect

object AMLExternalDocumentationObject extends DialectNode {

  override def name: String            = "ExternalDocumentationObject"
  override def nodeTypeMapping: String = CreativeWorkModel.`type`.head.iri()
  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(OasBaseDialect.DialectLocation + "#/declarations/ExternalDocumentationObject/description")
      .withName("description")
      .withNodePropertyMapping(CreativeWorkModel.Description.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(OasBaseDialect.DialectLocation + "#/declarations/ExternalDocumentationObject/url")
      .withName("url")
      .withMinCount(1)
      .withNodePropertyMapping(CreativeWorkModel.Url.value.iri())
      .withLiteralRange(xsdString.iri())
  )
}
