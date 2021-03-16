package org.mulesoft.amfintegration.dialect.dialects.metadialect

import amf.plugins.document.vocabularies.model.domain.{DocumentMapping, DocumentsModel}
import org.mulesoft.amfintegration.dialect.BaseDialect
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.DialectNode

object VocabularyDialect extends BaseDialect {
  override protected val name: String    = "Vocabulary"
  override protected val version: String = "1.0"

  override def DialectLocation: String = "file://vocabularies/dialects/vocabulary.yaml"

  override protected def emptyDocument: DocumentsModel =
    DocumentsModel()
      .withId(DialectLocation + "#/documents")
      .withLibrary(DocumentMapping())

  override protected def encodes: DialectNode = RootVocabularyObjectNode

  override val declares: Seq[DialectNode] = Seq(
    RootVocabularyObjectNode,
    ExternalObjectNode,
    PropertyTermObjectNode,
    ClassTermObjectNode
  )

  override protected def declaredNodes: Map[String, DialectNode] = Map(
    "propertyTerms" -> PropertyTermObjectNode,
    "classTerms"    -> ClassTermObjectNode
  )
}
