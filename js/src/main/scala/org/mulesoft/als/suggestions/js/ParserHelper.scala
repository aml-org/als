package org.mulesoft.als.suggestions.js

import amf.client.commands.CommandHelper
import amf.core.client.ParserConfig
import amf.core.model.document.BaseUnit
import amf.core.remote.Platform
import amf.core.services.RuntimeValidator
import amf.core.validation.AMFValidationReport

import scala.concurrent.Future


class ParseResult(val unit: BaseUnit, val report:AMFValidationReport) {

}

object ParseResult {
    def apply(unit: BaseUnit, report:AMFValidationReport) = new ParseResult(unit,report)
}

class ParserHelper(val platform:Platform) extends CommandHelper{

    override def parseInput(config: ParserConfig): Future[BaseUnit] = super.parseInput(config)

    def parse(config: ParserConfig): Future[BaseUnit] = {
        val res = for {
            _          <- AMFInit()
            _          <- processDialects(config)
            model      <- parseInput(config)
        } yield {
            model
        }
        res
    }
}

object ParserHelper {
    def apply(platform: Platform) = new ParserHelper(platform)
}