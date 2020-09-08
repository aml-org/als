package org.mulesoft.amfintegration.vocabularies

import org.mulesoft.amfintegration.vocabularies.propertyterms.declarationKeys._

object AlsDeclarationKeysVocabulary extends VocabularyObject {
  override protected def base: String = "http://als.declarationKeys/#"

  override protected def classes: Seq[ClassTermObjectNode] =
    Seq()

  override protected def properties: Seq[PropertyTermObjectNode] =
    Seq(
      MessageDeclarationKeyTerm,
      MessageAbstractDeclarationKeyTerm,
      OperationAbstractDeclarationKeyTerm,
      DomainPropertyDeclarationKeyTerm,
      ShapeDeclarationKeyTerm,
      SecuritySettingsDeclarationKeyTerm,
      TraitDeclarationKeyTerm,
      ResourceTypeDeclarationKeyTerm,
      SecuritySchemeDeclarationKeyTerm
    )
}
