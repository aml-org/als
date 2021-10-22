package org.mulesoft.amfintegration.dialect.dialects.raml

import amf.aml.client.scala.model.domain.{NodeMapping, PropertyMapping}
import amf.apicontract.internal.metamodel.domain.api.WebApiModel
import amf.apicontract.internal.metamodel.domain.templates.{ResourceTypeModel, TraitModel}
import amf.apicontract.internal.metamodel.domain.{EndPointModel, OperationModel, ParameterModel, PayloadModel, RequestModel, ResponseModel, ServerModel}
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.{amlAnyNode, amlNumber, xsdAnyType, xsdBoolean, xsdInteger, xsdString}
import amf.core.internal.metamodel.document.BaseUnitModel
import amf.core.internal.metamodel.domain.extensions.PropertyShapeModel
import amf.shapes.internal.domain.metamodel.{AnyShapeModel, ArrayShapeModel, CreativeWorkModel, ExampleModel, FileShapeModel, NodeShapeModel, ScalarShapeModel}

trait RamlDialectNodes {
  protected def scalarTypes: Seq[String] = Seq(
    "string",
    "number",
    "integer",
    "boolean",
    "file",
    "date"
  )

  protected def dialectLocation: String

  protected val implicitField: String
  def commonShapeProperties(nodeId: String): Seq[PropertyMapping] = {
    Seq(
      // Common properties
      PropertyMapping()
        .withId(dialectLocation + s"#/declarations/$nodeId/DataType/default")
        .withName("format")
        .withNodePropertyMapping(AnyShapeModel.Default.value.iri())
        .withLiteralRange(xsdAnyType.iri()),
      // TODO: schema and type can be a literal or a nested type
      PropertyMapping()
        .withId(dialectLocation + s"#/declarations/$nodeId/DataType/schema")
        .withName("schema")
        .withEnum(scalarTypes)
        .withNodePropertyMapping(implicitField)
        .withLiteralRange(xsdString.iri()),
      PropertyMapping()
        .withId(dialectLocation + s"#/declarations/$nodeId/DataType/type")
        .withName("type")
        .withEnum(scalarTypes)
        .withNodePropertyMapping(implicitField)
        .withLiteralRange(xsdString.iri()),
      PropertyMapping()
        .withId(dialectLocation + s"#/declarations/$nodeId/DataType/inherits")
        .withName("inherits")
        .withNodePropertyMapping(implicitField)
        .withAllowMultiple(true)
        .withObjectRange(Seq(
          DataTypeNodeId
        )),
      PropertyMapping()
        .withId(dialectLocation + s"#/declarations/$nodeId/DataType/example")
        .withName("example")
        .withNodePropertyMapping(AnyShapeModel.Examples.value.iri())
        .withObjectRange(
          Seq(
            ExampleNode.id
          )),
      PropertyMapping()
        .withId(dialectLocation + s"#/declarations/$nodeId/DataType/displayName")
        .withName("examples")
        .withNodePropertyMapping(AnyShapeModel.DisplayName.value.iri())
        .withMapTermKeyProperty(ExampleModel.Name.value.iri())
        .withObjectRange(Seq(ExampleNode.id)),
      PropertyMapping()
        .withId(dialectLocation + s"#/declarations/$nodeId/DataType/description")
        .withName("description")
        .withNodePropertyMapping(AnyShapeModel.Description.value.iri())
        .withLiteralRange(xsdString.iri()),
      PropertyMapping()
        .withId(dialectLocation + s"#/declarations/$nodeId/DataType/enum")
        .withName("enum")
        .withNodePropertyMapping(AnyShapeModel.XMLSerialization.value.iri())
        .withAllowMultiple(true)
        .withLiteralRange(amlAnyNode.iri()),
      // Object Type
      PropertyMapping()
        .withId(dialectLocation + s"#/declarations/$nodeId/ObjectTypeNode/properties")
        .withName("properties")
        .withNodePropertyMapping(NodeShapeModel.Properties.value.iri())
        .withMapTermKeyProperty(PropertyShapeModel.Name.value.iri())
        .withObjectRange(Seq(
          DataTypeNodeId
        )),
      PropertyMapping()
        .withId(dialectLocation + s"#/declarations/$nodeId/ObjectTypeNode/minProperties")
        .withName("minProperties")
        .withNodePropertyMapping(NodeShapeModel.MinProperties.value.iri())
        .withLiteralRange(xsdInteger.iri()),
      PropertyMapping()
        .withId(dialectLocation + s"#/declarations/$nodeId/ObjectTypeNode/maxProperties")
        .withName("maxProperties")
        .withNodePropertyMapping(NodeShapeModel.MaxProperties.value.iri())
        .withLiteralRange(xsdInteger.iri()),
      PropertyMapping()
        .withId(dialectLocation + s"#/declarations/$nodeId/ObjectTypeNode/addtionalProperties")
        .withName("additionalProperties")
        .withNodePropertyMapping(NodeShapeModel.AdditionalPropertiesSchema.value.iri())
        .withLiteralRange(xsdBoolean.iri()),
      PropertyMapping()
        .withId(dialectLocation + s"#/declarations/$nodeId/ObjectTypeNode/discriminator")
        .withName("discriminator")
        .withNodePropertyMapping(NodeShapeModel.Discriminator.value.iri())
        .withLiteralRange(xsdString.iri()),
      PropertyMapping()
        .withId(dialectLocation + s"#/declarations/$nodeId/ObjectTypeNode/discriminatorValue")
        .withName("discriminatorValue")
        .withNodePropertyMapping(NodeShapeModel.DiscriminatorValue.value.iri())
        .withLiteralRange(xsdString.iri()),
      PropertyMapping()
        .withId(dialectLocation + s"#/declarations/$nodeId/ArrayTypeNode/minItems")
        .withName("minItems")
        .withNodePropertyMapping(ArrayShapeModel.MinItems.value.iri())
        .withLiteralRange(xsdInteger.iri()),
      PropertyMapping()
        .withId(dialectLocation + s"#/declarations/$nodeId/ArrayTypeNode/maxItems")
        .withName("minItems")
        .withNodePropertyMapping(ArrayShapeModel.MaxItems.value.iri())
        .withLiteralRange(xsdInteger.iri()),
      PropertyMapping()
        .withId(dialectLocation + s"#/declarations/$nodeId/ArrayTypeNode/uniqueItems")
        .withName("uniqueItems")
        .withNodePropertyMapping(ArrayShapeModel.UniqueItems.value.iri())
        .withLiteralRange(xsdBoolean.iri()),
      // Scalar type
      PropertyMapping()
        .withId(dialectLocation + s"#/declarations/$nodeId/ScalarTypeNode/pattern")
        .withName("pattern")
        .withNodePropertyMapping(ScalarShapeModel.Pattern.value.iri())
        .withLiteralRange(xsdString.iri()),
      PropertyMapping()
        .withId(dialectLocation + s"#/declarations/$nodeId/ScalarTypeNode/minLength")
        .withName("minLength")
        .withNodePropertyMapping(ScalarShapeModel.MinLength.value.iri())
        .withLiteralRange(xsdInteger.iri()),
      PropertyMapping()
        .withId(dialectLocation + s"#/declarations/$nodeId/ScalarTypeNode/maxLength")
        .withName("maxLength")
        .withNodePropertyMapping(ScalarShapeModel.MaxLength.value.iri())
        .withLiteralRange(xsdInteger.iri()),
      PropertyMapping()
        .withId(dialectLocation + s"#/declarations/$nodeId/ScalarTypeNode/minimum")
        .withName("minimum")
        .withNodePropertyMapping(ScalarShapeModel.Minimum.value.iri())
        .withLiteralRange(amlNumber.iri()),
      PropertyMapping()
        .withId(dialectLocation + s"#/declarations/$nodeId/ScalarTypeNode/maximum")
        .withName("maximum")
        .withNodePropertyMapping(ScalarShapeModel.Maximum.value.iri())
        .withLiteralRange(amlNumber.iri()),
      PropertyMapping()
        .withId(dialectLocation + s"#/declarations/$nodeId/ScalarTypeNode/format")
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
        .withId(dialectLocation + s"#/declarations/$nodeId/ScalarTypeNode/multipleOf")
        .withName("multipleOf")
        .withNodePropertyMapping(ScalarShapeModel.MultipleOf.value.iri())
        .withLiteralRange(amlNumber.iri()),
      // file types
      PropertyMapping()
        .withId(dialectLocation + s"#/declarations/$nodeId/FileTypeNode/fileTypes")
        .withName("fileTypes")
        .withNodePropertyMapping(FileShapeModel.FileTypes.value.iri())
        .withLiteralRange(amlNumber.iri())
    )
  }

