package org.mulesoft.amfintegration.dialect.dialects.metadialect

import amf.plugins.document.vocabularies.metamodel.domain.UnionNodeMappingModel
import amf.plugins.document.vocabularies.model.domain.PropertyMapping
import amf.core.vocabulary.Namespace.XsdTypes._
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.DialectNode
object UnionMappingObjectNode extends DialectNode {
  override def name: String = "UnionMappingObjectNode"

  override def nodeTypeMapping: String = UnionNodeMappingModel.`type`.head.iri()

  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/union")
      .withNodePropertyMapping(UnionNodeMappingModel.ObjectRange.value.iri())
      .withName("union")
      .withLiteralRange(xsdString.iri())
      .withMinCount(1)
      .withAllowMultiple(true),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/typeDiscriminator")
      .withNodePropertyMapping(UnionNodeMappingModel.TypeDiscriminator.value.iri())
      .withName("typeDiscriminator")
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/typeDiscriminatorName")
      .withNodePropertyMapping(UnionNodeMappingModel.TypeDiscriminatorName.value.iri())
      .withName("typeDiscriminatorName")
      .withLiteralRange(xsdString.iri())
  )
}
