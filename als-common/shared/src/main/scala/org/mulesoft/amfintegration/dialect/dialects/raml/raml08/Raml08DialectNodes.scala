package org.mulesoft.amfintegration.dialect.dialects.raml.raml08

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.apicontract.internal.metamodel.domain.{EndPointModel, ParameterModel}
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.xsdBoolean
import amf.core.internal.metamodel.domain.extensions.PropertyShapeModel
import org.mulesoft.amfintegration.dialect.dialects.raml.RamlDialectNodes

object Raml08DialectNodes extends RamlDialectNodes {
  override protected def dialectLocation: String = Raml08TypesDialect.DialectLocation

  protected def extendedShapeProperties(nodeId: String): Seq[PropertyMapping] =
    Seq(
      PropertyMapping()
        .withId(dialectLocation + s"#/declarations/$nodeId/DataTypeNode/repeat")
        .withName("repeat")
        .withNodePropertyMapping(PropertyShapeModel.MaxCount.value.iri())
        .withLiteralRange(xsdBoolean.iri()),
      PropertyMapping()
        .withId(dialectLocation + s"#/declarations/$nodeId/DataTypeNode/required")
        .withName("required")
        .withNodePropertyMapping(PropertyShapeModel.MinCount.value.iri())
        .withLiteralRange(xsdBoolean.iri())
    )

  override def commonShapeProperties(nodeId: String): Seq[PropertyMapping] =
    super.commonShapeProperties(nodeId) ++ extendedShapeProperties(nodeId)

  private val extendedResourceNodeMappings: Seq[PropertyMapping] = innerResourceNodeMappings ++ Seq(
    PropertyMapping()
      .withId(dialectLocation + "#/declarations/ResourceNode/baseUriParameters")
      .withName("baseUriParameters")
      .withNodePropertyMapping(EndPointModel.Parameters.value.iri())
      .withMapTermKeyProperty(ParameterModel.Name.value.iri())
      .withObjectRange(Seq(
        DataTypeNodeId
      )))
  override protected def resourceNodeMappings: Seq[PropertyMapping] = extendedResourceNodeMappings
  override protected def methodNodeMappings: Seq[PropertyMapping]   = innerMethodNodeMappings
  override protected def rootMappings: Seq[PropertyMapping]         = innerRootMappings
  override protected val implicitField: String                      = Raml08TypesDialect.ImplicitField
}
