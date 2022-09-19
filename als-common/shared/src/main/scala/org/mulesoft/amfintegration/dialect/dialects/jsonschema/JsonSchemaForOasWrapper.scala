package org.mulesoft.amfintegration.dialect.dialects.jsonschema

import amf.aml.client.scala.model.domain.{NodeMapping, PropertyMapping}
import amf.apicontract.internal.metamodel.domain.ParameterModel
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.{xsdAnyType, xsdBoolean, xsdDouble, xsdInteger, xsdString}
import amf.core.internal.metamodel.domain.ShapeModel
import amf.core.internal.metamodel.domain.extensions.PropertyShapeModel
import amf.shapes.internal.domain.metamodel.{AnyShapeModel, ArrayShapeModel, NodeShapeModel, ScalarShapeModel}
import org.mulesoft.amfintegration.dialect.dialects.oas.OAS20Dialect.{DialectLocation, ImplicitField}
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.{
  AMLExternalDocumentationObject,
  Oas20SchemaObject,
  XmlObject
}

trait JsonSchemaForOasWrapper {

  def specProperties: Seq[PropertyMapping]

  val common: Seq[PropertyMapping] = specProperties ++ Seq(
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/ParameterObject/Shape/type")
      .withName("type")
      .withMinCount(1)
      .withEnum(
        Seq(
          "string",
          "number",
          "integer",
          "boolean",
          "array",
          "object",
          "file"
        )
      )
      .withNodePropertyMapping(ImplicitField)
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/ParameterObject/description")
      .withName("description")
      .withMinCount(1)
      .withNodePropertyMapping(ParameterModel.Description.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(DialectLocation + s"#/declarations/Schema/title")
      .withName("title")
      .withNodePropertyMapping(ShapeModel.DisplayName.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(DialectLocation + s"#/declarations/Schema/required")
      .withName("required")
      .withNodePropertyMapping(PropertyShapeModel.MinCount.value.iri())
      .withLiteralRange(xsdBoolean.iri()),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/SchemaObject/readOnly")
      .withName("readOnly")
      .withNodePropertyMapping(PropertyShapeModel.ReadOnly.value.iri())
      .withLiteralRange(xsdBoolean.iri()),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/SchemaObject/xml")
      .withName("xml")
      .withNodePropertyMapping(AnyShapeModel.XMLSerialization.value.iri())
      .withObjectRange(
        Seq(
          XmlObject.id
        )
      ),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/SchemaObject/externalDocs")
      .withName("externalDocs")
      .withNodePropertyMapping(AnyShapeModel.Documentation.value.iri())
      .withObjectRange(
        Seq(
          AMLExternalDocumentationObject.id
        )
      ),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/SchemaObject/example")
      .withName("example")
      .withNodePropertyMapping(AnyShapeModel.Examples.value.iri())
      .withLiteralRange(xsdAnyType.iri()),
    PropertyMapping()
      .withId(DialectLocation + s"#/declarations/Schema/default")
      .withName("default")
      .withNodePropertyMapping(ShapeModel.Default.value.iri())
      .withLiteralRange(xsdAnyType.iri()),
    PropertyMapping()
      .withId(DialectLocation + s"#/declarations/Schema/enum")
      .withName("enum")
      .withNodePropertyMapping(ShapeModel.Values.value.iri())
      .withLiteralRange(xsdAnyType.iri())
      .withAllowMultiple(true),
    PropertyMapping()
      .withId(DialectLocation + s"#/declarations/Schema/allOf")
      .withName("allOf")
      .withNodePropertyMapping(ShapeModel.Inherits.value.iri())
      .withLiteralRange(xsdAnyType.iri())
  )

  val SchemaObject: NodeMapping = NodeMapping()
    .withId(Oas20SchemaObject.id)
    .withName("SchemaObject")
    .withNodeTypeMapping(ShapeModel.`type`.head.iri())
    .withPropertiesMapping(common)

  val AnySchemaObject: NodeMapping = NodeMapping()
    .withId("#/declarations/AnySchemaObject")
    .withName("AnySchemaObject")
    .withNodeTypeMapping(AnyShapeModel.`type`.head.iri())
    .withPropertiesMapping(common)

  val StringSchemaObject: NodeMapping = NodeMapping()
    .withId("#/declarations/StringSchemaObject")
    .withName("StringSchemaObject")
    .withNodeTypeMapping("StringSchemaObject.id")
    .withPropertiesMapping(
      common ++ Seq(
        PropertyMapping()
          .withId(DialectLocation + "#/declarations/SchemaObject/format")
          .withName("format")
          .withNodePropertyMapping(ScalarShapeModel.Format.value.iri())
          .withEnum(
            Seq(
              "byte",
              "binary",
              "date",
              "date-time",
              "password"
            )
          )
          .withLiteralRange(xsdString.iri()),
        PropertyMapping()
          .withId(DialectLocation + "#/declarations/SchemaObject/pattern")
          .withName("pattern")
          .withNodePropertyMapping(ScalarShapeModel.Pattern.value.iri())
          .withLiteralRange(xsdString.iri()),
        PropertyMapping()
          .withId(DialectLocation + "#/declarations/SchemaObject/maxLength")
          .withName("maxLength")
          .withNodePropertyMapping(ScalarShapeModel.MaxLength.value.iri())
          .withLiteralRange(xsdInteger.iri()),
        PropertyMapping()
          .withId(DialectLocation + "#/declarations/SchemaObject/minLength")
          .withName("minLength")
          .withNodePropertyMapping(ScalarShapeModel.MinLength.value.iri())
          .withLiteralRange(xsdString.iri())
      )
    )

  private val numberProps: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/SchemaObject/multipleOf")
      .withName("multipleOf")
      .withNodePropertyMapping(ScalarShapeModel.MultipleOf.value.iri())
      .withLiteralRange(xsdDouble.iri()),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/SchemaObject/maximum")
      .withName("maximum")
      .withNodePropertyMapping(ScalarShapeModel.Maximum.value.iri())
      .withLiteralRange(xsdDouble.iri()),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/SchemaObject/exclusiveMaximum")
      .withName("exclusiveMaximum")
      .withNodePropertyMapping(ScalarShapeModel.ExclusiveMaximum.value.iri())
      .withLiteralRange(xsdBoolean.iri()),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/SchemaObject/minimum")
      .withName("minimum")
      .withNodePropertyMapping(ScalarShapeModel.Minimum.value.iri())
      .withLiteralRange(xsdDouble.iri()),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/SchemaObject/exclusiveMinimum")
      .withName("exclusiveMinimum")
      .withNodePropertyMapping(ScalarShapeModel.ExclusiveMinimum.value.iri())
      .withLiteralRange(xsdBoolean.iri())
  )
  val IntegerSchemaObject: NodeMapping = NodeMapping()
    .withId("#/declarations/IntegerSchemaObject")
    .withName("IntegerSchemaObject ")
    .withNodeTypeMapping("IntegerSchemaObject.id")
    .withPropertiesMapping(
      common ++ numberProps ++ Seq(
        PropertyMapping()
          .withId(DialectLocation + "#/declarations/SchemaObject/format")
          .withName("format")
          .withNodePropertyMapping(ScalarShapeModel.Format.value.iri())
          .withEnum(
            Seq(
              "int32",
              "int64"
            )
          )
          .withLiteralRange(xsdString.iri())
      )
    )

  val NumberSchemaObject: NodeMapping = NodeMapping()
    .withId("#/declarations/NumberSchemaObject")
    .withName("NumberSchemaObject ")
    .withNodeTypeMapping("NumberSchemaObject.id")
    .withPropertiesMapping(
      common ++ numberProps ++ Seq(
        PropertyMapping()
          .withId(DialectLocation + "#/declarations/SchemaObject/format")
          .withName("format")
          .withNodePropertyMapping(ScalarShapeModel.Format.value.iri())
          .withEnum(
            Seq(
              "int32",
              "int64",
              "float",
              "double"
            )
          )
          .withLiteralRange(xsdString.iri())
      )
    )

  def arraySpecProperties: Seq[PropertyMapping]

  val ArraySchemaObject: NodeMapping = NodeMapping()
    .withId("#/declarations/ArraySchemaObject")
    .withName("ArraySchemaObject")
    .withNodeTypeMapping(ArrayShapeModel.`type`.head.iri())
    .withPropertiesMapping(
      common ++ arraySpecProperties ++ Seq(
        PropertyMapping()
          .withId(DialectLocation + s"#/declarations/Schema/items")
          .withName("items")
          .withNodePropertyMapping(ArrayShapeModel.Items.value.iri())
          .withObjectRange(
            Seq(
              Oas20SchemaObject.id
            )
          ),
        PropertyMapping()
          .withId(DialectLocation + s"#/declarations/Schema/maxItems")
          .withName("maxItems")
          .withNodePropertyMapping(ArrayShapeModel.MaxItems.value.iri())
          .withLiteralRange(xsdInteger.iri()),
        PropertyMapping()
          .withId(DialectLocation + s"#/declarations/Schema/minItems")
          .withName("minItems")
          .withNodePropertyMapping(ArrayShapeModel.MinItems.value.iri())
          .withLiteralRange(xsdInteger.iri()),
        PropertyMapping()
          .withId(DialectLocation + s"#/declarations/Schema/uniqueItems")
          .withName("uniqueItems")
          .withNodePropertyMapping(ArrayShapeModel.UniqueItems.value.iri())
          .withLiteralRange(xsdBoolean.iri())
      )
    )

  def specNodeProperties: Seq[PropertyMapping]
  val NodeShapeObject: NodeMapping = NodeMapping()
    .withId("#/declarations/NodeSchemaObject")
    .withName("NodeSchemaObject")
    .withNodeTypeMapping(NodeShapeModel.`type`.head.iri())
    .withPropertiesMapping(
      common ++ specNodeProperties ++ Seq(
        PropertyMapping()
          .withId(DialectLocation + s"#/declarations/Schema/maxProperties")
          .withName("maxProperties")
          .withNodePropertyMapping(NodeShapeModel.MaxProperties.value.iri())
          .withLiteralRange(xsdInteger.iri()),
        PropertyMapping()
          .withId(DialectLocation + s"#/declarations/Schema/minProperties")
          .withName("minProperties")
          .withNodePropertyMapping(NodeShapeModel.MinProperties.value.iri())
          .withLiteralRange(xsdInteger.iri()),
        PropertyMapping()
          .withId(DialectLocation + s"#/declarations/Schema/properties")
          .withName("properties")
          .withNodePropertyMapping(NodeShapeModel.Properties.value.iri())
          .withMapTermKeyProperty(PropertyShapeModel.Name.value.iri())
          .withObjectRange(Seq(Oas20SchemaObject.id)),
        PropertyMapping()
          .withId(DialectLocation + s"#/declarations/Schema/additionalProperties")
          .withName("additionalProperties")
          .withNodePropertyMapping(NodeShapeModel.MinProperties.value.iri())
          .withLiteralRange(xsdInteger.iri())
      )
    )

}
