package org.mulesoft.als.suggestions.test

import amf.aml.client.scala.model.document.Dialect
import amf.core.client.scala.AMFParseResult
import amf.core.client.scala.config.{CachedReference, UnitCache}
import amf.core.client.scala.model.document.{BaseUnit, Module}
import amf.core.client.scala.resource.ResourceLoader
import amf.core.client.scala.validation.AMFValidationResult
import org.mulesoft.als.configuration.ProjectConfiguration
import org.mulesoft.amfintegration.AmfImplicits.BaseUnitImp
import org.mulesoft.amfintegration.ValidationProfile
import org.mulesoft.amfintegration.amfconfiguration.ProjectConfigurationState

import scala.concurrent.Future

case class TestProjectConfigurationState(
    d: Seq[Dialect],
    override val config: ProjectConfiguration,
    cachedUnit: BaseUnit
) extends ProjectConfigurationState {
  override def cache: UnitCache = new UnitCache {
    val map: Map[String, BaseUnit] = {
      val clone = cachedUnit.cloneUnit()
      Map(clone.identifier -> clone)
    }
    override def fetch(url: String): Future[CachedReference] = map.get(url) match {
      case Some(bu) => Future.successful(CachedReference(url, bu))
      case _        => throw new Exception("Unit not found")
    }
  }

  override val extensions: Seq[Dialect]                = d
  override val profiles: Seq[ValidationProfile]        = Nil
  override val results: Seq[AMFParseResult]            = Nil
  override val resourceLoaders: Seq[ResourceLoader]    = Nil
  override val projectErrors: Seq[AMFValidationResult] = Nil
}
