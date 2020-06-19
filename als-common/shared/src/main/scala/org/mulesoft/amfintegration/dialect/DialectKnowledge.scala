package org.mulesoft.amfintegration.dialect

import amf.core.model.document.BaseUnit
import amf.core.remote._
import amf.plugins.document.vocabularies.plugin.ReferenceStyles
import amf.plugins.document.vocabularies.model.document.{Dialect, DialectInstanceUnit}
import org.mulesoft.als.common.YPartBranch
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.AsyncApi20Dialect
import org.mulesoft.amfintegration.dialect.dialects.oas.{OAS20Dialect, OAS30Dialect}
import org.mulesoft.amfintegration.dialect.dialects.raml.raml08.{Raml08Dialect, Raml08TypesDialect}
import org.mulesoft.amfintegration.dialect.dialects.raml.raml10.{Raml10Dialect, Raml10TypesDialect}
import org.mulesoft.amfintegration.dialect.dialects.metadialect.MetaDialect

object DialectKnowledge {
  def dialectFor(bu: BaseUnit): Option[Dialect] = bu match {
    case _: Dialect             => Some(MetaDialect.dialect)
    case _: DialectInstanceUnit => WebApiDialectsRegistry.dialectFor(bu)
    case d if d.sourceVendor.contains(Oas20) && !OAS20Dialect().id.isEmpty =>
      Some(OAS20Dialect.dialect)
    case d if d.sourceVendor.contains(Oas30) && !OAS30Dialect().id.isEmpty =>
      Some(OAS30Dialect.dialect)
    case d if d.sourceVendor.contains(Raml10) && !Raml10Dialect().id.isEmpty =>
      Some(Raml10TypesDialect.dialect)
    case d if d.sourceVendor.contains(Raml08) && !Raml08Dialect().id.isEmpty =>
      Some(Raml08TypesDialect.dialect)
    case d if d.sourceVendor.contains(AsyncApi20) =>
      Some(AsyncApi20Dialect.dialect)
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

  def appliesReference(bu: BaseUnit, yPartBranch: YPartBranch): Boolean =
    dialectFor(bu).exists(dialect => isInclusion(yPartBranch, dialect))

}
