package org.mulesoft.amfintegration.dialect.dialects.metadialect

import amf.core.vocabulary.Namespace.XsdTypes.xsdString
import amf.plugins.document.vocabularies.metamodel.domain.{ClassTermModel, DatatypePropertyTermModel}
import amf.plugins.document.vocabularies.model.domain.PropertyMapping
import org.mulesoft.amfintegration.dialect.dialects.metadialect.PropertyTermObjectNode.{location, name}

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
