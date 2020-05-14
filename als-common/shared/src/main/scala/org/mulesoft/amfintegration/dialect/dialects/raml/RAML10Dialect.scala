package org.mulesoft.amfintegration.dialect.dialects.raml

import amf.core.annotations.Aliases
import amf.core.metamodel.document.BaseUnitModel
import amf.core.metamodel.domain.extensions.PropertyShapeModel
import amf.core.metamodel.domain.{ModelVocabularies, ShapeModel}
import amf.core.vocabulary.Namespace
import amf.core.vocabulary.Namespace.XsdTypes._
import amf.plugins.document.vocabularies.ReferenceStyles
import amf.plugins.document.vocabularies.model.document.{Dialect, Vocabulary}
import amf.plugins.document.vocabularies.model.domain._
import amf.plugins.domain.shapes.metamodel._
import amf.plugins.domain.webapi.metamodel._
import amf.plugins.domain.webapi.metamodel.templates.{ResourceTypeModel, TraitModel}

object RAML10Dialect {

  // Base location for all information in the OAS20 dialect
  val DialectLocation = "file://vocabularies/dialects/raml10.yaml"

  // Marking syntactic fields in the AST that are not directly mapped to properties in the mdoel
  val ImplicitField = (Namespace.Meta + "implicit").iri()

  object DialectNodes {

