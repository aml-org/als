package org.mulesoft.amfintegration.dialect

import amf.plugins.document.vocabularies.plugin.ReferenceStyles
import org.mulesoft.als.common.{ASTPartBranch, YPartBranch}
import org.mulesoft.amfintegration.amfconfiguration.DocumentDefinition

object DialectKnowledge {

  def isStyleValue(style: String, documentDefinition: DocumentDefinition): Boolean =
    documentDefinition.documents().forall(d => d.referenceStyle().is(style) || d.referenceStyle().isNullOrEmpty)

  def isRamlInclusion(yPartBranch: YPartBranch, documentDefinition: DocumentDefinition): Boolean =
    yPartBranch.hasIncludeTag && isStyleValue(ReferenceStyles.RAML, documentDefinition)

  def isJsonInclusion(yPartBranch: YPartBranch, documentDefinition: DocumentDefinition): Boolean =
    yPartBranch.isValue && isStyleValue(ReferenceStyles.JSONSCHEMA, documentDefinition) &&
      yPartBranch.parentEntry.exists(p => p.key.asScalar.exists(_.text == "$ref"))

  def isInclusion(yPartBranch: YPartBranch, documentDefinition: DocumentDefinition): Boolean =
    isRamlInclusion(yPartBranch, documentDefinition) || isJsonInclusion(yPartBranch, documentDefinition)

  def isInclusion(astPartBranch: ASTPartBranch, documentDefinition: DocumentDefinition): Boolean = astPartBranch match {
    case yPart: YPartBranch => isInclusion(yPart, documentDefinition)
    case _                  => false
  }

}
