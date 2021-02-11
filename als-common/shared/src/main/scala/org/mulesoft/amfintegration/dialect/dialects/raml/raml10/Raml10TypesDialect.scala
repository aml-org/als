package org.mulesoft.amfintegration.dialect.dialects.raml.raml10

import amf.core.metamodel.domain.ShapeModel
import amf.core.metamodel.domain.extensions.{CustomDomainPropertyModel, PropertyShapeModel}
import amf.core.vocabulary.Namespace
import amf.core.vocabulary.Namespace.XsdTypes.{xsdBoolean, xsdFloat, xsdInteger, xsdString}
import Raml10DialectNodes.ExampleNode
import amf.plugins.document.vocabularies.model.document.Dialect
import amf.plugins.document.vocabularies.model.domain.{
  DocumentMapping,
  NodeMapping,
  PropertyMapping,
  PublicNodeMapping
}
import amf.plugins.domain.shapes.metamodel._
import org.mulesoft.amfintegration.dialect.dialects.raml.RamlDialect

// use this dialect until refactor
object Raml10TypesDialect {

  final val ImplicitField: String = (Namespace.Meta + "implicit").iri()

  // do not use this one
  private object Raml10Dialect extends RamlDialect {

    override val dialectLocation = "file://vocabularies/dialects/raml10.yaml"

    // Dialect
    override protected val version: String     = "1.0"
    override protected lazy val rootId: String = Raml10DialectNodes.RootNode.id
    override protected lazy val dialectDeclares: Seq[NodeMapping] = Seq(
      Raml10DialectNodes.ExampleNode,
      Raml10DialectNodes.DataTypeNode,
      Raml10DialectNodes.DocumentationNode,
      Raml10DialectNodes.PayloadNode,
      Raml10DialectNodes.ResourceTypeNode,
      Raml10DialectNodes.TraitNode,
      Raml10DialectNodes.ResponseNode,
      Raml10DialectNodes.MethodNode,
      Raml10DialectNodes.ResourceNode,
      Raml10DialectNodes.RootNode,
      Raml10DialectNodes.XmlNode
    )
  }

  val DialectLocation = "file://parallel-als/vocabularies/dialects/raml10.yaml"

  // hack to force object initialization in amf and avoid exception
  private val orignalId = Raml10Dialect().id

