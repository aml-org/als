package org.mulesoft.amfintegration.dialect.dialects.jsonschema.draft7.base

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.{amlAnyNode, xsdAnyType, xsdBoolean, xsdString}
import amf.core.internal.metamodel.domain.ShapeModel
import amf.core.internal.metamodel.domain.extensions.PropertyShapeModel
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.DialectNode

trait BaseShapeNode extends DialectNode{

  override def name = "Shape"
  override def properties:Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + "#/declarations/ShapeNode/in")
      .withNodePropertyMapping(ShapeModel.Values.value.iri())
      .withName("enum")
      .withObjectRange(Seq(xsdAnyType.iri())),
    PropertyMapping()
      .withId(location + s"#/declarations/ShapeNode/const")
      .withName("const")
      .withNodePropertyMapping(ShapeModel.Values.value.iri())
      .withLiteralRange(xsdAnyType.iri()),
    PropertyMapping()
      .withId(location + "#/declarations/ShapeNode/inherits")
      .withNodePropertyMapping(ShapeModel.Inherits.value.iri())
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
          "null"
        ))
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + "#/declarations/SchemaObject/description")
      .withName("description")
      .withMinCount(1)
      .withNodePropertyMapping(ShapeModel.Description.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/ShapeObject/default")
      .withName("default")
      .withNodePropertyMapping(ShapeModel.Default.value.iri())
      .withLiteralRange(xsdAnyType.iri()),
    PropertyMapping()
      .withId(location + "#/declarations/SchemaObject/readOnly")
      .withName("readOnly")
      .withNodePropertyMapping(PropertyShapeModel.ReadOnly.value.iri())
      .withLiteralRange(xsdBoolean.iri()),
    PropertyMapping()
      .withId(location + "#/declarations/SchemaObject/writeOnly")
      .withName("writeOnly")
      .withNodePropertyMapping(PropertyShapeModel.WriteOnly.value.iri())
      .withLiteralRange(xsdBoolean.iri()),
    PropertyMapping()
      .withId(location + "#/declarations/SchemaObject/if")
      .withNodePropertyMapping(ShapeModel.If.value.iri())
      .withName("if")
      .withObjectRange(Seq(amlAnyNode.iri())),
    PropertyMapping()
      .withId(location + "#/declarations/SchemaObject/then")
      .withNodePropertyMapping(ShapeModel.Then.value.iri())
      .withName("then")
      .withObjectRange(Seq(amlAnyNode.iri())),
    PropertyMapping()
      .withId(location + "#/declarations/SchemaObject/else")
      .withNodePropertyMapping(ShapeModel.Else.value.iri())
      .withName("else")
      .withObjectRange(Seq(amlAnyNode.iri())),
    PropertyMapping()
      .withId(location + "#/declarations/SchemaObject/allOf")
      .withName("allOf")
      .withNodePropertyMapping(ShapeModel.And.value.iri())
      .withObjectRange(Seq("ShapeObjectId")),
    PropertyMapping()
      .withId(location + "#/declarations/SchemaObject/oneOf")
      .withName("oneOf")
      .withNodePropertyMapping(ShapeModel.Xone.value.iri())
      .withObjectRange(Seq("ShapeObjectId")),
    PropertyMapping()
      .withId(location + "#/declarations/SchemaObject/anyOf")
      .withName("anyOf")
      .withNodePropertyMapping(ShapeModel.Or.value.iri())
      .withObjectRange(Seq("ShapeObjectId")),
    PropertyMapping()
      .withId(location + "#/declarations/SchemaObject/not")
      .withName("not")
      .withNodePropertyMapping(ShapeModel.Not.value.iri())
      .withObjectRange(Seq("ShapeObjectId")),
    PropertyMapping()
      .withId(location + "#/declarations/SchemaObject/required")
      .withName("required")
      .withNodePropertyMapping(PropertyShapeModel.MinCount.value.iri())
      .withLiteralRange(xsdBoolean.iri()),

  )

  override def nodeTypeMapping: String = ShapeModel.`type`.head.iri()
}
