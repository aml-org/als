package org.mulesoft.amfintegration.dialect.dialects.metadialect

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.aml.internal.metamodel.domain.{NodeMappingModel, PropertyMappingModel}
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.{xsdString, xsdUri}
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.DialectNode

object NodeMappingObjectNode extends DialectNode {
  override def name: String = "NodeMappingObjectNode"

  override def nodeTypeMapping: String = NodeMappingModel.`type`.head.iri()

  override def properties: Seq[PropertyMapping] =
    Seq(
      PropertyMapping()
        .withId(location + s"#/declarations/$name/classTerm")
        .withNodePropertyMapping(NodeMappingModel.NodeTypeMapping.value.iri())
        .withName("classTerm")
        .withMinCount(1)
        .withLiteralRange(xsdString.iri()),
      PropertyMapping()
        .withId(location + s"#/declarations/$name/patch")
        .withNodePropertyMapping(NodeMappingModel.MergePolicy.value.iri())
        .withName("patch")
        .withLiteralRange(xsdString.iri())
        .withEnum(
          Seq(
            "insert",
            "delete",
            "update",
            "upsert",
            "ignore",
            "fail"
          )),
      PropertyMapping()
        .withId(location + s"#/declarations/$name/mappings")
        .withNodePropertyMapping(NodeMappingModel.PropertiesMapping.value.iri())
        .withName("mapping")
        .withObjectRange(Seq(PropertyMappingObjectNode.id))
        .withMapTermKeyProperty(PropertyMappingModel.Name.value.iri()),
      PropertyMapping()
        .withId(location + s"#/declarations/$name/extends")
        .withNodePropertyMapping(NodeMappingModel.Extends.value.iri())
        .withName("extends")
        .withLiteralRange(xsdUri.iri()),
      PropertyMapping()
        .withId(location + s"#/declarations/$name/idTemplate")
        .withNodePropertyMapping(NodeMappingModel.IdTemplate.value.iri())
        .withName("idTemplate")
        .withLiteralRange(xsdUri.iri())
    )
}
