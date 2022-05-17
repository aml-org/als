package org.mulesoft.amfintegration.amfconfiguration

import amf.aml.client.scala.AMLConfiguration
import amf.aml.client.scala.model.document.Dialect
import amf.apicontract.client.scala.AMFBaseUnitClient
import amf.core.client.common.transform.PipelineId
import amf.core.client.scala.{AMFParseResult, AMFResult}
import amf.core.client.scala.config.RenderOptions
import amf.core.client.scala.model.document.BaseUnit
import amf.core.client.scala.model.domain.DomainElement
import amf.core.client.scala.validation.AMFValidationReport
import amf.core.internal.remote.Spec
import org.yaml.builder.DocBuilder
import org.yaml.model.YNode

import scala.concurrent.Future
/*
 * A defined AML Configuration capable of resolution and emission
 * */
case class AMLSpecificConfiguration(config: AMLConfiguration) {

  def emit(de: DomainElement): YNode =
    config.elementClient().renderElement(de)

  def report(baseUnit: BaseUnit): Future[AMFValidationReport] =
    config
      .baseUnitClient()
      .validate(baseUnit.cloneUnit())

  def resolve(baseUnit: BaseUnit): AMFResult =
    config
      .baseUnitClient()
      .transform(baseUnit.cloneUnit(), PipelineId.Cache)

  def fullResolution(unit: BaseUnit): AMFResult = {
    config.baseUnitClient() match {
      case amf: AMFBaseUnitClient =>
        amf.transform(unit.cloneUnit(), PipelineId.Editing)
      case aml =>
        aml.transform(unit.cloneUnit(), PipelineId.Default)
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
  ): Unit =
    config
      .withRenderOptions(renderOptions)
      .baseUnitClient()
      .renderGraphToBuilder(resolved.cloneUnit(), builder)

  def parse(uri: String): Future[AMFParseResult] = config.baseUnitClient().parse(uri)
}