    def commonShapeProperties(nodeId: String): Seq[PropertyMapping] = Seq(
      PropertyMapping()
        .withId(DialectLocation + s"#/declarations/$nodeId/DataTypeNode/required")
        .withName("required")
        .withNodePropertyMapping(PropertyShapeModel.MinCount.value.iri())
        .withLiteralRange(xsdBoolean.iri()),
      // Common properties
      PropertyMapping()
        .withId(DialectLocation + s"#/declarations/$nodeId/DataType/default")
        .withName("format")
        .withNodePropertyMapping(AnyShapeModel.Default.value.iri())
        .withLiteralRange(xsdAnyType.iri()),
      // TODO: schema and type can be a literal or a nested type
      PropertyMapping()
        .withId(DialectLocation + s"#/declarations/$nodeId/DataType/schema")
        .withName("schema")
        .withEnum(
          Seq(
            "string",
            "number",
            "integer",
            "float",
            "boolean",
            "array",
            "file",
            "object",
            "date",
            "date-only",
            "time-only",
            "datetime-only",
            "datetime",
            "nil"
          ))
        .withNodePropertyMapping(ImplicitField)
        .withLiteralRange(xsdString.iri()),
      PropertyMapping()
        .withId(DialectLocation + s"#/declarations/$nodeId/DataType/type")
        .withName("type")
        .withEnum(
          Seq(
            "string",
            "number",
            "integer",
            "float",
            "boolean",
            "array",
            "file",
            "object",
            "date",
            "date-only",
            "time-only",
            "datetime-only",
            "datetime",
            "nil"
          ))
        .withNodePropertyMapping(ImplicitField)
        .withLiteralRange(xsdString.iri()),
      PropertyMapping()
        .withId(DialectLocation + s"#/declarations/$nodeId/DataType/inherits")
        .withName("inherits")
        .withNodePropertyMapping(ImplicitField)
        .withAllowMultiple(true)
        .withObjectRange(Seq(
          DataTypeNodeId
        )),
      PropertyMapping()
        .withId(DialectLocation + s"#/declarations/$nodeId/DataType/example")
        .withName("example")
        .withNodePropertyMapping(AnyShapeModel.Examples.value.iri())
        .withObjectRange(
          Seq(
            ExampleNode.id
          )),
      PropertyMapping()
        .withId(DialectLocation + s"#/declarations/$nodeId/DataType/examples")
        .withName("examples")
        .withNodePropertyMapping(AnyShapeModel.Examples.value.iri())
        .withObjectRange(
          Seq(
            ExampleNode.id
          )),
      PropertyMapping()
        .withId(DialectLocation + s"#/declarations/$nodeId/DataType/displayName")
        .withName("examples")
        .withNodePropertyMapping(AnyShapeModel.DisplayName.value.iri())
        .withLiteralRange(xsdString.iri()),
      PropertyMapping()
        .withId(DialectLocation + s"#/declarations/$nodeId/DataType/description")
        .withName("description")
        .withNodePropertyMapping(AnyShapeModel.Description.value.iri())
        .withLiteralRange(xsdString.iri()),
      PropertyMapping()
        .withId(DialectLocation + s"#/declarations/$nodeId/DataType/facets")
        .withName("facets")
        .withNodePropertyMapping(AnyShapeModel.CustomShapePropertyDefinitions.value.iri())
        .withLiteralRange(amlAnyNode.iri()),
      PropertyMapping()
        .withId(DialectLocation + s"#/declarations/$nodeId/DataType/xml")
        .withName("xml")
        .withNodePropertyMapping(AnyShapeModel.XMLSerialization.value.iri())
        .withObjectRange(
          Seq(
            XmlNode.id
          )),
      PropertyMapping()
        .withId(DialectLocation + s"#/declarations/$nodeId/DataType/enum")
        .withName("enum")
        .withNodePropertyMapping(AnyShapeModel.XMLSerialization.value.iri())
        .withAllowMultiple(true)
        .withLiteralRange(amlAnyNode.iri()),
      // Object Type
      PropertyMapping()
        .withId(DialectLocation + s"#/declarations/$nodeId/ObjectTypeNode/properties")
        .withName("properties")
        .withNodePropertyMapping(NodeShapeModel.Properties.value.iri())
        .withMapTermKeyProperty(PropertyShapeModel.Name.value.iri())
        .withObjectRange(Seq(
          DataTypeNodeId
        )),
      PropertyMapping()
        .withId(DialectLocation + s"#/declarations/$nodeId/ObjectTypeNode/minProperties")
        .withName("minProperties")
        .withNodePropertyMapping(NodeShapeModel.MinProperties.value.iri())
        .withLiteralRange(xsdInteger.iri()),
      PropertyMapping()
        .withId(DialectLocation + s"#/declarations/$nodeId/ObjectTypeNode/maxProperties")
        .withName("maxProperties")
        .withNodePropertyMapping(NodeShapeModel.MaxProperties.value.iri())
        .withLiteralRange(xsdInteger.iri()),
      PropertyMapping()
        .withId(DialectLocation + s"#/declarations/$nodeId/ObjectTypeNode/addtionalProperties")
        .withName("additionalProperties")
        .withNodePropertyMapping(NodeShapeModel.AdditionalPropertiesSchema.value.iri())
        .withLiteralRange(xsdBoolean.iri()),
      PropertyMapping()
        .withId(DialectLocation + s"#/declarations/$nodeId/ObjectTypeNode/discriminator")
        .withName("discriminator")
        .withNodePropertyMapping(NodeShapeModel.Discriminator.value.iri())
        .withLiteralRange(xsdString.iri()),
      PropertyMapping()
        .withId(DialectLocation + s"#/declarations/$nodeId/ObjectTypeNode/discriminatorValue")
        .withName("discriminatorValue")
        .withNodePropertyMapping(NodeShapeModel.DiscriminatorValue.value.iri())
        .withLiteralRange(xsdString.iri()),
      // Array type
      PropertyMapping()
        .withId(DialectLocation + s"#/declarations/$nodeId/ArrayTypeNode/items")
        .withName("items")
        .withNodePropertyMapping(ArrayShapeModel.Items.value.iri())
        .withAllowMultiple(true)
        .withObjectRange(Seq(
          DataTypeNodeId
        )),
      PropertyMapping()
        .withId(DialectLocation + s"#/declarations/$nodeId/ArrayTypeNode/minItems")
        .withName("minItems")
        .withNodePropertyMapping(ArrayShapeModel.MinItems.value.iri())
        .withLiteralRange(xsdInteger.iri()),
      PropertyMapping()
        .withId(DialectLocation + s"#/declarations/$nodeId/ArrayTypeNode/maxItems")
        .withName("minItems")
        .withNodePropertyMapping(ArrayShapeModel.MaxItems.value.iri())
        .withLiteralRange(xsdInteger.iri()),
      PropertyMapping()
        .withId(DialectLocation + s"#/declarations/$nodeId/ArrayTypeNode/uniqueItems")
        .withName("uniqueItems")
        .withNodePropertyMapping(ArrayShapeModel.UniqueItems.value.iri())
        .withLiteralRange(xsdBoolean.iri()),
      // Scalar type
      PropertyMapping()
        .withId(DialectLocation + s"#/declarations/$nodeId/ScalarTypeNode/pattern")
        .withName("pattern")
        .withNodePropertyMapping(ScalarShapeModel.Pattern.value.iri())
        .withLiteralRange(xsdString.iri()),
      PropertyMapping()
        .withId(DialectLocation + s"#/declarations/$nodeId/ScalarTypeNode/minLength")
        .withName("minLength")
        .withNodePropertyMapping(ScalarShapeModel.MinLength.value.iri())
        .withLiteralRange(xsdInteger.iri()),
      PropertyMapping()
        .withId(DialectLocation + s"#/declarations/$nodeId/ScalarTypeNode/maxLength")
        .withName("maxLength")
        .withNodePropertyMapping(ScalarShapeModel.MaxLength.value.iri())
        .withLiteralRange(xsdInteger.iri()),
      PropertyMapping()
        .withId(DialectLocation + s"#/declarations/$nodeId/ScalarTypeNode/minimum")
        .withName("minimum")
        .withNodePropertyMapping(ScalarShapeModel.Minimum.value.iri())
        .withLiteralRange(amlNumber.iri()),
      PropertyMapping()
        .withId(DialectLocation + s"#/declarations/$nodeId/ScalarTypeNode/maximum")
        .withName("maximum")
        .withNodePropertyMapping(ScalarShapeModel.Maximum.value.iri())
        .withLiteralRange(amlNumber.iri()),
      PropertyMapping()
        .withId(DialectLocation + s"#/declarations/$nodeId/ScalarTypeNode/format")
        .withName("format")
        .withNodePropertyMapping(ScalarShapeModel.Format.value.iri())
        .withEnum(
          Seq(
            "int8",
            "int16",
            "int32",
            "int64",
            "int",
            "long",
            "float",
            "double"
          ))
        .withLiteralRange(xsdString.iri()),
      PropertyMapping()
        .withId(DialectLocation + s"#/declarations/$nodeId/ScalarTypeNode/multipleOf")
        .withName("multipleOf")
        .withNodePropertyMapping(ScalarShapeModel.MultipleOf.value.iri())
        .withLiteralRange(amlNumber.iri()),
      // file types
      PropertyMapping()
        .withId(DialectLocation + s"#/declarations/$nodeId/FileTypeNode/fileTypes")
        .withName("fileTypes")
        .withNodePropertyMapping(FileShapeModel.FileTypes.value.iri())
        .withLiteralRange(amlNumber.iri())
    )

