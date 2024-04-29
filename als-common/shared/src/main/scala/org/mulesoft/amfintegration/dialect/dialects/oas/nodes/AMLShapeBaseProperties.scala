package org.mulesoft.amfintegration.dialect.dialects.oas.nodes

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.{
  amlAnyNode,
  xsdAnyType,
  xsdBoolean,
  xsdDouble,
  xsdInteger,
  xsdString
}
import amf.core.internal.metamodel.domain.ShapeModel
import amf.core.internal.metamodel.domain.extensions.PropertyShapeModel
import amf.shapes.internal.domain.metamodel.{AnyShapeModel, ArrayShapeModel, NodeShapeModel, ScalarShapeModel}
import org.mulesoft.amfintegration.dialect.dialects.oas.OAS20Dialect.{DialectLocation, ImplicitField}

trait AMLShapeBaseProperties {
  val commonShapeFields: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(DialectLocation + s"#/declarations/ShapeObject/multipleOf")
      .withName("multipleOf")
      .withNodePropertyMapping(ScalarShapeModel.MultipleOf.value.iri())
      .withLiteralRange(xsdDouble.iri()),
    PropertyMapping()
      .withId(DialectLocation + s"#/declarations/ShapeObject/maximum")
      .withName("maximum")
      .withNodePropertyMapping(ScalarShapeModel.Maximum.value.iri())
      .withLiteralRange(xsdDouble.iri()),
    PropertyMapping()
      .withId(DialectLocation + s"#/declarations/ShapeObject/exclusiveMaximum")
      .withName("exclusiveMaximum")
      .withNodePropertyMapping(ScalarShapeModel.ExclusiveMaximum.value.iri())
      .withLiteralRange(xsdDouble.iri()),
    PropertyMapping()
      .withId(DialectLocation + s"#/declarations/ShapeObject/minimum")
      .withName("minimum")
      .withNodePropertyMapping(ScalarShapeModel.Minimum.value.iri())
      .withLiteralRange(xsdDouble.iri()),
    PropertyMapping()
      .withId(DialectLocation + s"#/declarations/ShapeObject/exclusiveMinimum")
      .withName("exclusiveMinimum")
      .withNodePropertyMapping(ScalarShapeModel.ExclusiveMinimum.value.iri())
      .withLiteralRange(xsdDouble.iri()),
    PropertyMapping()
      .withId(DialectLocation + s"#/declarations/ShapeObject/maxLength")
      .withName("maxLength")
      .withNodePropertyMapping(ScalarShapeModel.MaxLength.value.iri())
      .withLiteralRange(xsdInteger.iri()),
    PropertyMapping()
      .withId(DialectLocation + s"#/declarations/ShapeObject/minLength")
      .withName("minLength")
      .withNodePropertyMapping(ScalarShapeModel.MinLength.value.iri())
      .withLiteralRange(xsdInteger.iri()),
    PropertyMapping()
      .withId(DialectLocation + s"#/declarations/ShapeObject/pattern")
      .withName("pattern")
      .withNodePropertyMapping(ScalarShapeModel.Pattern.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(DialectLocation + s"#/declarations/ShapeObject/maxItems")
      .withName("maxItems")
      .withNodePropertyMapping(ArrayShapeModel.MaxItems.value.iri())
      .withLiteralRange(xsdInteger.iri()),
    PropertyMapping()
      .withId(DialectLocation + s"#/declarations/ShapeObject/minItems")
      .withName("minItems")
      .withNodePropertyMapping(ArrayShapeModel.MinItems.value.iri())
      .withLiteralRange(xsdInteger.iri()),
    PropertyMapping()
      .withId(DialectLocation + s"#/declarations/ShapeObject/uniqueItems")
      .withName("uniqueItems")
      .withNodePropertyMapping(ArrayShapeModel.UniqueItems.value.iri())
      .withLiteralRange(xsdBoolean.iri()),
    PropertyMapping()
      .withId(DialectLocation + s"#/declarations/ShapeObject/enum")
      .withName("enum")
      .withNodePropertyMapping(ShapeModel.Values.value.iri())
      .withLiteralRange(xsdAnyType.iri()),
    PropertyMapping()
      .withId(DialectLocation + s"#/declarations/ShapeObject/items")
      .withName("items")
      .withNodePropertyMapping(ArrayShapeModel.Items.value.iri())
      .withObjectRange(
        Seq(
          AMLSchemaBaseObject.id
        )
      ),
    PropertyMapping()
      .withId(DialectLocation + s"#/declarations/ShapeObject/format")
      .withName("format")
      .withNodePropertyMapping(ScalarShapeModel.Format.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(DialectLocation + s"#/declarations/ShapeObject/default")
      .withName("default")
      .withNodePropertyMapping(ShapeModel.Default.value.iri())
      .withLiteralRange(xsdAnyType.iri())
  )

  val typeMapping: PropertyMapping = PropertyMapping()
    .withId(DialectLocation + "#/declarations/SchemaObject/Shape/type")
    .withName("type")
    .withMinCount(1)
    .withEnum(
      Seq(
        "object",
        "string",
        "number",
        "integer",
        "boolean",
        "array",
        "file"
      )
    )
    .withNodePropertyMapping(ImplicitField)
    .withLiteralRange(xsdString.iri())
  val shapeOnly: Seq[PropertyMapping] = commonShapeFields ++ Seq(
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/SchemaObject/title")
      .withName("title")
      .withMinCount(1)
      .withNodePropertyMapping(ShapeModel.Name.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/SchemaObject/required")
      .withName("required")
      .withNodePropertyMapping(PropertyShapeModel.MinCount.value.iri())
      .withLiteralRange(xsdBoolean.iri()),
    typeMapping,
    PropertyMapping()
      .withId(DialectLocation + s"#/declarations/SchemaObject/properties")
      .withName("properties")
      .withNodePropertyMapping(NodeShapeModel.Properties.value.iri())
      .withMapTermKeyProperty(PropertyShapeModel.Name.value.iri())
      .withObjectRange(
        Seq(
          AMLSchemaBaseObject.id
        )
      ),
    PropertyMapping()
      .withId(DialectLocation + s"#/declarations/SchemaObject/additionalProperties")
      .withName("additionalProperties")
      .withNodePropertyMapping(NodeShapeModel.AdditionalPropertiesSchema.value.iri())
      .withLiteralRange(xsdBoolean.iri()),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/SchemaObject/description")
      .withName("description")
      .withMinCount(1)
      .withNodePropertyMapping(ShapeModel.Description.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/SchemaObject/discriminator")
      .withName("discriminator")
      .withNodePropertyMapping(NodeShapeModel.Discriminator.value.iri())
      .withLiteralRange(xsdString.iri()),
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
      .withLiteralRange(amlAnyNode.iri())
  )

}
