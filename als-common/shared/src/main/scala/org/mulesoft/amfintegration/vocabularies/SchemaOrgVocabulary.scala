package org.mulesoft.amfintegration.vocabularies

import org.mulesoft.amfintegration.vocabularies.classterms.schemaorg.{
  CorrelationIdClassTerm,
  CreativeWorkClassTerm,
  LicenseClassTerm,
  OrganizationClassTerm
}
import org.mulesoft.amfintegration.vocabularies.propertyterms.schemaorg._

object SchemaOrgVocabulary extends VocabularyObject {

  override protected def base: String = "http://schema.org/#"

  override protected def classes: Seq[ClassTermObjectNode] =
    Seq(CorrelationIdClassTerm, CreativeWorkClassTerm, LicenseClassTerm, OrganizationClassTerm)

  override protected def properties: Seq[PropertyTermObjectNode] =
    Seq(
      CommentPropertyTerm,
      CorrelationIdPropertyTerm,
      DeprecatedPropertyTerm,
      DescriptionPropertyTerm,
      DisplayNamePropertyTerm,
      DocumentationPropertyTerm
    )
}
