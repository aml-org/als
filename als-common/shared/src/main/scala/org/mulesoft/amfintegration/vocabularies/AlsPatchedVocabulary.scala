package org.mulesoft.amfintegration.vocabularies

import org.mulesoft.amfintegration.vocabularies.propertyterms.patched.oas2.Oas2InfoKeyTerm

object AlsPatchedVocabulary extends VocabularyObject {
  override protected def base: String = "http://als.patched/#"

  override protected def classes: Seq[ClassTermObjectNode] =
    Seq()

  override protected def properties: Seq[PropertyTermObjectNode] =
    Seq(
      Oas2InfoKeyTerm
    )
}