    val XmlNode = NodeMapping()
      .withId(DialectLocation + "#/declarations/XmlNode")
      .withName("XmlNode")
      .withNodeTypeMapping(XMLSerializerModel.`type`.head.iri())
      .withPropertiesMapping(Seq(
        PropertyMapping()
          .withId(DialectLocation + "#/declarations/XmlNode/name")
          .withNodePropertyMapping(XMLSerializerModel.Name.value.iri())
          .withName("name")
          .withLiteralRange(xsdString.iri()),
        PropertyMapping()
          .withId(DialectLocation + "#/declarations/XmlNode/namespace")
          .withNodePropertyMapping(XMLSerializerModel.Namespace.value.iri())
          .withName("namespace")
          .withLiteralRange(amlLink.iri()),
        PropertyMapping()
          .withId(DialectLocation + "#/declarations/XmlNode/prefix")
          .withNodePropertyMapping(XMLSerializerModel.Prefix.value.iri())
          .withName("prefix")
          .withLiteralRange(xsdString.iri()),
        PropertyMapping()
          .withId(DialectLocation + "#/declarations/XmlNode/attribute")
          .withNodePropertyMapping(XMLSerializerModel.Attribute.value.iri())
          .withName("attribute")
          .withLiteralRange(xsdBoolean.iri()),
        PropertyMapping()
          .withId(DialectLocation + "#/declarations/XmlNode/wrapped")
          .withNodePropertyMapping(XMLSerializerModel.Wrapped.value.iri())
          .withName("wrapped")
          .withLiteralRange(xsdBoolean.iri())
      ))

