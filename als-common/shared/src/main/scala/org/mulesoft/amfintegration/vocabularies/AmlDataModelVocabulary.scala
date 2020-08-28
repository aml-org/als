package org.mulesoft.amfintegration.vocabularies

import org.mulesoft.amfintegration.vocabularies.propertyterms.aml.{PropertiesPropertyTerm, ValuePropertyTerm}

object AmlDataModelVocabulary extends VocabularyObject {

  override protected def base: String = "http://a.ml/vocabularies/data#"

  override protected def classes: Seq[ClassTermObjectNode] =
    Seq()

  override protected def properties: Seq[PropertyTermObjectNode] =
    Seq(ValuePropertyTerm, PropertiesPropertyTerm)
}