  final lazy val ExampleNode: NodeMapping = NodeMapping()
    .withId(dialectLocation + "#/declarations/ExampleNode")
    .withName("ExampleNode")
    .withNodeTypeMapping(ExampleModel.`type`.head.iri())
    .withPropertiesMapping(Seq(
      PropertyMapping()
        .withId(dialectLocation + s"#/declarations/ExampleNode/displayName")
        .withName("displayName")
        .withNodePropertyMapping(ExampleModel.DisplayName.value.iri())
        .withLiteralRange(xsdString.iri()),
      PropertyMapping()
        .withId(dialectLocation + s"#/declarations/ExampleNode/name")
        .withName("name")
        .withNodePropertyMapping(ExampleModel.Name.value.iri())
        .withLiteralRange(xsdString.iri()),
      PropertyMapping()
        .withId(dialectLocation + s"#/declarations/ExampleNode/description")
        .withName("description")
        .withNodePropertyMapping(ExampleModel.Description.value.iri())
        .withLiteralRange(xsdString.iri()),
      PropertyMapping()
        .withId(dialectLocation + s"#/declarations/ExampleNode/value")
        .withName("value")
        .withNodePropertyMapping(ExampleModel.Raw.value.iri())
        .withLiteralRange(amlAnyNode.iri()),
      PropertyMapping()
        .withId(dialectLocation + s"#/declarations/ExampleNode/strict")
        .withName("strict")
        .withNodePropertyMapping(ExampleModel.Strict.value.iri())
        .withLiteralRange(xsdBoolean.iri())
    ))

