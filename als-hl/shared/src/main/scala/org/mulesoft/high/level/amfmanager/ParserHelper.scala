package org.mulesoft.high.level.amfmanager

import amf.client.commands.CommandHelper
import amf.core.client.ParserConfig
import amf.core.model.document.BaseUnit
import amf.core.parser.UnspecifiedReference
import amf.core.remote.{Cache, Context, Platform}
import amf.core.services.{RuntimeCompiler, RuntimeValidator}
import amf.core.validation.AMFValidationReport
import amf.internal.environment.Environment

import scala.concurrent.Future

class ParserHelper(val platform:Platform) extends CommandHelper{

    private def parseInput(config: ParserConfig,env:Environment): Future[BaseUnit] = {
        var inputFile   = ensureUrl(config.input.get)
        val inputFormat = config.inputFormat.get
        RuntimeCompiler(
            inputFile,
            Option(effectiveMediaType(config.inputMediaType, config.inputFormat)),
            config.inputFormat,
            Context(platform),
            UnspecifiedReference,
            Cache(),
            None,
            env
        )
    }

    def parseResult(config: ParserConfig): Future[ParseResult] = {
        for {
            unit <- parse(config)
            report <- report(unit,config)
        } yield {
            ParseResult(unit,report)
        }
    }

    def parse(config: ParserConfig,env:Environment = Environment()): Future[BaseUnit] = {
        for {
            _          <- AmfInitializationHandler.init()
            _          <- processDialects(config)
            model      <- parseInput(config,env)
        } yield model
    }

    def printModel(model:BaseUnit, config: ParserConfig): Future[Unit] = {
        generateOutput(config,model)
    }

    def report(model: BaseUnit, config: ParserConfig):Future[AMFValidationReport] = {
        val customProfileLoaded = if (config.customProfile.isDefined) {
            RuntimeValidator.loadValidationProfile(config.customProfile.get) map { profileName =>
                profileName
            }
        } else {
            Future {
                config.profile
            }
        }
        customProfileLoaded flatMap  { profile =>
            RuntimeValidator(model, profile)
        }
    }
}

object ParserHelper {
    def apply(platform: Platform) = new ParserHelper(platform)
}