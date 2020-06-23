package org.mulesoft.amfintegration.vocabularies

import amf.plugins.document.vocabularies.model.document.Vocabulary
import org.mulesoft.amfintegration.vocabularies.classterms.{
  CorrelationIdClassTerm,
  CreativeWorkClassTerm,
  LicenseClassTerm,
  OrganizationClassTerm
}
import org.mulesoft.amfintegration.vocabularies.propertyterms.{
  CommentPropertyTerm,
  CorrelationIdPropertyTerm,
  DeprecatedPropertyTerm,
  DescriptionPropertyTerm,
  DisplayNamePropertyTerm,
  DocumentationPropertyTerm
}

object SchemaOrgVocabulary {

  private val base = "http://schema.org/#"
  def apply(): Vocabulary =
    Vocabulary()
      .withId(base)
      .withBase(base)
      .withDeclares(Seq(
        CorrelationIdClassTerm.obj,
        CreativeWorkClassTerm.obj,
        LicenseClassTerm.obj,
        OrganizationClassTerm.obj,
        CommentPropertyTerm.obj,
        CorrelationIdPropertyTerm.obj,
        DeprecatedPropertyTerm.obj,
        DescriptionPropertyTerm.obj,
        DisplayNamePropertyTerm.obj,
        DocumentationPropertyTerm.obj
      ))
}
