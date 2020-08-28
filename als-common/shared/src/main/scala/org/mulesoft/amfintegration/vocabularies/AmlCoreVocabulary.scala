package org.mulesoft.amfintegration.vocabularies

import org.mulesoft.amfintegration.vocabularies.propertyterms.NamePropertyTerm

object AmlCoreVocabulary extends VocabularyObject {

  override protected def base: String = "http://a.ml/vocabularies/core#"

  override protected def classes: Seq[ClassTermObjectNode] =
    Seq()

  override protected def properties: Seq[PropertyTermObjectNode] =
    Seq(NamePropertyTerm)
}
