package org.mulesoft.amfintegration.amfconfiguration

import amf.aml.client.scala.model.document.Dialect
import amf.core.client.scala.AMFResult
import amf.core.client.scala.validation.AMFValidationResult
import org.mulesoft.amfintegration.AmfImplicits.BaseUnitImp

class AmfParseResult(val result: AMFResult, val definedBy: Dialect, val amfConfiguration: AmfConfigurationWrapper) {
  val location: String = result.baseUnit.location().getOrElse(result.baseUnit.id)

  def groupedErrors: Map[String, Seq[AMFValidationResult]] =
    result.results.groupBy(e => e.location.getOrElse(location))

  lazy val tree: Set[String] = result.baseUnit.flatRefs
    .map(bu => bu.location().getOrElse(bu.id))
    .toSet + location
}