  final lazy val DataTypeNodeId: String = dialectLocation + "#/declarations/DataTypeNode"
  final lazy val DataTypeNode: NodeMapping = NodeMapping()
    .withId(DataTypeNodeId)
    .withName("DataTypeNode")
    .withNodeTypeMapping(ParameterModel.`type`.head.iri())
    .withPropertiesMapping(commonShapeProperties("DataTypeNode") :+ PropertyMapping()
      .withId(dialectLocation + s"#/declarations/DataTypeNode/DataTypeNode/required")
      .withName("required")
      .withNodePropertyMapping(ParameterModel.Required.value.iri())
      .withLiteralRange(xsdBoolean.iri()),
    )

  final lazy val DocumentationNode: NodeMapping = NodeMapping()
    .withId(dialectLocation + "#/declarations/DocumentationNode")
    .withName("DocumentationNode")
    .withNodeTypeMapping(CreativeWorkModel.`type`.head.iri())
    .withPropertiesMapping(Seq(
      PropertyMapping()
        .withId(dialectLocation + "#/declarations/DocumentationNode/title")
        .withName("title")
        .withNodePropertyMapping(CreativeWorkModel.Title.value.iri())
        .withMinCount(1)
        .withLiteralRange(xsdString.iri()),
      PropertyMapping()
        .withId(dialectLocation + "#/declarations/DocumentationNode/content")
        .withName("content")
        .withNodePropertyMapping(CreativeWorkModel.Description.value.iri())
        .withMinCount(1)
        .withLiteralRange(xsdString.iri())
    ))