    val ExampleNode = NodeMapping()
      .withId(DialectLocation + "#/declarations/ExampleNode")
      .withName("ExampleNode")
      .withNodeTypeMapping(ExampleModel.`type`.head.iri())
      .withPropertiesMapping(Seq(
        PropertyMapping()
          .withId(DialectLocation + s"#/declarations/ExampleNode/displayName")
          .withName("displayName")
          .withNodePropertyMapping(ExampleModel.DisplayName.value.iri())
          .withLiteralRange(xsdString.iri()),
        PropertyMapping()
          .withId(DialectLocation + s"#/declarations/ExampleNode/description")
          .withName("description")
          .withNodePropertyMapping(ExampleModel.Description.value.iri())
          .withLiteralRange(xsdString.iri()),
        PropertyMapping()
          .withId(DialectLocation + s"#/declarations/ExampleNode/value")
          .withName("value")
          .withNodePropertyMapping(ExampleModel.Raw.value.iri())
          .withLiteralRange(amlAnyNode.iri()),
        PropertyMapping()
          .withId(DialectLocation + s"#/declarations/ExampleNode/strict")
          .withName("strict")
          .withNodePropertyMapping(ExampleModel.Strict.value.iri())
          .withLiteralRange(xsdBoolean.iri())
      ))

    val DataTypeNodeId = DialectLocation + "#/declarations/DataTypeNode"
    val DataTypeNode = NodeMapping()
      .withId(DataTypeNodeId)
      .withName("DataTypeNode")
      .withNodeTypeMapping(ParameterModel.`type`.head.iri())
      .withPropertiesMapping(commonShapeProperties("DataTypeNode"))

    val DocumentationNode = NodeMapping()
      .withId(DialectLocation + "#/declarations/DocumentationNode")
      .withName("DocumentationNode")
      .withNodeTypeMapping(CreativeWorkModel.`type`.head.iri())
      .withPropertiesMapping(Seq(
        PropertyMapping()
          .withId(DialectLocation + "#/declarations/DocumentationNode/title")
          .withName("title")
          .withNodePropertyMapping(CreativeWorkModel.Title.value.iri())
          .withMinCount(1)
          .withLiteralRange(xsdString.iri()),
        PropertyMapping()
          .withId(DialectLocation + "#/declarations/DocumentationNode/content")
          .withName("content")
          .withNodePropertyMapping(CreativeWorkModel.Description.value.iri())
          .withMinCount(1)
          .withLiteralRange(xsdString.iri())
      ))

    val PayloadNode = NodeMapping()
      .withId(DialectLocation + "#/declarations/PayloadNode")
      .withName("PayloadNode")
      .withNodeTypeMapping(PayloadModel.`type`.head.iri())
      .withPropertiesMapping(
        Seq(
          // TODO: patternName
          PropertyMapping()
            .withId(DialectLocation + s"#/declarations/PayloadNode/mediaType")
            .withName("mediaType")
            .withNodePropertyMapping(PayloadModel.MediaType.value.iri())
            .withObjectRange(Seq(
              DataTypeNodeId
            ))
        ) ++ commonShapeProperties("PayloadNode"))

    val ResourceTypeNode = NodeMapping()
      .withId(DialectLocation + "#/declarations/ResourceTypeNode")
      .withName("ResourceTypeNode")
      .withNodeTypeMapping(ResourceTypeModel.`type`.head.iri())
      .withPropertiesMapping(
        Seq(
          PropertyMapping()
            .withId(DialectLocation + s"#/declarations/ResourceTypeNode/usage")
            .withName("usage")
            .withNodePropertyMapping(BaseUnitModel.Usage.value.iri())
            .withLiteralRange(xsdString.iri())
        ))

    val TraitNode = NodeMapping()
      .withId(DialectLocation + "#/declarations/TraitNode")
      .withName("TraitNode")
      .withNodeTypeMapping(TraitModel.`type`.head.iri())
      .withPropertiesMapping(Seq(
        ))

    val ResponseNode = NodeMapping()
      .withId(DialectLocation + "#/declarations/ResponseNode")
      .withName("ResponseNode")
      .withNodeTypeMapping(ResponseModel.`type`.head.iri())
      .withPropertiesMapping(Seq(
        PropertyMapping()
          .withId(DialectLocation + s"#/declarations/ResponseNode/statusCode")
          .withName("statusCode")
          .withNodePropertyMapping(ResponseModel.StatusCode.value.iri())
          .withLiteralRange(xsdString.iri()),
        PropertyMapping()
          .withId(DialectLocation + s"#/declarations/ResponseNode/description")
          .withName("description")
          .withNodePropertyMapping(ResponseModel.Description.value.iri())
          .withLiteralRange(xsdString.iri()),
        PropertyMapping()
          .withId(DialectLocation + "#/declarations/ResponseNode/headers")
          .withName("headers")
          .withNodePropertyMapping(ResponseModel.Headers.value.iri())
          .withMapTermKeyProperty(ParameterModel.Name.value.iri())
          .withObjectRange(Seq(
            DataTypeNodeId
          )),
        PropertyMapping()
          .withId(DialectLocation + "#/declarations/ResponseNode/body")
          .withName("body")
          .withNodePropertyMapping(ResponseModel.Payloads.value.iri())
          .withMapTermKeyProperty(PayloadModel.MediaType.value.iri())
          .withObjectRange(Seq(
            PayloadNode.id
          ))
      ))

