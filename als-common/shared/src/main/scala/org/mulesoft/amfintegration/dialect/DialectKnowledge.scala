package org.mulesoft.amfintegration.dialect

import amf.aml.client.scala.model.document.Dialect
import amf.core.internal.remote.Spec
import amf.plugins.document.vocabularies.plugin.ReferenceStyles
import org.mulesoft.als.common.YPartBranch

object DialectKnowledge {

  def isStyleValue(style: String, dialect: Dialect): Boolean =
    Option(dialect.documents()).forall(d => d.referenceStyle().is(style) || d.referenceStyle().isNullOrEmpty)

  def isRamlInclusion(yPartBranch: YPartBranch, dialect: Dialect): Boolean =
    yPartBranch.hasIncludeTag && isStyleValue(ReferenceStyles.RAML, dialect)

  def isJsonInclusion(yPartBranch: YPartBranch, dialect: Dialect): Boolean =
    yPartBranch.isValue && isStyleValue(ReferenceStyles.JSONSCHEMA, dialect) &&
      yPartBranch.parentEntry.exists(p => p.key.asScalar.exists(_.text == "$ref"))

  def isInclusion(yPartBranch: YPartBranch, dialect: Dialect): Boolean =
    isRamlInclusion(yPartBranch, dialect) || isJsonInclusion(yPartBranch, dialect)

  def isPathFacetSupportedByVersion(spec: Spec): Boolean =
    !Spec.RAML08.equals(spec)
}
