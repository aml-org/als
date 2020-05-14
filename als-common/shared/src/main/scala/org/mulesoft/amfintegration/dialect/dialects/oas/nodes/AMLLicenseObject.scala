package org.mulesoft.amfintegration.dialect.dialects.oas.nodes

import amf.core.vocabulary.Namespace.XsdTypes.{xsdString, xsdUri}
import org.mulesoft.amfintegration.dialect.dialects.oas.OasBaseDialect
import amf.plugins.document.vocabularies.model.domain.PropertyMapping
import amf.plugins.domain.webapi.metamodel.LicenseModel

object AMLLicenseObject extends DialectNode {

  override def name: String            = "LicenseObject"
  override def nodeTypeMapping: String = LicenseModel.`type`.head.iri()
  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(OasBaseDialect.DialectLocation + "#/declarations/LicenseObject/name")
      .withName("name")
      .withMinCount(1)
      .withNodePropertyMapping(LicenseModel.Name.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(OasBaseDialect.DialectLocation + "#/declarations/LicenseObject/url")
      .withName("url")
      .withNodePropertyMapping(LicenseModel.Url.value.iri())
      .withLiteralRange(xsdUri.iri())
  )
}
