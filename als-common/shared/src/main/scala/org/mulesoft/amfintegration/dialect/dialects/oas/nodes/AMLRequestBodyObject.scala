package org.mulesoft.amfintegration.dialect.dialects.oas.nodes

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.apicontract.internal.metamodel.domain.{PayloadModel, RequestModel}
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.xsdString
import org.mulesoft.amfintegration.dialect.dialects.oas.OAS20Dialect.DialectLocation
import org.mulesoft.amfintegration.dialect.dialects.oas.OAS30Dialect

object AMLRequestBodyObject extends DialectNode {

  override def location: String = OAS30Dialect.DialectLocation
  override def name: String     = "RequestBodyObject"

  override def nodeTypeMapping: String = RequestModel.`type`.head.iri()
  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/RequestBodyObject/description")
      .withName("description")
      .withNodePropertyMapping(RequestModel.Description.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/RequestBodyObject/content")
      .withName("content")
      .withNodePropertyMapping(RequestModel.Payloads.value.iri())
      .withMapTermKeyProperty(PayloadModel.MediaType.value.iri())
      .withObjectRange(Seq(AMLContentObject.id)),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/RequestBodyObject/required")
      .withName("required")
      .withNodePropertyMapping(RequestModel.Required.value.iri())
      .withLiteralRange(xsdString.iri())
  )
}