  val ShapeNodeId: String = DialectLocation + "#/declarations/ShapeNode"
  val shapeTypesProperty: PropertyMapping = PropertyMapping()
    .withId(DialectLocation + "#/declarations/ShapeNode/inherits")
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
        "file",
        "object",
        "date-only",
        "time-only",
        "datetime-only",
        "datetime",
        "nil",
        "any"
      ))
    .withLiteralRange(xsdString.iri())
  val shapeProperties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/ShapeNode/in")
      .withNodePropertyMapping(ShapeModel.Values.value.iri())
      .withName("enum")
      .withObjectRange(Seq(ShapeNodeId))
      .withAllowMultiple(true),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/ShapeNode/default")
      .withNodePropertyMapping(ShapeModel.Default.value.iri())
      .withName("default")
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/ShapeNode/displayName")
      .withNodePropertyMapping(ShapeModel.DisplayName.value.iri())
      .withName("displayName")
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/ShapeNode/description")
      .withNodePropertyMapping(ShapeModel.Description.value.iri())
      .withName("description")
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/ShapeNode/facets")
      .withNodePropertyMapping(ShapeModel.CustomShapeProperties.value.iri())
      .withName("facets")
      .withObjectRange(Seq(CustomDomainPropertyModel.`type`.head.iri()))
      .withMapTermKeyProperty(CustomDomainPropertyModel.Name.value.iri()),
    shapeTypesProperty
  )

  val ShapeNode: NodeMapping = NodeMapping()
    .withId(ShapeNodeId)
    .withName("ShapeNode")
    .withNodeTypeMapping(ShapeModel.`type`.head.iri())
    .withPropertiesMapping(shapeProperties)

  val anyShapeProperties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/AnyShapeNode/xmlSerialization")
      .withNodePropertyMapping(AnyShapeModel.XMLSerialization.value.iri())
      .withName("xml")
      .withObjectRange(Seq(XMLSerializerModel.`type`.head.iri())),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/AnyShapeNode/example")
      .withNodePropertyMapping(AnyShapeModel.Examples.value.iri())
      .withName("example")
      .withObjectRange(Seq(ExampleNode.id)),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/AnyShapeNode/examples")
      .withNodePropertyMapping(AnyShapeModel.Examples.value.iri())
      .withName("examples")
      .withObjectRange(Seq(ExampleNode.id))
      .withMapTermKeyProperty(ExampleModel.DisplayName.value.iri())
  ) ++ shapeProperties

  val AnyShapeNode: NodeMapping = NodeMapping()
    .withId(DialectLocation + "#/declarations/AnyShapeNode")
    .withName("AnyShapeNode")
    .withNodeTypeMapping(AnyShapeModel.`type`.head.iri())
    .withPropertiesMapping(anyShapeProperties)

  val PropertyShapeNode: NodeMapping = NodeMapping()
    .withId(DialectLocation + "#/declarations/PropertyShapeNode")
    .withName("PropertyShapeNode")
    .withNodeTypeMapping(PropertyShapeModel.`type`.head.iri())
    .withPropertiesMapping(
      anyShapeProperties ++ Seq(
        PropertyMapping()
          .withId(DialectLocation + "#/declarations/PropertyShapeNode/required")
          .withNodePropertyMapping(PropertyShapeModel.MinCount.value.iri())
          .withName("required")
          .withLiteralRange(xsdBoolean.iri())
      ))

  val NodeShapeNode: NodeMapping = NodeMapping()
    .withId(DialectLocation + "#/declarations/NodeShapeNode")
    .withName("NodeShapeNode")
    .withNodeTypeMapping(NodeShapeModel.`type`.head.iri())
    .withPropertiesMapping(anyShapeProperties ++ Seq(
      PropertyMapping()
        .withId(DialectLocation + "#/declarations/NodeShapeNode/minProperties")
        .withNodePropertyMapping(NodeShapeModel.MinProperties.value.iri())
        .withName("minProperties")
        withLiteralRange xsdInteger.iri(),
      PropertyMapping()
        .withId(DialectLocation + "#/declarations/NodeShapeNode/maxProperties")
        .withNodePropertyMapping(NodeShapeModel.MaxProperties.value.iri())
        .withName("maxProperties")
        withLiteralRange xsdInteger.iri(),
      PropertyMapping()
        .withId(DialectLocation + "#/declarations/NodeShapeNode/additionalProperties")
        .withNodePropertyMapping(NodeShapeModel.Closed.value.iri())
        .withName("additionalProperties")
        withLiteralRange xsdBoolean.iri(),
      PropertyMapping()
        .withId(DialectLocation + "#/declarations/NodeShapeNode/discriminator")
        .withNodePropertyMapping(NodeShapeModel.Discriminator.value.iri())
        .withName("discriminator")
        withLiteralRange xsdString.iri(),
      PropertyMapping()
        .withId(DialectLocation + "#/declarations/NodeShapeNode/discriminatorValue")
        .withNodePropertyMapping(NodeShapeModel.DiscriminatorValue.value.iri())
        .withName("discriminatorValue")
        withLiteralRange xsdString.iri(),
      PropertyMapping()
        .withId(DialectLocation + "#/declarations/NodeShapeNode/properties")
        .withNodePropertyMapping(NodeShapeModel.Properties.value.iri())
        .withName("properties")
        .withObjectRange(Seq(PropertyShapeNode.id))
        .withMapTermKeyProperty(PropertyShapeModel.Name.value.iri())
    ))

  val ArrayShapeNode: NodeMapping = NodeMapping()
    .withId(DialectLocation + "#/declarations/ArrayShapeNode")
    .withName("ArrayShapeNode")
    .withNodeTypeMapping(ArrayShapeModel.`type`.head.iri())
    .withPropertiesMapping(anyShapeProperties ++ Seq(
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
        .withLiteralRange(xsdBoolean.iri())
    ))

  val UnionShapeNode: NodeMapping = NodeMapping()
    .withId(DialectLocation + "#/declarations/UnionShapeNode")
    .withName("UnionShapeNode")
    .withNodeTypeMapping(UnionShapeModel.`type`.head.iri())
    .withPropertiesMapping(anyShapeProperties)

  val ScalarShapeNode: NodeMapping = NodeMapping()
    .withId(DialectLocation + "#/declarations/ScalarShapeNode")
    .withName("ScalarShapeNode")
    .withNodeTypeMapping(ScalarShapeModel.`type`.head.iri())
    .withPropertiesMapping(anyShapeProperties)

  val StringShapeNode: NodeMapping = NodeMapping()
    .withId(DialectLocation + "#/declarations/StringShapeNode")
    .withName("StringShapeNode")
    .withNodeTypeMapping((Namespace.Shapes + "StringShape").iri())
    .withPropertiesMapping(anyShapeProperties ++ Seq(
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
    ))

  val NumberShapeNode: NodeMapping = NodeMapping()
    .withId(DialectLocation + "#/declarations/NumberShapeNode")
    .withName("NumberShapeNode")
    .withNodeTypeMapping((Namespace.Shapes + "NumberShape").iri())
    .withPropertiesMapping(anyShapeProperties ++ Seq(
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
        .withId(DialectLocation + "#/declarations/ScalarShapeNode/format")
        .withNodePropertyMapping(ScalarShapeModel.Format.value.iri())
        .withName("format")
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
        .withId(DialectLocation + "#/declarations/ScalarShapeNode/maximun")
        .withNodePropertyMapping(ScalarShapeModel.MultipleOf.value.iri())
        .withName("multipleOf")
        .withLiteralRange(xsdFloat.iri())
    ))

  val FileShapeNode: NodeMapping = NodeMapping()
    .withId(DialectLocation + "#/declarations/FileShapeNode")
    .withName("FileShapeNode")
    .withNodeTypeMapping(FileShapeModel.`type`.head.iri())
    .withPropertiesMapping(anyShapeProperties ++ Seq(
      PropertyMapping()
        .withId(DialectLocation + "#/declarations/ScalarShapeNode/fileTypes")
        .withNodePropertyMapping(FileShapeModel.FileTypes.value.iri())
        .withName("fileTypes")
        .withLiteralRange(xsdString.iri()),
      PropertyMapping()
        .withId(DialectLocation + "#/declarations/ScalarShapeNode/minLength")
        .withNodePropertyMapping(FileShapeModel.MinLength.value.iri())
        .withName("minLength")
        .withLiteralRange(xsdInteger.iri()),
      PropertyMapping()
        .withId(DialectLocation + "#/declarations/ScalarShapeNode/maxLength")
        .withNodePropertyMapping(FileShapeModel.MaxLength.value.iri())
        .withName("maxLength")
        .withLiteralRange(xsdInteger.iri())
    ))

  val NilShapeNode: NodeMapping = NodeMapping()
    .withId(DialectLocation + "#/declarations/NilShapeNode")
    .withName("NilShapeNode")
    .withNodeTypeMapping(NilShapeModel.`type`.head.iri())

  val AnnotationType: NodeMapping = NodeMapping()
    .withId(DialectLocation + "#/declarations/AnnotationType")
    .withName("AnnotationType")
    .withNodeTypeMapping(CustomDomainPropertyModel.`type`.head.iri())
    .withPropertiesMapping(
      Seq(
        PropertyMapping()
          .withId(DialectLocation + "#/declarations/AnnotationType/allowedTargets")
          .withNodePropertyMapping(CustomDomainPropertyModel.Domain.value.iri())
          .withName("allowedTargets")
          .withAllowMultiple(true)
          .withEnum(Seq(
            "API",
            "DocumentationItem",
            "Resource",
            "Method",
            "Response",
            "RequestBody",
            "ResponseBody",
            "TypeDeclaration",
            "Example",
            "ResourceType",
            "Trait",
            "SecurityScheme",
            "SecuritySchemeSettings",
            "AnnotationType",
            "Library",
            "Overlay",
            "Extension"
          ))
          .withLiteralRange(xsdString.iri())))

  val dialect: Dialect = {
    val dialect = Raml10Dialect()
    dialect.withDeclares(
      dialect.declares ++
        Seq(
          ShapeNode,
          AnyShapeNode,
          PropertyShapeNode,
          NodeShapeNode,
          ArrayShapeNode,
          UnionShapeNode,
          StringShapeNode,
          NumberShapeNode,
          FileShapeNode,
          NilShapeNode,
          ScalarShapeNode,
          Raml10SecuritySchemesDialect.SecurityScheme,
          Raml10SecuritySchemesDialect.OAuth1Settings,
          Raml10SecuritySchemesDialect.OAuth2Settings,
          Raml10SecuritySchemesDialect.OAuth2Flows,
          AnnotationType
        ))

    val declaredNodes = Seq(
      PublicNodeMapping()
        .withId(DialectLocation + "#/documents/types")
        .withName("types")
        .withMappedNode(ShapeNode.id),
      PublicNodeMapping()
        .withId(DialectLocation + "#/documents/resourceTypes")
        .withName("resourceTypes")
        .withMappedNode(Raml10DialectNodes.ResourceTypeNode.id),
      PublicNodeMapping()
        .withId(DialectLocation + "#/documents/traits")
        .withName("traits")
        .withMappedNode(Raml10DialectNodes.TraitNode.id),
      PublicNodeMapping()
        .withId(DialectLocation + "#/documents/securitySchemes")
        .withName("securitySchemes")
        .withMappedNode(Raml10SecuritySchemesDialect.SecurityScheme.id), // todo
      PublicNodeMapping()
        .withId(DialectLocation + "#/documents/annotationTypes")
        .withName("annotationTypes")
        .withMappedNode(AnnotationType.id) // todo
    )

    dialect.documents().root().withDeclaredNodes(declaredNodes)

    dialect
      .documents()
      .withLibrary(
        DocumentMapping()
          .withId(DialectLocation + "#/library")
          .withDeclaredNodes(declaredNodes))
    dialect
  }
  def apply(): Dialect = dialect
}
