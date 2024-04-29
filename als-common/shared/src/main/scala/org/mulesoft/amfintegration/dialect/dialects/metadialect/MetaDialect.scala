package org.mulesoft.amfintegration.dialect.dialects.metadialect

import amf.aml.client.scala.model.domain.{DocumentMapping, DocumentsModel}
import org.mulesoft.amfintegration.dialect.BaseDialect
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.DialectNode

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
    UnionMappingObjectNode,
    AnnotationMappingObjectNode
  )

  override protected def declaredNodes: Map[String, DialectNode] = Map(
    "nodeMappings"       -> NodeMappingObjectNode,
    "annotationMappings" -> AnnotationMappingObjectNode
  )
}
