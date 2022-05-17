package org.mulesoft.amfintegration.dialect.dialects.jsonschema.draft7.base

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.shapes.internal.domain.metamodel.{AnyShapeModel, ExampleModel}
import org.mulesoft.amfintegration.dialect.dialects.raml.raml10.Raml10DialectNodes.ExampleNode

trait BaseAnyShapeNode extends BaseShapeNode {

  override def properties: Seq[PropertyMapping] = super.properties ++ Seq(
    PropertyMapping()
      .withId(location + "#/declarations/AnyShapeNode/examples")
      .withNodePropertyMapping(AnyShapeModel.Examples.value.iri())
      .withName("examples")
      .withObjectRange(Seq(ExampleNode.id))
      .withMapTermKeyProperty(ExampleModel.Name.value.iri())
  )
  override def nodeTypeMapping: String = AnyShapeModel.`type`.head.iri()
  override def name                    = "AnyShape"
}
