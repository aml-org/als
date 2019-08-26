package org.mulesoft.als.suggestions.plugins.aml.webapi

import amf.core.metamodel.domain.{DataNodeModel, ShapeModel}
import amf.core.metamodel.domain.extensions.PropertyShapeModel
import amf.core.vocabulary.Namespace.XsdTypes._
import amf.dialects.OAS20Dialect
import amf.dialects.OAS20Dialect.DialectNodes.{ExternalDocumentationObject, XMLObject, commonDataShapesProperties, commonParamProps}
import amf.dialects.OAS20Dialect.{DialectLocation, DialectNodes, ImplicitField, SchemaObjectId}
import amf.plugins.document.vocabularies.model.document.Dialect
import amf.plugins.document.vocabularies.model.domain.{NodeMapping, PropertyMapping, PublicNodeMapping}
import amf.plugins.domain.shapes.metamodel.{AnyShapeModel, ArrayShapeModel, NodeShapeModel, ScalarShapeModel}
import amf.plugins.domain.webapi.metamodel.{OperationModel, ParameterModel, PayloadModel}

package object oas {
  object Oas20DialectWrapper {

    private val PayloadParameter = NodeMapping()
      .withId("#/declarations/PayloadParameter")
      .withName("PayloadPArameter")
      .withNodeTypeMapping(PayloadModel.`type`.head.iri())

    lazy val dialect: Dialect = {

      val d = OAS20Dialect()
      d.withDeclares(
        d.declares.filter(p => !(p.id == SchemaObjectId )) ++ Seq(
          JsonSchemas.SchemaObject,
          JsonSchemas.AnySchemaObject,
          JsonSchemas.ArraySchemaObject,
          JsonSchemas.IntegerSchemaObject,
          JsonSchemas.NodeShapeObject,
          JsonSchemas.NumberSchemaObject,
          JsonSchemas.StringSchemaObject
        ))
    }

    val commonDataShapesProperties: Seq[PropertyMapping] = {
      Seq(
        PropertyMapping()
          .withId(DialectLocation + s"#/declarations/Schema/default")
          .withName("default")
          .withNodePropertyMapping(ShapeModel.Default.value.iri())
          .withLiteralRange(xsdAnyType.iri()),
        PropertyMapping()
          .withId(DialectLocation + s"#/declarations/Schema/enum")
          .withName("enum")
          .withNodePropertyMapping(ShapeModel.Values.value.iri())
          .withLiteralRange(xsdAnyType.iri()),
        PropertyMapping()
          .withId(DialectLocation + s"#/declarations/Schema/allOf")
          .withName("allOf")
          .withNodePropertyMapping(ShapeModel.Inherits.value.iri())
          .withLiteralRange(xsdAnyType.iri()),
      )
    }

    val paramBiding = PropertyMapping()
      .withId(DialectLocation + "#/declarations/ParameterObject/binding")
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
    val paramName = PropertyMapping()
      .withId(DialectLocation + "#/declarations/ParameterObject/name")
      .withName("name")
      .withMinCount(1)
      .withNodePropertyMapping(ParameterModel.Name.value.iri())
      .withLiteralRange(xsdString.iri())

    val basicParamsProps: Seq[PropertyMapping] = Seq(paramBiding, paramName)


    val HeaderCommonObject = NodeMapping()
      .withId("#/declarations/HeaderCommonObject")
      .withName("HeaderCommonObject")
      .withNodeTypeMapping("http://HeaderCommonObject/#mapping")
      .withPropertiesMapping(commonParamProps ++ Seq(

        PropertyMapping()
          .withId(DialectLocation + "#/declarations/ParameterObject/type")
          .withName("type")
          .withMinCount(1)
          .withNodePropertyMapping(
            DialectLocation + "#/declarations/ParameterObject/type")
          .withEnum(
            Seq(
              "string",
              "number",
              "integer",
              "boolean",
              "array",
              "file",
            ))
          .withLiteralRange(xsdString.iri())
      ))

    val HeaderObject = NodeMapping()
      .withId("#/declarations/HeaderObject")
      .withName("HeaderObject")
      .withNodeTypeMapping("http://HeaderObject/#mapping")
      .withPropertiesMapping(
        commonParamProps ++ Seq(
          PropertyMapping()
            .withId(DialectLocation + "#/declarations/ParameterObject/type")
            .withName("type")
            .withMinCount(1)
            .withNodePropertyMapping(
              DialectLocation + "#/declarations/ParameterObject/type")
            .withEnum(
              Seq(
                "string",
                "number",
                "integer",
                "boolean",
                "array",
                "file",
              ))
            .withLiteralRange(xsdString.iri())
        ))

    val ParameterObject = NodeMapping()
      .withId("#/declarations/ParameterObject")
      .withName("ParameterObject")
      .withNodeTypeMapping(ParameterModel.`type`.head.iri())
      .withPropertiesMapping(commonParamProps ++ Seq(
        PropertyMapping()
          .withId(DialectLocation + "#/declarations/ParameterObject/required")
          .withName("required")
          .withMinCount(1)
          .withNodePropertyMapping(ParameterModel.Required.value.iri())
          .withLiteralRange(xsdBoolean.iri()),
        paramBiding
      ))

    // shapes schema
    object JsonSchemas {
      val common: Seq[PropertyMapping] = commonDataShapesProperties ++ Seq(
        OAS20Dialect.shapesPropertyMapping) ++ Seq(
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
          .withObjectRange(Seq(
            XMLObject.id
          )),
        PropertyMapping()
          .withId(DialectLocation + "#/declarations/SchemaObject/externalDocs")
          .withName("externalDocs")
          .withNodePropertyMapping(AnyShapeModel.Documentation.value.iri())
          .withObjectRange(Seq(
            ExternalDocumentationObject.id
          )),
        PropertyMapping()
          .withId(DialectLocation + "#/declarations/SchemaObject/example")
          .withName("example")
          .withNodePropertyMapping(AnyShapeModel.Examples.value.iri())
          .withLiteralRange(DataNodeModel.`type`.head.iri())
      )

      val SchemaObject = NodeMapping()
        .withId(SchemaObjectId)
        .withName("SchemaObject")
        .withNodeTypeMapping(ShapeModel.`type`.head.iri())
        .withPropertiesMapping(common)

      val AnySchemaObject = NodeMapping()
        .withId("#/declarations/AnySchemaObject")
        .withName("AnySchemaObject")
        .withNodeTypeMapping(AnyShapeModel.`type`.head.iri())
        .withPropertiesMapping(common)

      val StringSchemaObject = NodeMapping()
        .withId("#/declarations/StringSchemaObject")
        .withName("StringSchemaObject")
        .withNodeTypeMapping("StringSchemaObject.id")
        .withPropertiesMapping(common ++ Seq(
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
              ))
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
        ))

      val numberProps = common ++ Seq(
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
          .withId(
            DialectLocation + "#/declarations/SchemaObject/exclusiveMaximum")
          .withName("exclusiveMaximum")
          .withNodePropertyMapping(
            ScalarShapeModel.ExclusiveMaximum.value.iri())
          .withLiteralRange(xsdDouble.iri()),
        PropertyMapping()
          .withId(DialectLocation + "#/declarations/SchemaObject/minimum")
          .withName("minimum")
          .withNodePropertyMapping(ScalarShapeModel.Minimum.value.iri())
          .withLiteralRange(xsdDouble.iri()),
        PropertyMapping()
          .withId(
            DialectLocation + "#/declarations/SchemaObject/exclusiveMinimum")
          .withName("exclusiveMinimum")
          .withNodePropertyMapping(
            ScalarShapeModel.ExclusiveMinimum.value.iri())
          .withLiteralRange(xsdDouble.iri()),
      )
      val IntegerSchemaObject = NodeMapping()
        .withId("#/declarations/IntegerSchemaObject")
        .withName("IntegerSchemaObject ")
        .withNodeTypeMapping("IntegerSchemaObject.id")
        .withPropertiesMapping(
          numberProps ++  Seq(
            PropertyMapping()
              .withId(DialectLocation + "#/declarations/SchemaObject/format")
              .withName("format")
              .withNodePropertyMapping(ScalarShapeModel.Format.value.iri())
              .withEnum(Seq(
                "int32",
                "int64"
              ))
              .withLiteralRange(xsdString.iri())
          ))

      val NumberSchemaObject = NodeMapping()
        .withId("#/declarations/NumberSchemaObject")
        .withName("NumberSchemaObject ")
        .withNodeTypeMapping("NumberSchemaObject.id")
        .withPropertiesMapping(
          numberProps ++ Seq(
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
                ))
              .withLiteralRange(xsdString.iri())
          ))

      val ArraySchemaObject = NodeMapping()
        .withId("#/declarations/ArraySchemaObject")
        .withName("ArraySchemaObject")
        .withNodeTypeMapping(ArrayShapeModel.`type`.head.iri())
        .withPropertiesMapping(common ++ Seq(
          PropertyMapping()
            .withId(DialectLocation + s"#/declarations/Schema/items")
            .withName("items")
            .withNodePropertyMapping(ArrayShapeModel.Items.value.iri())
            .withObjectRange(Seq(
              SchemaObjectId
            )),
          PropertyMapping()
            .withId(DialectLocation + s"#/declarations/Schema/collectionFormat")
            .withName("collectionFormat")
            .withNodePropertyMapping(
              ArrayShapeModel.CollectionFormat.value.iri())
            .withEnum(
              Seq(
                "csv",
                "ssv",
                "tsv",
                "pipes",
                "multi"
              ))
            .withLiteralRange(xsdString.iri()),
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
        ))

      val NodeShapeObject = NodeMapping()
        .withId("#/declarations/NodeSchemaObject")
        .withName("NodeSchemaObject")
        .withNodeTypeMapping(NodeShapeModel.`type`.head.iri())
        .withPropertiesMapping(common ++ Seq(
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
            .withObjectRange(Seq(SchemaObjectId)),
          PropertyMapping()
            .withId(
              DialectLocation + s"#/declarations/Schema/additionalProperties")
            .withName("additionalProperties")
            .withNodePropertyMapping(NodeShapeModel.MinProperties.value.iri())
            .withLiteralRange(xsdInteger.iri()),
          PropertyMapping()
            .withId(DialectLocation + s"#/declarations/Schema/discriminator")
            .withName("discriminator")
            .withNodePropertyMapping(NodeShapeModel.Discriminator.value.iri())
            .withLiteralRange(xsdInteger.iri())
        ))

    }
  }
}
