package org.mulesoft.amfintegration.vocabularies.integration

import amf.aml.client.scala.model.document.Vocabulary
import org.mulesoft.amfintegration.vocabularies.{
  AlsDeclarationKeysVocabulary,
  AlsPatchedVocabulary,
  AmlApiContractVocabulary,
  AmlCoreVocabulary,
  AmlDataModelVocabulary,
  AmlDataShapesVocabulary,
  AmlDocumentVocabulary,
  SchemaOrgVocabulary,
  ShaclVocabulary
}

object DefaultVocabularies {
  val all: Seq[Vocabulary] = Seq(
    AlsDeclarationKeysVocabulary(),
    SchemaOrgVocabulary(),
    ShaclVocabulary(),
    AmlApiContractVocabulary(),
    AmlDataModelVocabulary(),
    AmlDataShapesVocabulary(),
    AmlCoreVocabulary(),
    AmlDocumentVocabulary(),
    AlsPatchedVocabulary()
  )

//  profile.vendors.foreach { v =>
//    alsAmlPlugin.registerWebApiDialect(v))
}
