package org.mulesoft.amfintegration.vocabularies

import org.mulesoft.amfintegration.vocabularies.propertyterms.aml.DocumentPropertyTerm

object AmlDocumentVocabulary extends VocabularyObject {

  override protected def base: String = "http://a.ml/vocabularies/document#"

  override protected def classes: Seq[ClassTermObjectNode] =
    Seq()

  override protected def properties: Seq[PropertyTermObjectNode] =
    Seq(DocumentPropertyTerm)
}