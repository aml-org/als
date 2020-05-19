package org.mulesoft.amfintegration.dialect.dialects.metadialect

import amf.dialects.oas.nodes.DialectNode
import amf.plugins.document.vocabularies.model.domain.{DocumentMapping, DocumentsModel}
import org.mulesoft.amfintegration.dialect.BaseDialect

object MetaDialect extends BaseDialect {

  override def DialectLocation: String = "file://vocabularies/dialects/metadialect.yaml"

  override protected val name: String    = "Dialect"
  override protected val version: String = "1.0"

  override protected def emptyDocument: DocumentsModel =
    DocumentsModel()
      .withId(DialectLocation + "#/documents")
      .withLibrary(DocumentMapping())

  override protected def encodes: DialectNode = DialectRootEncodedNode

  override val declares: Seq[DialectNode] = Seq(
    DialectRootEncodedNode,
    DocumentOptionsObjectNode,
    DocumentsObjectNode,
    ExternalObjectNode,
    FragmentDocumentObjectNode,
    LibraryDocumentObjectNode,
    NodeMappingObjectNode,
    PropertyMappingObjectNode,
    PublicNodeMappingObjectNode,
    RootDocumentObjectNode,
    UnionMappingObjectNode
  )

  override protected def declaredNodes: Map[String, DialectNode] = Map(
    "nodeMappings" -> NodeMappingObjectNode
  )
}
