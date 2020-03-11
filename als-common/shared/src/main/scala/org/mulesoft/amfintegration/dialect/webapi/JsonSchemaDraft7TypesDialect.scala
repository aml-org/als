package org.mulesoft.amfintegration.dialect.webapi

import amf.core.metamodel.domain.ShapeModel
import amf.core.metamodel.domain.extensions.PropertyShapeModel
import amf.core.vocabulary.Namespace
import amf.core.vocabulary.Namespace.XsdTypes.{xsdAnyType, xsdBoolean, xsdFloat, xsdInteger, xsdString}
import amf.dialects.RAML10Dialect.DialectNodes.ExampleNode
import amf.plugins.document.vocabularies.model.domain.{NodeMapping, PropertyMapping}
import amf.plugins.domain.shapes.metamodel.{AnyShapeModel, ArrayShapeModel, ExampleModel, NilShapeModel, NodeShapeModel, ScalarShapeModel, TupleShapeModel}

object JsonSchemaDraft7TypesDialect {
  val DialectLocation = "file://parallel-als/vocabularies/dialects/jsonSchemaDraft7.yaml"

  val ShapeNodeId: String = DialectLocation + "#/declarations/ShapeNode"

  val shapeProperties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/ShapeNode/in")
      .withNodePropertyMapping(ShapeModel.Values.value.iri())
      .withName("enum")
      .withObjectRange(Seq(ShapeNodeId))
      .withAllowMultiple(true),
    PropertyMapping()
      .withId(DialectLocation + s"#/declarations/ShapeNode/const")
      .withName("const")
      .withNodePropertyMapping(ShapeModel.Values.value.iri())
      .withLiteralRange(xsdAnyType.iri()),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/ShapeNode/inherits")
      .withNodePropertyMapping(ShapeModel.Inherits.value.iri())
      .withName("type")
      .withEnum(
        Seq(
          "string",
          "number",
          "integer",
          "boolean",
          "array",
          "object",
          "null"
        ))
      .withLiteralRange(xsdString.iri())
  )

  val ShapeNode: NodeMapping = NodeMapping()
    .withId(ShapeNodeId)
    .withName("ShapeNode")
    .withNodeTypeMapping(ShapeModel.`type`.head.iri())
    .withPropertiesMapping(shapeProperties)

  val anyShapeProperties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/AnyShapeNode/examples")
      .withNodePropertyMapping(AnyShapeModel.Examples.value.iri())
      .withName("examples")
      .withObjectRange(Seq(ExampleNode.id))
      .withMapTermKeyProperty(ExampleModel.Name.value.iri()),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/SchemaObject/description")
      .withName("description")
      .withMinCount(1)
      .withNodePropertyMapping(ShapeModel.Description.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(DialectLocation + s"#/declarations/ShapeObject/default")
      .withName("default")
      .withNodePropertyMapping(ShapeModel.Default.value.iri())
      .withLiteralRange(xsdAnyType.iri()),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/SchemaObject/readOnly")
      .withName("readOnly")
      .withNodePropertyMapping(PropertyShapeModel.ReadOnly.value.iri())
      .withLiteralRange(xsdBoolean.iri()),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/SchemaObject/writeOnly")
      .withName("writeOnly")
      .withNodePropertyMapping(PropertyShapeModel.WriteOnly.value.iri())
  ) ++ shapeProperties

  val AnyShapeNode: NodeMapping = NodeMapping()
    .withId(DialectLocation + "#/declarations/AnyShapeNode")
    .withName("AnyShapeNode")
    .withNodeTypeMapping(AnyShapeModel.`type`.head.iri())
    .withPropertiesMapping(anyShapeProperties)

  val nodeProperties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/NodeShapeNode/maxProperties")
      .withNodePropertyMapping(NodeShapeModel.MaxProperties.value.iri())
      .withName("maxProperties")
      withLiteralRange xsdInteger.iri(),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/NodeShapeNode/minProperties")
      .withNodePropertyMapping(NodeShapeModel.MinProperties.value.iri())
      .withName("minProperties")
      withLiteralRange xsdInteger.iri(),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/SchemaObject/required")
      .withName("required")
      .withNodePropertyMapping(PropertyShapeModel.MinCount.value.iri())
      .withLiteralRange(xsdBoolean.iri()),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/NodeShapeNode/properties")
      .withNodePropertyMapping(NodeShapeModel.Properties.value.iri())
      .withName("properties")
      .withObjectRange(Seq(ShapeNode.id))
      .withMapTermKeyProperty(PropertyShapeModel.Name.value.iri()),
    PropertyMapping()
      .withId(DialectLocation + s"#/declarations/ShapeObject/patternProperties")
      .withName("patternProperties")
      .withNodePropertyMapping(NodeShapeModel.Properties.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/NodeShapeNode/additionalProperties")
      .withNodePropertyMapping(NodeShapeModel.Closed.value.iri())
      .withName("additionalProperties")
      .withLiteralRange(xsdBoolean.iri()),
    PropertyMapping()
      .withId(DialectLocation + s"#declarations/SchemaObject/dependencies")
      .withName("dependencies")
      .withNodePropertyMapping(NodeShapeModel.Dependencies.value.iri())
      .withLiteralRange(xsdString.iri())
      .withAllowMultiple(true),
    PropertyMapping()
      .withId(DialectLocation + s"#/declarations/SchemaObject/propertyNames")
      .withName("propertyNames")
      .withNodePropertyMapping(NodeShapeModel.PropertyNames.value.iri())
      .withLiteralRange(NodeShapeNode.id)
  )

  val NodeShapeNode: NodeMapping = NodeMapping()
    .withId(DialectLocation + "#/declarations/NodeShapeNode")
    .withName("NodeShapeNode")
    .withNodeTypeMapping(NodeShapeModel.`type`.head.iri())
    .withPropertiesMapping(anyShapeProperties ++ nodeProperties)

  val arrayShapeProperties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/ArrayShapeNode/items")
      .withNodePropertyMapping(ArrayShapeModel.Items.value.iri())
      .withName("items")
      .withObjectRange(Seq(ShapeNodeId)),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/ArrayShapeNode/minItems")
      .withNodePropertyMapping(ArrayShapeModel.MinItems.value.iri())
      .withName("minItems")
      .withLiteralRange(xsdInteger.iri()),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/ArrayShapeNode/maxItems")
      .withNodePropertyMapping(ArrayShapeModel.MaxItems.value.iri())
      .withName("maxItems")
      .withLiteralRange(xsdInteger.iri()),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/ArrayShapeNode/uniqueItems")
      .withNodePropertyMapping(ArrayShapeModel.UniqueItems.value.iri())
      .withName("uniqueItems")
      .withLiteralRange(xsdBoolean.iri()),
    PropertyMapping()
      .withId(DialectLocation + s"#/declarations/SchemaObject/additionalItems")
      .withName("additionalItems")
      .withNodePropertyMapping(TupleShapeModel.AdditionalItemsSchema.value.iri())
      .withLiteralRange(NodeShapeNode.id),
  )

  val ArrayShapeNode: NodeMapping = NodeMapping()
    .withId(DialectLocation + "#/declarations/ArrayShapeNode")
    .withName("ArrayShapeNode")
    .withNodeTypeMapping(ArrayShapeModel.`type`.head.iri())
    .withPropertiesMapping(anyShapeProperties ++ arrayShapeProperties)

  val stringShapeProperties: Seq[PropertyMapping] = Seq(
      PropertyMapping()
        .withId(DialectLocation + "#/declarations/ScalarShapeNode/pattern")
        .withNodePropertyMapping(ScalarShapeModel.Pattern.value.iri())
        .withName("pattern")
        .withLiteralRange(xsdString.iri()),
      PropertyMapping()
        .withId(DialectLocation + "#/declarations/ScalarShapeNode/minLength")
        .withNodePropertyMapping(ScalarShapeModel.MinLength.value.iri())
        .withName("minLength")
        .withLiteralRange(xsdInteger.iri()),
      PropertyMapping()
        .withId(DialectLocation + "#/declarations/ScalarShapeNode/maxLength")
        .withNodePropertyMapping(ScalarShapeModel.MaxLength.value.iri())
        .withName("maxLength")
        .withLiteralRange(xsdInteger.iri())
    )

  val StringShapeNode: NodeMapping = NodeMapping()
    .withId(DialectLocation + "#/declarations/StringShapeNode")
    .withName("StringShapeNode")
    .withNodeTypeMapping((Namespace.Shapes + "StringShape").iri())
    .withPropertiesMapping(anyShapeProperties ++ stringShapeProperties)

  private val numberShapeProperties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/ScalarShapeNode/minimum")
      .withNodePropertyMapping(ScalarShapeModel.Minimum.value.iri())
      .withName("minimum")
      .withLiteralRange(xsdFloat.iri()),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/ScalarShapeNode/maximun")
      .withNodePropertyMapping(ScalarShapeModel.Maximum.value.iri())
      .withName("maximum")
      .withLiteralRange(xsdFloat.iri()),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/ScalarShapeNode/multipleOf")
      .withNodePropertyMapping(ScalarShapeModel.MultipleOf.value.iri())
      .withName("multipleOf")
      .withLiteralRange(xsdFloat.iri()),
    PropertyMapping()
      .withId(DialectLocation + s"#/declarations/ShapeObject/exclusiveMaximum")
      .withName("exclusiveMaximum")
      .withNodePropertyMapping(ScalarShapeModel.ExclusiveMaximum.value.iri())
      .withLiteralRange(xsdInteger.iri()),
    PropertyMapping()
      .withId(DialectLocation + s"#/declarations/ShapeObject/exclusiveMinimum")
      .withName("exclusiveMinimum")
      .withNodePropertyMapping(ScalarShapeModel.ExclusiveMinimum.value.iri())
      .withLiteralRange(xsdInteger.iri()),
  )

  val NumberShapeNode: NodeMapping = NodeMapping()
    .withId(DialectLocation + "#/declarations/NumberShapeNode")
    .withName("NumberShapeNode")
    .withNodeTypeMapping((Namespace.Shapes + "NumberShape").iri())
    .withPropertiesMapping(anyShapeProperties ++ numberShapeProperties)

  val NilShapeNode: NodeMapping = NodeMapping()
    .withId(DialectLocation + "#/declarations/NilShapeNode")
    .withName("NilShapeNode")
    .withNodeTypeMapping(NilShapeModel.`type`.head.iri())
}
