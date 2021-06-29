package org.mulesoft.amfintegration.dialect.integration

import amf.aml.client.scala.AMLConfigurationState
import amf.aml.client.scala.model.document.{Dialect, DialectInstanceUnit, Vocabulary}
import amf.core.client.scala.model.document.{BaseUnit, ExternalFragment}
import amf.core.internal.remote.Spec
import org.mulesoft.amfintegration.amfconfiguration.ProfileMatcher
import org.mulesoft.amfintegration.dialect.dialects.ExternalFragmentDialect
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.AsyncApi20Dialect
import org.mulesoft.amfintegration.dialect.dialects.metadialect.{MetaDialect, VocabularyDialect}
import org.mulesoft.amfintegration.dialect.dialects.oas.{OAS20Dialect, OAS30Dialect}
import org.mulesoft.amfintegration.dialect.dialects.raml.raml08.Raml08TypesDialect
import org.mulesoft.amfintegration.dialect.dialects.raml.raml10.Raml10TypesDialect

import scala.collection.immutable

/**
  * @param dialects initialized with the server on startup (for example Web API dialects)
  */
case class BaseAlsDialectProvider(dialects: Set[Dialect]) {

  def definitionFor(bu: BaseUnit)(implicit configurationState: AMLConfigurationState): Option[Dialect] = bu match {
    case di: DialectInstanceUnit =>
      allDialects(configurationState).find(d => di.definedBy().option().contains(d.id))
    case _: Dialect =>
      Some(MetaDialect.dialect)
    case _: Vocabulary =>
      Some(VocabularyDialect.dialect)
    case _: ExternalFragment =>
      Some(ExternalFragmentDialect.dialect)
    case _ =>
      allDialects(configurationState).find(d => ProfileMatcher.spec(d).contains(bu.sourceSpec.getOrElse(Spec.AMF)))
  }

  def definitionFor(spec: Spec)(implicit configurationState: AMLConfigurationState): Option[Dialect] =
    allDialects(configurationState).find(d => ProfileMatcher.spec(d).contains(spec))

  private def allDialects(configurationState: AMLConfigurationState) =
    configurationState.getDialects().toSet ++ dialects

  def definitionFor(nameAndVersion: String)(implicit configurationState: AMLConfigurationState): Option[Dialect] =
    configurationState.getDialects().find(_.nameAndVersion() == nameAndVersion)
}

object BaseAlsDialectProvider {
  val apiDialects: Set[Dialect] = Set(
    Raml08TypesDialect(),
    Raml10TypesDialect(),
    OAS20Dialect(),
    OAS30Dialect(),
    AsyncApi20Dialect()
  )

  val allDialects: Set[Dialect] = apiDialects + MetaDialect()

  def apply(): BaseAlsDialectProvider = new BaseAlsDialectProvider(allDialects)
}
