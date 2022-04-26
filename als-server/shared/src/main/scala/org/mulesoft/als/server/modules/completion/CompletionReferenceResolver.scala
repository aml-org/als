package org.mulesoft.als.server.modules.completion

import amf.aml.client.scala.model.document.Dialect
import amf.core.client.scala.AMFParseResult
import amf.core.client.scala.config.{CachedReference, UnitCache}
import amf.core.client.scala.model.document.BaseUnit
import amf.core.client.scala.resource.ResourceLoader
import amf.core.client.scala.validation.AMFValidationResult
import org.mulesoft.als.configuration.ProjectConfiguration
import org.mulesoft.amfintegration.AmfImplicits.BaseUnitImp
import org.mulesoft.amfintegration.ValidationProfile
import org.mulesoft.amfintegration.amfconfiguration.ProjectConfigurationState

import scala.concurrent.Future

case class CompletionReferenceResolver(extensions: Seq[Dialect],
                                       profiles: Seq[ValidationProfile],
                                       config: ProjectConfiguration,
                                       results: Seq[AMFParseResult],
                                       resourceLoaders: Seq[ResourceLoader],
                                       projectErrors: Seq[AMFValidationResult],
                                       bu: BaseUnit)
    extends ProjectConfigurationState {
  override def cache: UnitCache = new UnitCache {
    val map: Map[String, BaseUnit] = bu.flatRefs.map(bu => bu.location().getOrElse(bu.id) -> bu).toMap
    override def fetch(url: String): Future[CachedReference] = map.get(url) match {
      case Some(bu) => Future.successful(CachedReference(url, bu))
      case _        => throw new Exception("Unit not found")
    }
  }
}

object CompletionReferenceResolver {
  def apply(p: ProjectConfigurationState, bu: BaseUnit): CompletionReferenceResolver = {
    new CompletionReferenceResolver(
      p.extensions,
      p.profiles,
      p.config,
      p.results,
      p.resourceLoaders,
      p.projectErrors,
      bu
    )
  }
}
