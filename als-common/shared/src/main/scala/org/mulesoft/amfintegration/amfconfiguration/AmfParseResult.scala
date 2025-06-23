package org.mulesoft.amfintegration.amfconfiguration

import amf.core.client.scala.AMFResult
import amf.core.client.scala.validation.AMFValidationResult
import amf.shapes.client.scala.ShapesConfiguration
import org.mulesoft.amfintegration.AmfImplicits.BaseUnitImp
import org.mulesoft.common.collections._

case class AmfParseContext(amfConfiguration: ShapesConfiguration, state: ALSConfigurationState)

class AmfResult(val result: AMFResult) {
  val location: String = result.baseUnit.location().getOrElse(result.baseUnit.id)

  def groupedErrors: Map[String, Seq[AMFValidationResult]] =
    result.results.legacyGroupBy(e => e.location.getOrElse(location))

  lazy val tree: Set[String] = result.baseUnit.flatRefs
    .map(bu => bu.location().getOrElse(bu.id))
    .toSet + location
}

class AmfParseResult(
                      override val result: AMFResult,
                      val documentDefinition: DocumentDefinition,
                      val context: AmfParseContext,
                      val uri: String
) extends AmfResult(result)