    val MethodNode = NodeMapping()
      .withId(DialectLocation + "#/declarations/MethodNode")
      .withName("MethodNode")
      .withNodeTypeMapping(OperationModel.`type`.head.iri())
      .withPropertiesMapping(Seq(
        PropertyMapping()
          .withId(DialectLocation + s"#/declarations/MethodNode/displayName")
          .withName("displayName")
          .withNodePropertyMapping(OperationModel.Name.value.iri())
          .withLiteralRange(xsdString.iri()),
        PropertyMapping()
          .withId(DialectLocation + s"#/declarations/MethodNode/description")
          .withName("description")
          .withNodePropertyMapping(OperationModel.Description.value.iri())
          .withLiteralRange(xsdString.iri()),
        PropertyMapping()
          .withId(DialectLocation + "#/declarations/MethodNode/Request/parameters")
          .withName("queryParameters")
          .withNodePropertyMapping(RequestModel.QueryParameters.value.iri())
          .withMapTermKeyProperty(ParameterModel.Name.value.iri())
          .withObjectRange(Seq(
            DataTypeNodeId
          )),
        PropertyMapping()
          .withId(DialectLocation + "#/declarations/MethodNode/Request/headers")
          .withName("headers")
          .withNodePropertyMapping(RequestModel.Headers.value.iri())
          .withMapTermKeyProperty(ParameterModel.Name.value.iri())
          .withObjectRange(Seq(
            DataTypeNodeId
          )),
        PropertyMapping()
          .withId(DialectLocation + "#/declarations/MethodNode/Request/queryString")
          .withName("queryString")
          .withNodePropertyMapping(PropertyShapeModel.`type`.head.iri())
          .withMapTermKeyProperty(ShapeModel.Name.value.iri())
          .withObjectRange(Seq(
            DataTypeNodeId
          )),
        PropertyMapping()
          .withId(DialectLocation + "#/declarations/MethodNode/responses")
          .withName("responses")
          .withNodePropertyMapping(OperationModel.Responses.value.iri())
          .withMapTermKeyProperty(ResponseModel.StatusCode.value.iri())
          .withObjectRange(Seq(
            ResponseNode.id
          )),
        PropertyMapping()
          .withId(DialectLocation + "#/declarations/MethodNode/Request/body")
          .withName("body")
          .withNodePropertyMapping(RequestModel.Payloads.value.iri())
          .withMapTermKeyProperty(PayloadModel.MediaType.value.iri())
          .withObjectRange(Seq(
            PayloadNode.id
          )),
        PropertyMapping()
          .withId(DialectLocation + "#/declarations/MethodNode/protocols")
          .withName("protocols")
          .withNodePropertyMapping(OperationModel.Schemes.value.iri())
          .withEnum(Seq(
            "HTTP",
            "HTTPS"
          ))
          .withLiteralRange(xsdString.iri())
          .withAllowMultiple(true),
        PropertyMapping()
          .withId(DialectLocation + "#/declarations/MethodNode/trait")
          .withName("is")
          .withNodePropertyMapping(OperationModel.Extends.value.iri())
          .withAllowMultiple(true)
          .withObjectRange(Seq(OperationModel.Extends.value.iri())), // todo: replace for trait def in dialect
        PropertyMapping()
          .withId(DialectLocation + "#/declarations/ResourceNode/securedBy")
          .withName("securedBy")
          .withNodePropertyMapping(EndPointModel.Security.value.iri())
          .withAllowMultiple(true)
          .withLiteralRange(xsdString.iri()) // object range to secured by?
      ))

