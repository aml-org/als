package org.mulesoft.amfintegration.dialect.dialects.metadialect

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.aml.internal.metamodel.domain.ClassTermModel
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.xsdString

object ClassTermObjectNode extends TermObjectNode {

  override def name: String = "ClassTermObjectNode"

  override def nodeTypeMapping: String =
    ClassTermModel.`type`.head.iri()

  override def properties: Seq[PropertyMapping] = super.properties ++ Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/properties")
      .withNodePropertyMapping(ClassTermModel.Properties.value.iri())
      .withName("properties")
      .withAllowMultiple(true)
      .withLiteralRange(xsdString.iri())
  )
}
