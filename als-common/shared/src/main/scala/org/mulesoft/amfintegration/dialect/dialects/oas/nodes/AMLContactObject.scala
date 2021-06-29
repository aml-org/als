package org.mulesoft.amfintegration.dialect.dialects.oas.nodes

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.apicontract.internal.metamodel.domain.OrganizationModel
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.{xsdString, xsdUri}
import org.mulesoft.amfintegration.dialect.dialects.oas.{OAS30Dialect, OasBaseDialect}

object AMLContactObject extends DialectNode {

  override def location: String = OAS30Dialect.DialectLocation

  override def name: String            = "ContactObject"
  override def nodeTypeMapping: String = OrganizationModel.`type`.head.iri()
  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(OasBaseDialect.DialectLocation + "#/declarations/ContactObject/name")
      .withName("name")
      .withNodePropertyMapping(OrganizationModel.Name.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(OasBaseDialect.DialectLocation + "#/declarations/ContactObject/url")
      .withName("url")
      .withNodePropertyMapping(OrganizationModel.Url.value.iri())
      .withLiteralRange(xsdUri.iri()),
    PropertyMapping()
      .withId(OasBaseDialect.DialectLocation + "#/declarations/ContactObject/email")
      .withName("email")
      .withNodePropertyMapping(OrganizationModel.Email.value.iri())
      .withLiteralRange(xsdString.iri())
  )
}
