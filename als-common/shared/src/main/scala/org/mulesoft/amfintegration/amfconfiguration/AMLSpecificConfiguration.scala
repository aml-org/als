package org.mulesoft.amfintegration.amfconfiguration

import amf.aml.client.scala.AMLConfiguration
import amf.apicontract.client.scala.AMFBaseUnitClient
import amf.core.client.common.transform.PipelineId
import amf.core.client.scala.config.RenderOptions
import amf.core.client.scala.model.document.BaseUnit
import amf.core.client.scala.model.domain.DomainElement
import amf.core.client.scala.validation.AMFValidationReport
import amf.core.client.scala.{AMFParseResult, AMFResult}
import org.yaml.builder.DocBuilder
import org.yaml.model.YNode

import scala.concurrent.Future
/*
 * A defined AML Configuration capable of resolution and emission
 * */
case class AMLSpecificConfiguration(config: AMLConfiguration, newCachingLogic: Boolean) {

  def emit(de: DomainElement): YNode =
    config.elementClient().renderElement(de)

  def report(baseUnit: BaseUnit): Future[AMFValidationReport] = {
    val finalBaseUnit = if (newCachingLogic) baseUnit else baseUnit.cloneUnit()

    config
      .baseUnitClient()
      .validate(finalBaseUnit)
  }

  def resolve(baseUnit: BaseUnit): AMFResult = {
    val finalBaseUnit = if (newCachingLogic) baseUnit else baseUnit.cloneUnit()
    config
      .baseUnitClient()
      .transform(finalBaseUnit, PipelineId.Cache)
  }

  def fullResolution(unit: BaseUnit): AMFResult = {
    val finalUnit = if (newCachingLogic) unit else unit.cloneUnit()
    config.baseUnitClient() match {
      case amf: AMFBaseUnitClient =>
        amf.transform(finalUnit, PipelineId.Editing)
      case aml =>
        aml.transform(finalUnit, PipelineId.Default)
    }
  }

  def renderElement(de: DomainElement): YNode =
    config.elementClient().renderElement(de)

  def serialize(syntax: String, unit: BaseUnit): String =
    config.baseUnitClient().render(unit, syntax)

  def convertTo(model: BaseUnit, syntax: Option[String]): String = {
    val client = config.baseUnitClient()
    val result = client.transform(model.cloneUnit(), PipelineId.Compatibility)
    syntax.map(s => s"application/$s").fold(client.render(result.baseUnit))(s => client.render(result.baseUnit, s))
  }

  def asJsonLD(
      resolved: BaseUnit,
      builder: DocBuilder[_],
      renderOptions: RenderOptions = RenderOptions().withCompactUris.withoutSourceMaps
  ): Unit = {
    val finalResolved = if (newCachingLogic) resolved else resolved.cloneUnit()
    config
      .withRenderOptions(renderOptions)
      .baseUnitClient()
      .renderGraphToBuilder(finalResolved, builder)
  }

  def parse(uri: String): Future[AMFParseResult] = config.baseUnitClient().parse(uri)
}