  final lazy val PayloadNode: NodeMapping = NodeMapping()
    .withId(dialectLocation + "#/declarations/PayloadNode")
    .withName("PayloadNode")
    .withNodeTypeMapping(PayloadModel.`type`.head.iri())
    .withPropertiesMapping(
      Seq(
        // TODO: patternName
        PropertyMapping()
          .withId(dialectLocation + s"#/declarations/PayloadNode/mediaType")
          .withName("mediaType")
          .withNodePropertyMapping(PayloadModel.MediaType.value.iri())
          .withObjectRange(Seq(
            DataTypeNodeId
          ))
      ) ++ commonShapeProperties("PayloadNode"))

  final lazy val ResourceTypeNode: NodeMapping = NodeMapping()
    .withId(dialectLocation + "#/declarations/ResourceTypeNode")
    .withName("ResourceTypeNode")
    .withNodeTypeMapping(ResourceTypeModel.`type`.head.iri())
    .withPropertiesMapping(
      Seq(
        PropertyMapping()
          .withId(dialectLocation + s"#/declarations/ResourceTypeNode/usage")
          .withName("usage")
          .withNodePropertyMapping(BaseUnitModel.Usage.value.iri())
          .withLiteralRange(xsdString.iri())
      ))

  final lazy val TraitNode: NodeMapping = NodeMapping()
    .withId(dialectLocation + "#/declarations/TraitNode")
    .withName("TraitNode")
    .withNodeTypeMapping(TraitModel.`type`.head.iri())
    .withPropertiesMapping(
      Seq(
        PropertyMapping()
          .withId(dialectLocation + s"#/declarations/TraitNode/usage")
          .withName("usage")
          .withNodePropertyMapping(BaseUnitModel.Usage.value.iri())
          .withLiteralRange(xsdString.iri())
      ))

