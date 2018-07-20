package org.mulesoft.language.outline.test

import amf.core.model.document.BaseUnit
import amf.core.validation.AMFValidationReport

class ParseResult(val unit: BaseUnit, val report:AMFValidationReport) {

}

object ParseResult {
    def apply(unit: BaseUnit, report:AMFValidationReport) = new ParseResult(unit,report)
}
