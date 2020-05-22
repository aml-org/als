package org.mulesoft.amfintegration.dialect.dialects.jsonschema.draft7.base

import amf.dialects.RAML10Dialect.DialectNodes.ExampleNode
import amf.plugins.document.vocabularies.model.domain.PropertyMapping
import amf.plugins.domain.shapes.metamodel.{AnyShapeModel, ExampleModel}

trait BaseAnyShapeNode extends BaseShapeNode {

  override def properties: Seq[PropertyMapping] = super.properties ++ Seq(
    PropertyMapping()
      .withId(location + "#/declarations/AnyShapeNode/examples")
      .withNodePropertyMapping(AnyShapeModel.Examples.value.iri())
      .withName("examples")
      .withObjectRange(Seq(ExampleNode.id))
      .withMapTermKeyProperty(ExampleModel.Name.value.iri()),
  )
  override def nodeTypeMapping: String = AnyShapeModel.`type`.head.iri()
  override def name = "AnyShape"
}