    val ResourceNode = NodeMapping()
      .withId(DialectLocation + "#/declarations/ResourceNode")
      .withName("ResourceNode")
      .withNodeTypeMapping(EndPointModel.`type`.head.iri())
      .withPropertiesMapping(Seq(
        PropertyMapping()
          .withId(DialectLocation + s"#/declarations/ResourceNode/displayName")
          .withName("displayName")
          .withNodePropertyMapping(EndPointModel.Name.value.iri())
          .withLiteralRange(xsdString.iri()),
        PropertyMapping()
          .withId(DialectLocation + s"#/declarations/ResourceNode/description")
          .withName("description")
          .withNodePropertyMapping(EndPointModel.Description.value.iri())
          .withLiteralRange(xsdString.iri()),
        PropertyMapping()
          .withId(DialectLocation + "#/declarations/ResourceNode/get")
          .withName("get")
          .withNodePropertyMapping(EndPointModel.Operations.value.iri())
          .withObjectRange(Seq(MethodNode.id)),
        PropertyMapping()
          .withId(DialectLocation + "#/declarations/ResourceNode/put")
          .withName("put")
          .withNodePropertyMapping(EndPointModel.Operations.value.iri())
          .withObjectRange(Seq(MethodNode.id)),
        PropertyMapping()
          .withId(DialectLocation + "#/declarations/ResourceNode/post")
          .withName("post")
          .withNodePropertyMapping(EndPointModel.Operations.value.iri())
          .withObjectRange(Seq(MethodNode.id)),
        PropertyMapping()
          .withId(DialectLocation + "#/declarations/ResourceNode/delete")
          .withName("delete")
          .withNodePropertyMapping(EndPointModel.Operations.value.iri())
          .withObjectRange(Seq(MethodNode.id)),
        PropertyMapping()
          .withId(DialectLocation + "#/declarations/ResourceNode/options")
          .withName("options")
          .withNodePropertyMapping(EndPointModel.Operations.value.iri())
          .withObjectRange(Seq(MethodNode.id)),
        PropertyMapping()
          .withId(DialectLocation + "#/declarations/ResourceNode/head")
          .withName("head")
          .withNodePropertyMapping(EndPointModel.Operations.value.iri())
          .withObjectRange(Seq(MethodNode.id)),
        PropertyMapping()
          .withId(DialectLocation + "#/declarations/ResourceNode/patch")
          .withName("patch")
          .withNodePropertyMapping(EndPointModel.Operations.value.iri())
          .withObjectRange(Seq(MethodNode.id)),
        PropertyMapping()
          .withId(DialectLocation + "#/declarations/ResourceNode/is")
          .withName("is")
          .withNodePropertyMapping(EndPointModel.Extends.value.iri())
          .withObjectRange(Seq(TraitNode.id)),
        PropertyMapping()
          .withId(DialectLocation + "#/declarations/ResourceNode/type")
          .withName("type")
          .withNodePropertyMapping(EndPointModel.Extends.value.iri())
          .withObjectRange(Seq(ResourceTypeNode.id)),
        PropertyMapping()
          .withId(DialectLocation + "#/declarations/ResourceNode/securedBy")
          .withName("securedBy")
          .withNodePropertyMapping(EndPointModel.Security.value.iri())
          .withAllowMultiple(true)
          .withLiteralRange(xsdString.iri()),
        PropertyMapping()
          .withId(DialectLocation + "#/declarations/ResourceNode/uriParameters")
          .withName("uriParameters")
          .withNodePropertyMapping(EndPointModel.Parameters.value.iri())
          .withMapTermKeyProperty(ParameterModel.Name.value.iri())
          .withObjectRange(Seq(
            DataTypeNodeId
          ))
      ))

