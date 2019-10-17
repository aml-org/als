package org.mulesoft.amfmanager.dialect

import amf.core.model.document.BaseUnit
import amf.core.remote.{Oas20, Raml08, Raml10}
import amf.dialects.{OAS20Dialect, RAML08Dialect, RAML10Dialect, WebApiDialectsRegistry}
import amf.plugins.document.vocabularies.ReferenceStyles
import amf.plugins.document.vocabularies.model.document.{Dialect, DialectInstanceUnit}
import org.mulesoft.als.common.YPartBranch
import org.mulesoft.amfmanager.dialect.webapi.oas.Oas20DialectWrapper
import org.mulesoft.amfmanager.dialect.webapi.raml.raml08.Raml08TypesDialect
import org.mulesoft.amfmanager.dialect.webapi.raml.raml10.Raml10TypesDialect

trait DialectKnowledge {
  def dialectFor(bu: BaseUnit): Option[Dialect] = bu match {
    case _: DialectInstanceUnit => WebApiDialectsRegistry.dialectFor(bu)
    case d if d.sourceVendor.contains(Oas20) && !OAS20Dialect().id.isEmpty =>
      Some(Oas20DialectWrapper.dialect)
    case d if d.sourceVendor.contains(Raml10) && !RAML10Dialect().id.isEmpty =>
      Some(Raml10TypesDialect.dialect)
    case d if d.sourceVendor.contains(Raml08) && !RAML08Dialect().id.isEmpty =>
      Some(Raml08TypesDialect.dialect)
    case _ => None
  }

  def isStyleValue(style: String, dialect: Dialect): Boolean =
    Option(dialect.documents()).forall(d => d.referenceStyle().is(style) || d.referenceStyle().isNullOrEmpty)

  def isRamlInclusion(yPartBranch: YPartBranch, dialect: Dialect): Boolean =
    yPartBranch.hasIncludeTag && isStyleValue(ReferenceStyles.RAML, dialect)

  def isJsonInclusion(yPartBranch: YPartBranch, dialect: Dialect): Boolean =
    yPartBranch.isValue && isStyleValue(ReferenceStyles.JSONSCHEMA, dialect) &&
      yPartBranch.parentEntry.exists(p => p.key.asScalar.exists(_.text == "$ref"))

  def isInclusion(yPartBranch: YPartBranch, dialect: Dialect): Boolean =
    isRamlInclusion(yPartBranch, dialect) || isJsonInclusion(yPartBranch, dialect)
}
