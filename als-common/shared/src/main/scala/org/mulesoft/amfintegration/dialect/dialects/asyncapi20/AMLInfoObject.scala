package org.mulesoft.amfintegration.dialect.dialects.asyncapi20

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.apicontract.internal.metamodel.domain.api.AsyncApiModel
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.xsdString
import org.mulesoft.amfintegration.dialect.dialects.oas.OasBaseDialect
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.{AMLContactObject, AMLLicenseObject, DialectNode}

object AMLInfoObject extends DialectNode {

  override def name: String            = "InfoObject"
  override def nodeTypeMapping: String = AsyncApiModel.`type`.head.iri()
  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(OasBaseDialect.DialectLocation + "#/declarations/InfoObject/title")
      .withName("title")
      .withMinCount(1)
      .withNodePropertyMapping(AsyncApiModel.Name.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(OasBaseDialect.DialectLocation + "#/declarations/InfoObject/description")
      .withName("description")
      .withNodePropertyMapping(AsyncApiModel.Name.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(OasBaseDialect.DialectLocation + "#/declarations/InfoObject/termsOfService")
      .withName("termsOfService")
      .withNodePropertyMapping(AsyncApiModel.TermsOfService.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(OasBaseDialect.DialectLocation + "#/declarations/InfoObject/version")
      .withName("version")
      .withMinCount(1)
      .withNodePropertyMapping(AsyncApiModel.Version.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(OasBaseDialect.DialectLocation + "#/declarations/InfoObject/contact")
      .withName("contact")
      .withNodePropertyMapping(AsyncApiModel.Provider.value.iri())
      .withObjectRange(
        Seq(
          AMLContactObject.id
        )
      ),
    PropertyMapping()
      .withId(OasBaseDialect.DialectLocation + "#/declarations/InfoObject/license")
      .withName("license")
      .withNodePropertyMapping(AsyncApiModel.License.value.iri())
      .withObjectRange(
        Seq(
          AMLLicenseObject.id
        )
      )
  )
}
