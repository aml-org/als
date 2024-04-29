package org.mulesoft.amfintegration.vocabularies

import org.mulesoft.amfintegration.vocabularies.propertyterms.aml.{
  AnyOfPropertyTerm,
  DiscriminatorPropertyTerm,
  FileTypePropertyTerm,
  FormatPropertyTerm,
  InheritsPropertyTerm,
  ItemsPropertyTerm
}

object AmlDataShapesVocabulary extends VocabularyObject {

  override protected def base: String = "http://a.ml/vocabularies/shapes#"

  override protected def classes: Seq[ClassTermObjectNode] =
    Seq()

  override protected def properties: Seq[PropertyTermObjectNode] =
    Seq(
      AnyOfPropertyTerm,
      DiscriminatorPropertyTerm,
      FileTypePropertyTerm,
      FormatPropertyTerm,
      InheritsPropertyTerm,
      ItemsPropertyTerm
    )
}
