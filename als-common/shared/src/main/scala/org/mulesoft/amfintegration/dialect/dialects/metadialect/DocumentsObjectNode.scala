package org.mulesoft.amfintegration.dialect.dialects.metadialect

import amf.dialects.oas.nodes.DialectNode
import amf.plugins.document.vocabularies.metamodel.domain.DocumentsModelModel
import amf.plugins.document.vocabularies.model.domain.PropertyMapping

object DocumentsObjectNode extends DialectNode {
  override def name: String = "DocumentsObjectNode"

  override def nodeTypeMapping: String = DocumentsModelModel.`type`.head.iri()

  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/root")
      .withNodePropertyMapping(DocumentsModelModel.Root.value.iri())
      .withName("root")
      .withObjectRange(Seq(RootDocumentObjectNode.id)),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/fragments")
      .withNodePropertyMapping(DocumentsModelModel.Fragments.value.iri())
      .withName("fragments")
      .withObjectRange(Seq(FragmentDocumentObjectNode.id)),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/library")
      .withNodePropertyMapping(DocumentsModelModel.Library.value.iri())
      .withName("library")
      .withObjectRange(Seq(LibraryDocumentObjectNode.id)),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/options")
      .withNodePropertyMapping("DocumentModelOptions.id")
      .withName("options")
      .withObjectRange(Seq(DocumentOptionsObjectNode.id))
  )
}
