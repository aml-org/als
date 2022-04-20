package org.mulesoft.amfintegration.amfconfiguration

import amf.aml.client.scala.model.document.Dialect
import amf.apicontract.client.scala.AMFConfiguration
import amf.core.client.scala.AMFParseResult
import amf.core.client.scala.config.{CachedReference, UnitCache}
import amf.core.client.scala.model.document.BaseUnit
import amf.core.client.scala.resource.ResourceLoader
import amf.core.client.scala.validation.AMFValidationResult
import org.mulesoft.als.configuration.ProjectConfiguration
import org.mulesoft.amfintegration.ValidationProfile

import scala.concurrent.Future

trait ProjectConfigurationState {

  def customSetUp(amfConfiguration: AMFConfiguration): AMFConfiguration = amfConfiguration
  def cache: UnitCache

  val extensions: Seq[Dialect]
  val profiles: Seq[ValidationProfile]
  val config: ProjectConfiguration
  val results: Seq[AMFParseResult]
  val resourceLoaders: Seq[ResourceLoader]
  val projectErrors: Seq[AMFValidationResult]
}

case class EmptyProjectConfigurationState(folder: String) extends ProjectConfigurationState() {
  override val cache: UnitCache = (url: String) =>
    Future.failed(new Exception("NothingUnitCache doesn't have any cached units"))
  override val extensions: Seq[Dialect]                = Seq.empty
  override val profiles: Seq[ValidationProfile]        = Seq.empty
  override val config: ProjectConfiguration            = ProjectConfiguration.empty(folder)
  override val results: Seq[AMFParseResult]            = Seq.empty
  override val resourceLoaders: Seq[ResourceLoader]    = Seq.empty
  override val projectErrors: Seq[AMFValidationResult] = Seq.empty
}

object EmptyProjectConfigurationState extends EmptyProjectConfigurationState("") {}
