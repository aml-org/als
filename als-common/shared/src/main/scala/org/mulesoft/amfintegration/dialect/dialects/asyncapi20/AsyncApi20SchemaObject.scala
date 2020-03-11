package org.mulesoft.amfintegration.dialect.dialects.asyncapi20

import amf.core.metamodel.domain.ShapeModel
import amf.core.metamodel.domain.extensions.PropertyShapeModel
import amf.core.vocabulary.Namespace.XsdTypes._
import amf.dialects.oas.nodes.AMLSchemaBaseObject
import amf.plugins.document.vocabularies.model.domain.PropertyMapping
import amf.plugins.domain.shapes.metamodel.{ArrayShapeModel, NodeShapeModel, TupleShapeModel}

object AsyncApi20SchemaObject extends AMLSchemaBaseObject {
  override def name: String            = "scheme"
  override def nodeTypeMapping: String = ShapeModel.`type`.head.iri()
  def DialectLocation: String          = AsyncApi20Dialect.DialectLocation

  override def properties: Seq[PropertyMapping] = shapeOnly ++ Seq(
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/SchemaObject/allOf")
      .withName("allOf")
      .withNodePropertyMapping(ShapeModel.And.value.iri())
      .withObjectRange(Seq(AsyncApi20SchemaObject.id)),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/SchemaObject/oneOf")
      .withName("oneOf")
      .withNodePropertyMapping(ShapeModel.Xone.value.iri())
      .withObjectRange(Seq(AsyncApi20SchemaObject.id)),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/SchemaObject/anyOf")
      .withName("anyOf")
      .withNodePropertyMapping(ShapeModel.Or.value.iri())
      .withObjectRange(Seq(AsyncApi20SchemaObject.id)),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/SchemaObject/not")
      .withName("not")
      .withNodePropertyMapping(ShapeModel.Not.value.iri())
      .withObjectRange(Seq(AsyncApi20SchemaObject.id)),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/SchemaObject/writeOnly")
      .withName("writeOnly")
      .withNodePropertyMapping(PropertyShapeModel.WriteOnly.value.iri())
      .withLiteralRange(xsdBoolean.iri()),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/SchemaObject/deprecated")
      .withName("deprecated")
      .withNodePropertyMapping(PropertyShapeModel.Deprecated.value.iri())
      .withLiteralRange(xsdBoolean.iri()),
    PropertyMapping()
      .withId(DialectLocation + s"#/declarations/ShapeObject/maxProperties")
      .withName("maxProperties")
      .withNodePropertyMapping(NodeShapeModel.MaxProperties.value.iri())
      .withLiteralRange(xsdInteger.iri()),
    PropertyMapping()
      .withId(DialectLocation + s"#/declarations/ShapeObject/minProperties")
      .withName("minProperties")
      .withNodePropertyMapping(NodeShapeModel.MinProperties.value.iri())
      .withLiteralRange(xsdInteger.iri()),
    PropertyMapping()
      .withId(DialectLocation + s"#/declarations/ShapeObject/const")
      .withName("const")
      .withNodePropertyMapping(ShapeModel.Values.value.iri())
      .withLiteralRange(xsdAnyType.iri()),
    PropertyMapping()
      .withId(DialectLocation + s"#/declarations/ShapeObject/if")
      .withName("if")
      .withNodePropertyMapping(ShapeModel.If.value.iri())
      .withLiteralRange(AMLSchemaBaseObject.id),
    PropertyMapping()
      .withId(DialectLocation + s"#/declarations/ShapeObject/then")
      .withName("then")
      .withNodePropertyMapping(ShapeModel.Then.value.iri())
      .withLiteralRange(AMLSchemaBaseObject.id),
    PropertyMapping()
      .withId(DialectLocation + s"#/declarations/ShapeObject/else")
      .withName("else")
      .withNodePropertyMapping(ShapeModel.Else.value.iri())
      .withLiteralRange(AMLSchemaBaseObject.id),
    PropertyMapping()
      .withId(DialectLocation + s"#/declarations/ShapeObject/patternProperties")
      .withName("patternProperties")
      .withNodePropertyMapping(NodeShapeModel.Properties.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(DialectLocation + s"#/declarations/SchemaObject/additionalItems")
      .withName("additionalItems")
      .withNodePropertyMapping(TupleShapeModel.AdditionalItemsSchema.value.iri())
      .withLiteralRange(AMLSchemaBaseObject.id),
    PropertyMapping()
      .withId(DialectLocation + s"#/declarations/SchemaObject/propertyNames")
      .withName("propertyNames")
      .withNodePropertyMapping(NodeShapeModel.PropertyNames.value.iri())
      .withLiteralRange(AMLSchemaBaseObject.id),
    PropertyMapping()
      .withId(DialectLocation + s"#/declarations/ShapeObject/contains")
      .withName("contains")
      .withNodePropertyMapping(ArrayShapeModel.Contains.value.iri())
      .withLiteralRange(xsdInteger.iri()),
    PropertyMapping()
      .withId(DialectLocation + s"#declarations/SchemaObject/dependencies")
      .withName("dependencies")
      .withNodePropertyMapping(NodeShapeModel.Dependencies.value.iri())
      .withLiteralRange(xsdString.iri())
      .withAllowMultiple(true)
  )
}
