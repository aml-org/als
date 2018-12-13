package org.mulesoft.als.suggestions.test

import amf.core.model.document.BaseUnit
import amf.core.validation.AMFValidationReport

// todo: same that in hl, problems with dependencies in tests modules. Fix later. (extract to common?)
class ParseResult(val unit: BaseUnit, val report:AMFValidationReport) {

}

object ParseResult {
  def apply(unit: BaseUnit, report:AMFValidationReport) = new ParseResult(unit,report)
}