    val RootNode = NodeMapping()
      .withId(DialectLocation + "#/declarations/RootNode")
      .withName("RootNode")
      .withNodeTypeMapping(WebApiModel.`type`.head.iri())
      .withPropertiesMapping(Seq(
        PropertyMapping()
          .withId(DialectLocation + "#/declarations/RootNode/title")
          .withName("title")
          .withMinCount(1)
          .withNodePropertyMapping(WebApiModel.Name.value.iri())
          .withLiteralRange(xsdString.iri()),
        PropertyMapping()
          .withId(DialectLocation + "#/declarations/RootNode/description")
          .withName("description")
          .withNodePropertyMapping(WebApiModel.Description.value.iri())
          .withLiteralRange(xsdString.iri()),
        PropertyMapping()
          .withId(DialectLocation + "#/declarations/RootNode/version")
          .withName("version")
          .withNodePropertyMapping(WebApiModel.Version.value.iri())
          .withLiteralRange(xsdString.iri()),
        PropertyMapping()
          .withId(DialectLocation + "#/declarations/RootNode/baseUri")
          .withName("baseUri")
          .withNodePropertyMapping(ServerModel.Url.value.iri())
          .withLiteralRange(xsdString.iri()),
        PropertyMapping()
          .withId(DialectLocation + "#/declarations/RootNode/baseUriParameters")
          .withName("baseUriParameters")
          .withNodePropertyMapping(ServerModel.Variables.value.iri())
          .withMapTermKeyProperty(ParameterModel.Name.value.iri())
          .withObjectRange(Seq(
            DataTypeNodeId
          )),
        PropertyMapping()
          .withId(DialectLocation + "#/declarations/RootNode/protocols")
          .withName("protocols")
          .withNodePropertyMapping(WebApiModel.Schemes.value.iri())
          .withEnum(Seq(
            "HTTP",
            "HTTPS"
          ))
          .withLiteralRange(xsdString.iri())
          .withAllowMultiple(true),
        PropertyMapping()
          .withId(DialectLocation + "#/declarations/RootNode/mediaType")
          .withName("mediaType")
          .withNodePropertyMapping(WebApiModel.Accepts.value.iri())
          .withLiteralRange(xsdString.iri()),
        PropertyMapping()
          .withId(DialectLocation + "#/declarations/RootNode/documentation")
          .withName("documentation")
          .withAllowMultiple(true)
          .withNodePropertyMapping(WebApiModel.Documentations.value.iri())
          .withObjectRange(Seq(
            DocumentationNode.id
          )),
        PropertyMapping()
          .withId(DialectLocation + "#/declarations/RootNode/securedBy")
          .withName("securedBy")
          .withAllowMultiple(true)
          .withNodePropertyMapping(WebApiModel.Security.value.iri())
          .withLiteralRange(xsdString.iri())
      ))
  }

  // Dialect
  val dialect = {
    val d = Dialect()
      .withId(DialectLocation)
      .withName("RAML")
      .withVersion("1.0")
      .withLocation(DialectLocation)
      .withId(DialectLocation)
      .withDeclares(Seq(
        DialectNodes.XmlNode,
        DialectNodes.ExampleNode,
        DialectNodes.DataTypeNode,
        DialectNodes.DocumentationNode,
        DialectNodes.PayloadNode,
        DialectNodes.ResourceTypeNode,
        DialectNodes.TraitNode,
        DialectNodes.ResponseNode,
        DialectNodes.MethodNode,
        DialectNodes.ResourceNode,
        DialectNodes.RootNode
      ))
      .withDocuments(
        DocumentsModel()
          .withId(DialectLocation + "#/documents")
          .withReferenceStyle(ReferenceStyles.RAML)
          .withRoot(
            DocumentMapping()
              .withId(DialectLocation + "#/documents/root")
              .withEncoded(DialectNodes.RootNode.id)
          ))

    d.withExternals(
      Seq(
        External()
          .withId(DialectLocation + "#/externals/core")
          .withAlias("core")
          .withBase(Namespace.Core.base),
        External()
          .withId(DialectLocation + "#/externals/shacl")
          .withAlias("shacl")
          .withBase(Namespace.Shacl.base),
        External()
          .withId(DialectLocation + "#/externals/apiContract")
          .withAlias("apiContract")
          .withBase(Namespace.ApiContract.base),
        External()
          .withId(DialectLocation + "#/externals/meta")
          .withAlias("meta")
          .withBase(Namespace.Meta.base),
        External()
          .withId(DialectLocation + "#/externals/owl")
          .withAlias("owl")
          .withBase(Namespace.Owl.base)
      ))

    val vocabularies = Seq(
      ModelVocabularies.AmlDoc,
      ModelVocabularies.ApiContract,
      ModelVocabularies.Core,
      ModelVocabularies.Shapes,
      ModelVocabularies.Meta,
      ModelVocabularies.Security
    )
    d.annotations += Aliases(vocabularies.map { vocab =>
      (vocab.alias, (vocab.base, vocab.filename))
    }.toSet)

    d.withReferences(vocabularies.map { vocab =>
      Vocabulary()
        .withLocation(vocab.filename)
        .withId(vocab.filename)
        .withBase(vocab.base)
    })

    d
  }

  def apply(): Dialect = dialect
}
