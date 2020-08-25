package org.mulesoft.amfintegration.vocabularies

import org.mulesoft.amfintegration.vocabularies.propertyterms.aml.ExamplesPropertyTerm

object AmlApiContractVocabulary extends VocabularyObject {

  override protected def base: String = "http://a.ml/vocabularies/apiContract#"

  override protected def classes: Seq[ClassTermObjectNode] =
    Seq()

  override protected def properties: Seq[PropertyTermObjectNode] =
    Seq(ExamplesPropertyTerm)
}
