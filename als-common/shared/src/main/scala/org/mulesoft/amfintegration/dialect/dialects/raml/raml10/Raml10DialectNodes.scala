package org.mulesoft.amfintegration.dialect.dialects.raml.raml10

import amf.aml.client.scala.model.domain.{NodeMapping, PropertyMapping}
import amf.apicontract.internal.metamodel.domain.api.WebApiModel
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.{amlAnyNode, amlLink, xsdBoolean, xsdString}
import amf.core.internal.metamodel.domain.ShapeModel
import amf.core.internal.metamodel.domain.extensions.PropertyShapeModel
import amf.shapes.internal.domain.metamodel.{AnyShapeModel, ArrayShapeModel, XMLSerializerModel}
import org.mulesoft.amfintegration.dialect.dialects.raml.RamlDialectNodes

object Raml10DialectNodes extends RamlDialectNodes {
  override protected def dialectLocation: String = Raml10TypesDialect.DialectLocation

  lazy val XmlNode: NodeMapping = NodeMapping()
    .withId(dialectLocation + "#/declarations/XmlNode")
    .withName("XmlNode")
    .withNodeTypeMapping(XMLSerializerModel.`type`.head.iri())
    .withPropertiesMapping(Seq(
      PropertyMapping()
        .withId(dialectLocation + "#/declarations/XmlNode/name")
        .withNodePropertyMapping(XMLSerializerModel.Name.value.iri())
        .withName("name")
        .withLiteralRange(xsdString.iri()),
      PropertyMapping()
        .withId(dialectLocation + "#/declarations/XmlNode/namespace")
        .withNodePropertyMapping(XMLSerializerModel.Namespace.value.iri())
        .withName("namespace")
        .withLiteralRange(amlLink.iri()),
      PropertyMapping()
        .withId(dialectLocation + "#/declarations/XmlNode/prefix")
        .withNodePropertyMapping(XMLSerializerModel.Prefix.value.iri())
        .withName("prefix")
        .withLiteralRange(xsdString.iri()),
      PropertyMapping()
        .withId(dialectLocation + "#/declarations/XmlNode/attribute")
        .withNodePropertyMapping(XMLSerializerModel.Attribute.value.iri())
        .withName("attribute")
        .withLiteralRange(xsdBoolean.iri()),
      PropertyMapping()
        .withId(dialectLocation + "#/declarations/XmlNode/wrapped")
        .withNodePropertyMapping(XMLSerializerModel.Wrapped.value.iri())
        .withName("wrapped")
        .withLiteralRange(xsdBoolean.iri())
    ))

  override protected def scalarTypes: Seq[String] = super.scalarTypes ++ Seq(
    "float",
    "array",
    "object",
    "date-only",
    "time-only",
    "datetime-only",
    "datetime",
    "nil"
  )
  protected def extendedShapeProperties(nodeId: String): Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(dialectLocation + s"#/declarations/$nodeId/DataType/examples")
      .withName("examples")
      .withNodePropertyMapping(AnyShapeModel.Examples.value.iri())
      .withObjectRange(
        Seq(
          Raml10DialectNodes.ExampleNode.id
        )),
    PropertyMapping()
      .withId(dialectLocation + s"#/declarations/$nodeId/DataType/facets")
      .withName("facets")
      .withNodePropertyMapping(AnyShapeModel.CustomShapePropertyDefinitions.value.iri())
      .withLiteralRange(amlAnyNode.iri()),
    PropertyMapping()
      .withId(dialectLocation + s"#/declarations/$nodeId/DataType/xml")
      .withName("xml")
      .withNodePropertyMapping(AnyShapeModel.XMLSerialization.value.iri())
      .withObjectRange(
        Seq(
          XmlNode.id
        )),
    // Array type
    PropertyMapping()
      .withId(dialectLocation + s"#/declarations/$nodeId/ArrayTypeNode/items")
      .withName("items")
      .withNodePropertyMapping(ArrayShapeModel.Items.value.iri())
      .withAllowMultiple(true)
      .withObjectRange(Seq(
        Raml10DialectNodes.DataTypeNodeId
      )),
    PropertyMapping()
      .withId(dialectLocation + s"#/declarations/$nodeId/DataTypeNode/required")
      .withName("required")
      .withNodePropertyMapping(PropertyShapeModel.MinCount.value.iri())
      .withLiteralRange(xsdBoolean.iri()),
  )

  override def commonShapeProperties(nodeId: String): Seq[PropertyMapping] =
    super.commonShapeProperties(nodeId) ++ extendedShapeProperties(nodeId)

  private lazy val extendedMethodNodeMappings = innerMethodNodeMappings ++ Seq(
    PropertyMapping()
      .withId(dialectLocation + "#/declarations/MethodNode/Request/queryString")
      .withName("queryString")
      .withNodePropertyMapping(PropertyShapeModel.`type`.head.iri())
      .withMapTermKeyProperty(ShapeModel.Name.value.iri())
      .withObjectRange(Seq(
        DataTypeNodeId
      )))

  override protected def methodNodeMappings: Seq[PropertyMapping] =
    extendedMethodNodeMappings

  override protected def rootMappings: Seq[PropertyMapping] = extendedRootMappings
  private val extendedRootMappings = {
    Seq(
      PropertyMapping()
        .withId(dialectLocation + "#/declarations/RootNode/description")
        .withName("description")
        .withNodePropertyMapping(WebApiModel.Description.value.iri())
        .withLiteralRange(xsdString.iri())) ++ innerRootMappings
  }

  override protected val resourceNodeMappings: Seq[PropertyMapping] = innerResourceNodeMappings
  override protected val implicitField: String                      = Raml10TypesDialect.ImplicitField
}
