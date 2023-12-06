package org.mulesoft.amfintegration.amfconfiguration

import amf.aml.client.scala.model.document.Dialect
import amf.apicontract.client.scala.{AMFConfiguration, APIConfiguration}
import amf.core.client.scala.AMFParseResult
import amf.core.client.scala.config.{CachedReference, UnitCache}
import amf.core.client.scala.model.document.Module
import amf.core.client.scala.resource.ResourceLoader
import amf.core.client.scala.validation.AMFValidationResult
import amf.graphql.client.scala.GraphQLConfiguration
import org.mulesoft.als.configuration.ProjectConfiguration
import org.mulesoft.amfintegration.ValidationProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait ProjectConfigurationState {

  def customSetUp(amfConfiguration: AMFConfiguration): AMFConfiguration = amfConfiguration

  def cache: UnitCache

  def getCompanionForDialect(d: Dialect): Future[Option[Module]] =
    Future
      .sequence(
        config.designDependency.map { dd =>
          cache.fetch(dd).map {
            case CachedReference(_, content: Module) => Some(content, content.references.map(_.id).contains(d.id))
            case _                                   => None
          }
        }
      )
      .map(r => r.flatten.find(_._2).map(_._1))

  def getProjectConfig: AMFConfiguration = {
    GraphQLConfiguration.GraphQL()
  }

  val extensions: Seq[Dialect]
  val profiles: Seq[ValidationProfile]
  val config: ProjectConfiguration
  val results: Seq[AMFParseResult]
  val resourceLoaders: Seq[ResourceLoader]
  val projectErrors: Seq[AMFValidationResult]
  val rootProjectConfiguration: AMFConfiguration = APIConfiguration.APIWithJsonSchema()
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
