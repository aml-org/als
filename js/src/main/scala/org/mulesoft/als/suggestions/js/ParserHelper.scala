package org.mulesoft.als.suggestions.js

import amf.client.commands.CommandHelper
import amf.core.client.ParserConfig
import amf.core.model.document.BaseUnit
import amf.core.parser.UnspecifiedReference
import amf.core.remote.{Cache, Context, Platform}
import amf.core.services.{RuntimeCompiler, RuntimeValidator}
import amf.core.validation.AMFValidationReport
import amf.internal.environment.Environment

import scala.concurrent.Future


class ParseResult(val unit: BaseUnit, val report:AMFValidationReport) {

}

object ParseResult {
    def apply(unit: BaseUnit, report:AMFValidationReport) = new ParseResult(unit,report)
}

class ParserHelper(val platform:Platform) extends CommandHelper{

    protected def parseInput(config: ParserConfig,env:Environment): Future[BaseUnit] = {
        var inputFile   = ensureUrl(config.input.get)
        val inputFormat = config.inputFormat.get
        RuntimeCompiler(
            inputFile,
            Option(effectiveMediaType(config.inputMediaType, config.inputFormat)),
            effectiveVendor(config.inputMediaType, config.inputFormat),
            Context(platform),
            UnspecifiedReference,
            Cache(),
            None,
            env
        )
    }

    def parse(config: ParserConfig, env:Environment = Environment()): Future[BaseUnit] = {
        val res = for {
            _          <- AMFInit()
            _          <- processDialects(config)
            model      <- parseInput(config, env)
        } yield {
            model
        }
        res
    }
}

object ParserHelper {
    def apply(platform: Platform) = new ParserHelper(platform)
}