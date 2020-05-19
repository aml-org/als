package org.mulesoft.amfintegration.dialect.dialects.metadialect

import amf.core.vocabulary.Namespace.XsdTypes.xsdUri
import amf.plugins.document.vocabularies.metamodel.domain.{DocumentMappingModel, PublicNodeMappingModel}
import amf.plugins.document.vocabularies.model.domain.PropertyMapping
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.DialectNode

trait DocumentMappingObjectNode extends DialectNode {
  override def nodeTypeMapping: String = DocumentMappingModel.`type`.head.iri()

  override def properties: Seq[PropertyMapping] = Nil
}

trait EncodesDocumentObjectNode extends DocumentMappingObjectNode {

  override def properties: Seq[PropertyMapping] =
    super.properties :+ PropertyMapping()
      .withId(location + s"#/declarations/$name/encodes")
      .withNodePropertyMapping(DocumentMappingModel.EncodedNode.value.iri())
      .withName("encodes")
      .withLiteralRange(xsdUri.iri())
}

trait DeclaresDocumentObjectNode extends DocumentMappingObjectNode {

  override def properties: Seq[PropertyMapping] =
    super.properties :+ PropertyMapping()
      .withId(location + s"#/declarations/$name/declares")
      .withNodePropertyMapping(DocumentMappingModel.DeclaredNodes.value.iri())
      .withName("declares")
      .withMapTermKeyProperty(PublicNodeMappingModel.Name.value.iri())
      .withMapTermValueProperty(PublicNodeMappingModel.MappedNode.value.iri())
      .withObjectRange(Seq(PublicNodeMappingObjectNode.id))
}
