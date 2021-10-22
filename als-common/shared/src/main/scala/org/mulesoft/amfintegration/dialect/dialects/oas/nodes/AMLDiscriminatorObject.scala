package org.mulesoft.amfintegration.dialect.dialects.oas.nodes

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.xsdString
import amf.shapes.internal.domain.metamodel.NodeShapeModel
import org.mulesoft.amfintegration.dialect.dialects.oas.OAS30Dialect.DialectLocation

object AMLDiscriminatorObject extends DialectNode {
  override def name: String = "DiscriminatorObject"

  override def nodeTypeMapping: String = "DiscriminatorObject.id"

  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(DialectLocation + s"#/declarations/DiscriminatorObject/propertyName")
      .withNodePropertyMapping(NodeShapeModel.Discriminator.value.iri())
      .withName("propertyName")
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(DialectLocation + s"#/declarations/DiscriminatorObject/mapping")
      .withName("mapping")
      .withLiteralRange(xsdString.iri())
  )
}
