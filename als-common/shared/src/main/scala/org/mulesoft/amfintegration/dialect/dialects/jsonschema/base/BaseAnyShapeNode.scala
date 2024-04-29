package org.mulesoft.amfintegration.dialect.dialects.jsonschema.base

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.xsdString
import amf.shapes.internal.domain.metamodel.{AnyShapeModel, ExampleModel, NodeShapeModel}
import org.mulesoft.amfintegration.dialect.dialects.jsonschema.base.BaseAnyShapeNode.anyShapeFacets
import org.mulesoft.amfintegration.dialect.dialects.raml.raml10.Raml10DialectNodes.ExampleNode

trait BaseAnyShapeNode extends BaseShapeNode {

  override def properties: Seq[PropertyMapping] = super.properties ++ anyShapeFacets(location)

  override def nodeTypeMapping: String = AnyShapeModel.`type`.head.iri()
  override def name                    = "AnyShape"
}
object BaseAnyShapeNode {
  def anyShapeFacets(location: String): Seq[PropertyMapping] =
    Seq(
      PropertyMapping()
        .withId(location + "#/declarations/AnyShapeNode/examples")
        .withNodePropertyMapping(AnyShapeModel.Examples.value.iri())
        .withName("examples")
        .withObjectRange(Seq(ExampleNode.id))
        .withMapTermKeyProperty(ExampleModel.Name.value.iri())
    )
}
