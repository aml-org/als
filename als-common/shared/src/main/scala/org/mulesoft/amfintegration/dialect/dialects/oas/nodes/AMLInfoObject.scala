package org.mulesoft.amfintegration.dialect.dialects.oas.nodes

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.apicontract.internal.metamodel.domain.api.WebApiModel
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.xsdString
import org.mulesoft.amfintegration.dialect.dialects.oas.OasBaseDialect

object AMLInfoObject extends DialectNode {

  override def name: String            = "InfoObject"
  override def nodeTypeMapping: String = WebApiModel.`type`.head.iri()
  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(OasBaseDialect.DialectLocation + "#/declarations/InfoObject/title")
      .withName("title")
      .withMinCount(1)
      .withNodePropertyMapping(WebApiModel.Name.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(OasBaseDialect.DialectLocation + "#/declarations/InfoObject/description")
      .withName("description")
      .withNodePropertyMapping(WebApiModel.Name.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(OasBaseDialect.DialectLocation + "#/declarations/InfoObject/termsOfService")
      .withName("termsOfService")
      .withNodePropertyMapping(WebApiModel.TermsOfService.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(OasBaseDialect.DialectLocation + "#/declarations/InfoObject/version")
      .withName("version")
      .withMinCount(1)
      .withNodePropertyMapping(WebApiModel.Version.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(OasBaseDialect.DialectLocation + "#/declarations/InfoObject/contact")
      .withName("contact")
      .withNodePropertyMapping(WebApiModel.Provider.value.iri())
      .withObjectRange(
        Seq(
          AMLContactObject.id
        )),
    PropertyMapping()
      .withId(OasBaseDialect.DialectLocation + "#/declarations/InfoObject/license")
      .withName("license")
      .withNodePropertyMapping(WebApiModel.License.value.iri())
      .withObjectRange(
        Seq(
          AMLLicenseObject.id
        ))
  )
}
