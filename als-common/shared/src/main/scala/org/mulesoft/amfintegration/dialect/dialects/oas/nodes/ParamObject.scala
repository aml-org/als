package org.mulesoft.amfintegration.dialect.dialects.oas.nodes

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.apicontract.internal.metamodel.domain.{ParameterModel, PayloadModel, RequestModel}
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.{xsdBoolean, xsdString}
import org.mulesoft.amfintegration.dialect.dialects.oas.{OAS20Dialect, OAS30Dialect}

trait AMLOasParamBaseObject extends AMLSchemaBaseObject {

  val paramName: PropertyMapping = PropertyMapping()
    .withId(OAS20Dialect.DialectLocation + "#/declarations/ParameterObject/name")
    .withName("name")
    .withMinCount(1)
    .withNodePropertyMapping(ParameterModel.Name.value.iri())
    .withLiteralRange(xsdString.iri())

  val description: PropertyMapping = PropertyMapping()
    .withId(OAS20Dialect.DialectLocation + "#/declarations/ParameterObject/description")
    .withName("description")
    .withMinCount(1)
    .withNodePropertyMapping(ParameterModel.Description.value.iri())
    .withLiteralRange(xsdString.iri())
  def paramProperties: Seq[PropertyMapping] =
    Seq(
      paramName,
      description
    )
}

object Oas20ParamObject extends AMLOasParamBaseObject {

  override val name: String            = "ParameterObject"
  override val nodeTypeMapping: String = ParameterModel.`type`.head.iri()
  val paramBinding: PropertyMapping = PropertyMapping()
    .withId(OAS20Dialect.DialectLocation + "#/declarations/ParameterObject/binding")
    .withName("in")
    .withMinCount(1)
    .withEnum(
      Seq(
        "query",
        "header",
        "path",
        "formData",
        "body"
      ))
    .withNodePropertyMapping(ParameterModel.Binding.value.iri())
    .withLiteralRange(xsdString.iri())

  override def properties: Seq[PropertyMapping] = paramProperties ++ Seq(
    PropertyMapping()
      .withId(OAS20Dialect.DialectLocation + "#/declarations/ParameterObject/required")
      .withName("required")
      .withMinCount(1)
      .withNodePropertyMapping(ParameterModel.Required.value.iri())
      .withLiteralRange(xsdBoolean.iri()),
    paramBinding
  )

  override def location: String = OAS20Dialect.DialectLocation
}

trait AMLOas30BaseParamProps extends Oas30ExampleProperty {
  def styleProp: PropertyMapping =
    PropertyMapping()
      .withId(OAS30Dialect.DialectLocation + "#/declarations/ParameterObject/style")
      .withName("style")
      .withNodePropertyMapping(ParameterModel.Style.value.iri())
      .withLiteralRange(xsdString.iri())
      .withEnum(
        Seq(
          "matrix",
          "label",
          "form",
          "simple",
          "spaceDelimited",
          "pipeDelimited",
          "deepObject"
        )) // todo: handle enum values in custom plugin?
  def specialProps: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(OAS30Dialect.DialectLocation + "#/declarations/ParameterObject/required")
      .withName("required")
      .withMinCount(1)
      .withNodePropertyMapping(ParameterModel.Required.value.iri())
      .withLiteralRange(xsdBoolean.iri()),
    PropertyMapping()
      .withId(OAS30Dialect.DialectLocation + "#/declarations/ParameterObject/deprecated")
      .withName("deprecated")
      .withNodePropertyMapping(ParameterModel.Deprecated.value.iri())
      .withLiteralRange(xsdBoolean.iri()),
    PropertyMapping()
      .withId(OAS30Dialect.DialectLocation + "#/declarations/ParameterObject/allowEmptyValue")
      .withName("allowEmptyValue")
      .withNodePropertyMapping(ParameterModel.AllowEmptyValue.value.iri())
      .withLiteralRange(xsdBoolean.iri()),
    styleProp,
    PropertyMapping()
      .withId(OAS30Dialect.DialectLocation + "#/declarations/ParameterObject/explode")
      .withName("explode")
      .withNodePropertyMapping(ParameterModel.Explode.value.iri())
      .withLiteralRange(xsdBoolean.iri()),
    PropertyMapping()
      .withId(OAS30Dialect.DialectLocation + "#/declarations/ParameterObject/allowReserved")
      .withName("allowReserved")
      .withNodePropertyMapping(ParameterModel.AllowReserved.value.iri())
      .withLiteralRange(xsdBoolean.iri()),
    PropertyMapping()
      .withId(OAS30Dialect.DialectLocation + "#/declarations/ParameterObject/schema")
      .withName("schema")
      .withNodePropertyMapping(ParameterModel.Schema.value.iri())
      .withObjectRange(Seq(Oas20SchemaObject.id)),
    PropertyMapping()
      .withId(OAS30Dialect.DialectLocation + "#/declarations/RequestBodyObject/content")
      .withName("content")
      .withNodePropertyMapping(RequestModel.Payloads.value.iri())
      .withMapTermKeyProperty(PayloadModel.MediaType.value.iri())
      .withObjectRange(Seq(AMLContentObject.id)),
    examples,
    example
  )
}

object Oas30ParamObject extends AMLOas30BaseParamProps with AMLOasParamBaseObject {

  override val name: String            = "ParameterObject"
  override val nodeTypeMapping: String = ParameterModel.`type`.head.iri()
  override def properties: Seq[PropertyMapping] = specialProps ++ paramProperties ++ Seq(
    PropertyMapping()
      .withId(OAS30Dialect.DialectLocation + "#/declarations/ParameterObject/binding")
      .withName("in")
      .withMinCount(1)
      .withEnum(
        Seq(
          "query",
          "header",
          "path",
          "cookie"
        ))
      .withNodePropertyMapping(ParameterModel.Binding.value.iri())
      .withLiteralRange(xsdString.iri())
  )

  override def location: String = OAS30Dialect.DialectLocation
}
