package org.mulesoft.amfintegration.vocabularies

import org.mulesoft.amfintegration.vocabularies.propertyterms.NamePropertyTerm
import org.mulesoft.amfintegration.vocabularies.propertyterms.shacl.{
  ShaclDefaultValuePropertyTerm,
  ShaclElsePropertyTerm,
  ShaclIfPropertyTerm,
  ShaclInPropertyTerm,
  ShaclNotPropertyTerm,
  ShaclOrPropertyTerm,
  ShaclPathPropertyTerm,
  ShaclShapePropertyTerm
}

object ShaclVocabulary extends VocabularyObject {
  override protected def base: String = "http://www.w3.org/ns/shacl#"

  override protected def classes: Seq[ClassTermObjectNode] = Nil

  override protected def properties: Seq[PropertyTermObjectNode] =
    Seq(
      NamePropertyTerm,
      ShaclShapePropertyTerm,
      ShaclDefaultValuePropertyTerm,
      ShaclElsePropertyTerm,
      ShaclIfPropertyTerm,
      ShaclInPropertyTerm,
      ShaclNotPropertyTerm,
      ShaclOrPropertyTerm,
      ShaclPathPropertyTerm
    )
}
