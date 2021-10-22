package org.mulesoft.amfintegration

import amf.core.client.scala.model.document.BaseUnit
import amf.core.client.scala.validation.AMFValidationReport

class ParseResult(val unit: BaseUnit, val report: AMFValidationReport) {}

object ParseResult {
  def apply(unit: BaseUnit, report: AMFValidationReport) = new ParseResult(unit, report)
}
