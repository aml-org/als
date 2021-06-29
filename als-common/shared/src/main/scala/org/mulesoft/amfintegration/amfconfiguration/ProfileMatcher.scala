package org.mulesoft.amfintegration.amfconfiguration

import amf.aml.client.scala.model.document.Dialect
import amf.core.client.common.validation.{ProfileName, ProfileNames}
import amf.core.client.scala.model.document.BaseUnit
import amf.core.internal.remote.Spec
import org.mulesoft.amfintegration.DialectWithVendor
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.AsyncApi20Dialect
import org.mulesoft.amfintegration.dialect.dialects.metadialect.MetaDialect
import org.mulesoft.amfintegration.dialect.dialects.oas.{OAS20Dialect, OAS30Dialect}
import org.mulesoft.amfintegration.dialect.dialects.raml.raml08.Raml08TypesDialect
import org.mulesoft.amfintegration.dialect.dialects.raml.raml10.Raml10TypesDialect

object ProfileMatcher {
  def profile(model: BaseUnit): ProfileName = model.sourceSpec.map(profile).getOrElse(ProfileNames.AMF)

  def profile(spec: Spec): ProfileName =
    spec match {
      case Spec.RAML10  => ProfileNames.RAML10
      case Spec.RAML08  => ProfileNames.RAML08
      case Spec.OAS20   => ProfileNames.OAS20
      case Spec.OAS30   => ProfileNames.OAS30
      case Spec.ASYNC20 => ProfileNames.ASYNC20
      case Spec.AML     => ProfileNames.AML
      case _            => ProfileNames.AMF
    }

  private val webApiDialects: Set[DialectWithVendor] = Set(
    DialectWithVendor(Raml08TypesDialect(), Spec.RAML08),
    DialectWithVendor(Raml10TypesDialect(), Spec.RAML10),
    DialectWithVendor(OAS20Dialect(), Spec.OAS20),
    DialectWithVendor(OAS30Dialect(), Spec.OAS30),
    DialectWithVendor(AsyncApi20Dialect(), Spec.ASYNC20),
    DialectWithVendor(MetaDialect(), Spec.AML)
  )

  def spec(dialect: Dialect): Option[Spec] =
    webApiDialects.find(_.dialect == dialect).map(_.spec)
  def dialect(spec: Spec): Option[Dialect] =
    webApiDialects.find(_.spec == spec).map(_.dialect)
}
