package org.mulesoft.amfintegration.vocabularies

import org.mulesoft.amfintegration.vocabularies.propertyterms.shacl.ShaclNamePropertyTerm

object ShaclVocabulary extends VocabularyObject {
  override protected def base: String = "http://www.w3.org/ns/shacl#"

  override protected def classes: Seq[ClassTermObjectNode] = Nil

  override protected def properties: Seq[PropertyTermObjectNode] = Seq(ShaclNamePropertyTerm)
}
