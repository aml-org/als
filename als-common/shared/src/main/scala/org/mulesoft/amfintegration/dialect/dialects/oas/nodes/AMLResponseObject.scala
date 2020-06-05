package org.mulesoft.amfintegration.dialect.dialects.oas.nodes

import amf.core.vocabulary.Namespace.XsdTypes.xsdString
import org.mulesoft.amfintegration.dialect.dialects.oas.{OAS20Dialect, OAS30Dialect, OasBaseDialect}
import amf.plugins.document.vocabularies.model.domain.PropertyMapping
import amf.plugins.domain.shapes.metamodel.ExampleModel
import amf.plugins.domain.webapi.metamodel.{ParameterModel, PayloadModel, ResponseModel, TemplatedLinkModel}

trait AMLResponseObject extends DialectNode {

  override def name: String            = "ResponseObject"
  override def nodeTypeMapping: String = ResponseModel.`type`.head.iri()

  def versionProperties: Seq[PropertyMapping]

  val statusCodeProperty: PropertyMapping = PropertyMapping()
    .withId(OasBaseDialect.DialectLocation + "#/declarations/ResponseObject/statusCode")
    .withName("statusCode")
    .withMinCount(1)
    .withNodePropertyMapping(ResponseModel.StatusCode.value.iri())
    .withLiteralRange(xsdString.iri())

  override def properties: Seq[PropertyMapping] = versionProperties ++ Seq(
    PropertyMapping()
      .withId(OasBaseDialect.DialectLocation + "#/declarations/ResponseObject/description")
      .withName("description")
      .withMinCount(1)
      .withNodePropertyMapping(ResponseModel.Description.value.iri())
      .withLiteralRange(xsdString.iri()),
    statusCodeProperty
  )
}

object Oas20ResponseObject extends AMLResponseObject {
  override def versionProperties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(OAS20Dialect.DialectLocation + "#/declarations/ResponseObject/schema")
      .withName("schema")
      .withNodePropertyMapping(ResponseModel.Payloads.value.iri())
      .withObjectRange(
        Seq(
          Oas20SchemaObject.id
        )),
    PropertyMapping()
      .withId(OasBaseDialect.DialectLocation + "#/declarations/ResponseObject/headers")
      .withName("headers")
      .withNodePropertyMapping(ResponseModel.Headers.value.iri())
      .withMapKeyProperty(ParameterModel.Name.value.iri())
      .withObjectRange(Seq(
        Oas20AMLHeaderObject.id
      )),
    PropertyMapping()
      .withId(OAS20Dialect.DialectLocation + "#/declarations/ResponseObject/examples")
      .withName("examples")
      .withMapKeyProperty(ExampleModel.MediaType.value.iri())
      .withMapValueProperty(ExampleModel.Raw.value.iri())
      .withNodePropertyMapping(ResponseModel.Examples.value.iri())
      .withObjectRange(Seq(
        AMLExampleObject.id
      ))
  )
}

object Oas30ResponseObject extends AMLResponseObject {

  override def versionProperties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(OAS30Dialect.DialectLocation + "#/declarations/ResponseObject/headers")
      .withName("headers")
      .withNodePropertyMapping(ResponseModel.Headers.value.iri())
      .withMapKeyProperty(ParameterModel.Name.value.iri())
      .withObjectRange(Seq(
        Oas30AMLHeaderObject.id
      )),
    PropertyMapping()
      .withId(OAS30Dialect.DialectLocation + "#/declarations/ResponseObject/content")
      .withName("content")
      .withNodePropertyMapping(ResponseModel.Payloads.value.iri())
      .withMapKeyProperty(PayloadModel.MediaType.value.iri())
      .withObjectRange(Seq(
        AMLContentObject.id
      )),
    PropertyMapping()
      .withId(OAS30Dialect.DialectLocation + "#/declarations/ResponseObject/links")
      .withName("links")
      .withNodePropertyMapping(ResponseModel.Links.value.iri())
      .withMapKeyProperty(TemplatedLinkModel.Name.value.iri())
      .withObjectRange(Seq(
        AMLLinkObject.id
      ))
  )

}