  final lazy val ResponseNode: NodeMapping = NodeMapping()
    .withId(dialectLocation + "#/declarations/ResponseNode")
    .withName("ResponseNode")
    .withNodeTypeMapping(ResponseModel.`type`.head.iri())
    .withPropertiesMapping(Seq(
      PropertyMapping()
        .withId(dialectLocation + s"#/declarations/ResponseNode/statusCode")
        .withName("statusCode")
        .withNodePropertyMapping(ResponseModel.StatusCode.value.iri())
        .withLiteralRange(xsdString.iri()),
      PropertyMapping()
        .withId(dialectLocation + s"#/declarations/ResponseNode/description")
        .withName("description")
        .withNodePropertyMapping(ResponseModel.Description.value.iri())
        .withLiteralRange(xsdString.iri()),
      PropertyMapping()
        .withId(dialectLocation + "#/declarations/ResponseNode/headers")
        .withName("headers")
        .withNodePropertyMapping(ResponseModel.Headers.value.iri())
        .withMapTermKeyProperty(ParameterModel.Name.value.iri())
        .withObjectRange(Seq(
          DataTypeNodeId
        )),
      PropertyMapping()
        .withId(dialectLocation + "#/declarations/ResponseNode/body")
        .withName("body")
        .withNodePropertyMapping(ResponseModel.Payloads.value.iri())
        .withMapTermKeyProperty(PayloadModel.MediaType.value.iri())
        .withObjectRange(Seq(
          PayloadNode.id
        ))
    ))
  protected def methodNodeMappings: Seq[PropertyMapping]
  final protected val innerMethodNodeMappings: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(dialectLocation + s"#/declarations/MethodNode/displayName")
      .withName("displayName")
      .withNodePropertyMapping(OperationModel.Name.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(dialectLocation + s"#/declarations/MethodNode/description")
      .withName("description")
      .withNodePropertyMapping(OperationModel.Description.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(dialectLocation + "#/declarations/MethodNode/Request/parameters")
      .withName("queryParameters")
      .withNodePropertyMapping(RequestModel.QueryParameters.value.iri())
      .withMapTermKeyProperty(ParameterModel.Name.value.iri())
      .withObjectRange(Seq(
        DataTypeNodeId
      )),
    PropertyMapping()
      .withId(dialectLocation + "#/declarations/MethodNode/Request/headers")
      .withName("headers")
      .withNodePropertyMapping(RequestModel.Headers.value.iri())
      .withMapTermKeyProperty(ParameterModel.Name.value.iri())
      .withObjectRange(Seq(
        DataTypeNodeId
      )),
    PropertyMapping()
      .withId(dialectLocation + "#/declarations/MethodNode/responses")
      .withName("responses")
      .withNodePropertyMapping(OperationModel.Responses.value.iri())
      .withMapTermKeyProperty(ResponseModel.StatusCode.value.iri())
      .withObjectRange(Seq(
        ResponseNode.id
      )),
    PropertyMapping()
      .withId(dialectLocation + "#/declarations/MethodNode/Request/body")
      .withName("body")
      .withNodePropertyMapping(RequestModel.Payloads.value.iri())
      .withMapTermKeyProperty(PayloadModel.MediaType.value.iri())
      .withObjectRange(Seq(
        PayloadNode.id
      )),
    PropertyMapping()
      .withId(dialectLocation + "#/declarations/MethodNode/protocols")
      .withName("protocols")
      .withNodePropertyMapping(OperationModel.Schemes.value.iri())
      .withEnum(
        Seq(
          "HTTP",
          "HTTPS"
        ))
      .withLiteralRange(xsdString.iri())
      .withAllowMultiple(true),
    PropertyMapping()
      .withId(dialectLocation + "#/declarations/MethodNode/trait")
      .withName("is")
      .withNodePropertyMapping(OperationModel.Extends.value.iri())
      .withAllowMultiple(true)
      .withObjectRange(Seq(OperationModel.Extends.value.iri())), // todo: replace for trait def in dialect
    PropertyMapping()
      .withId(dialectLocation + "#/declarations/ResourceNode/securedBy")
      .withName("securedBy")
      .withNodePropertyMapping(EndPointModel.Security.value.iri())
      .withAllowMultiple(true)
      .withLiteralRange(xsdString.iri()) // object range to secured by?
  )
  final lazy val MethodNode: NodeMapping = NodeMapping()
    .withId(dialectLocation + "#/declarations/MethodNode")
    .withName("MethodNode")
    .withNodeTypeMapping(OperationModel.`type`.head.iri())
    .withPropertiesMapping(methodNodeMappings)

  protected def resourceNodeMappings: Seq[PropertyMapping]
  final protected val innerResourceNodeMappings: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(dialectLocation + s"#/declarations/ResourceNode/displayName")
      .withName("displayName")
      .withNodePropertyMapping(EndPointModel.Name.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(dialectLocation + s"#/declarations/ResourceNode/description")
      .withName("description")
      .withNodePropertyMapping(EndPointModel.Description.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(dialectLocation + "#/declarations/ResourceNode/get")
      .withName("get")
      .withNodePropertyMapping(EndPointModel.Operations.value.iri())
      .withObjectRange(Seq(MethodNode.id)),
    PropertyMapping()
      .withId(dialectLocation + "#/declarations/ResourceNode/put")
      .withName("put")
      .withNodePropertyMapping(EndPointModel.Operations.value.iri())
      .withObjectRange(Seq(MethodNode.id)),
    PropertyMapping()
      .withId(dialectLocation + "#/declarations/ResourceNode/post")
      .withName("post")
      .withNodePropertyMapping(EndPointModel.Operations.value.iri())
      .withObjectRange(Seq(MethodNode.id)),
    PropertyMapping()
      .withId(dialectLocation + "#/declarations/ResourceNode/delete")
      .withName("delete")
      .withNodePropertyMapping(EndPointModel.Operations.value.iri())
      .withObjectRange(Seq(MethodNode.id)),
    PropertyMapping()
      .withId(dialectLocation + "#/declarations/ResourceNode/options")
      .withName("options")
      .withNodePropertyMapping(EndPointModel.Operations.value.iri())
      .withObjectRange(Seq(MethodNode.id)),
    PropertyMapping()
      .withId(dialectLocation + "#/declarations/ResourceNode/head")
      .withName("head")
      .withNodePropertyMapping(EndPointModel.Operations.value.iri())
      .withObjectRange(Seq(MethodNode.id)),
    PropertyMapping()
      .withId(dialectLocation + "#/declarations/ResourceNode/patch")
      .withName("patch")
      .withNodePropertyMapping(EndPointModel.Operations.value.iri())
      .withObjectRange(Seq(MethodNode.id)),
    PropertyMapping()
      .withId(dialectLocation + "#/declarations/ResourceNode/is")
      .withName("is")
      .withAllowMultiple(true)
      .withNodePropertyMapping(EndPointModel.Extends.value.iri())
      .withObjectRange(Seq(TraitNode.id)),
    PropertyMapping()
      .withId(dialectLocation + "#/declarations/ResourceNode/type")
      .withName("type")
      .withNodePropertyMapping(EndPointModel.Extends.value.iri())
      .withObjectRange(Seq(ResourceTypeNode.id)),
    PropertyMapping()
      .withId(dialectLocation + "#/declarations/ResourceNode/securedBy")
      .withName("securedBy")
      .withNodePropertyMapping(EndPointModel.Security.value.iri())
      .withAllowMultiple(true)
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(dialectLocation + "#/declarations/ResourceNode/uriParameters")
      .withName("uriParameters")
      .withNodePropertyMapping(EndPointModel.Parameters.value.iri())
      .withMapTermKeyProperty(ParameterModel.Name.value.iri())
      .withObjectRange(Seq(
        DataTypeNodeId
      ))
  )
  final lazy val ResourceNode: NodeMapping = NodeMapping()
    .withId(dialectLocation + "#/declarations/ResourceNode")
    .withName("ResourceNode")
    .withNodeTypeMapping(EndPointModel.`type`.head.iri())
    .withPropertiesMapping(resourceNodeMappings)

  protected def rootMappings: Seq[PropertyMapping]
  final protected val innerRootMappings: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(dialectLocation + "#/declarations/RootNode/title")
      .withName("title")
      .withMinCount(1)
      .withNodePropertyMapping(WebApiModel.Name.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(dialectLocation + "#/declarations/RootNode/version")
      .withName("version")
      .withNodePropertyMapping(WebApiModel.Version.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(dialectLocation + "#/declarations/RootNode/baseUri")
      .withName("baseUri")
      .withNodePropertyMapping(ServerModel.Url.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(dialectLocation + "#/declarations/RootNode/baseUriParameters")
      .withName("baseUriParameters")
      .withNodePropertyMapping(ServerModel.Variables.value.iri())
      .withMapTermKeyProperty(ParameterModel.Name.value.iri())
      .withObjectRange(Seq(
        DataTypeNodeId
      )),
    PropertyMapping()
      .withId(dialectLocation + "#/declarations/RootNode/protocols")
      .withName("protocols")
      .withNodePropertyMapping(WebApiModel.Schemes.value.iri())
      .withEnum(
        Seq(
          "HTTP",
          "HTTPS"
        ))
      .withLiteralRange(xsdString.iri())
      .withAllowMultiple(true),
    PropertyMapping()
      .withId(dialectLocation + "#/declarations/RootNode/mediaType")
      .withName("mediaType")
      .withNodePropertyMapping(WebApiModel.Accepts.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(dialectLocation + "#/declarations/RootNode/documentation")
      .withName("documentation")
      .withAllowMultiple(true)
      .withNodePropertyMapping(WebApiModel.Documentations.value.iri())
      .withObjectRange(Seq(
        DocumentationNode.id
      )),
    PropertyMapping()
      .withId(dialectLocation + "#/declarations/RootNode/securedBy")
      .withName("securedBy")
      .withAllowMultiple(true)
      .withNodePropertyMapping(WebApiModel.Security.value.iri())
      .withLiteralRange(xsdString.iri())
  )
  final lazy val RootNode: NodeMapping = NodeMapping()
    .withId(dialectLocation + "#/declarations/RootNode")
    .withName("RootNode")
    .withNodeTypeMapping(WebApiModel.`type`.head.iri())
    .withPropertiesMapping(rootMappings)
}
