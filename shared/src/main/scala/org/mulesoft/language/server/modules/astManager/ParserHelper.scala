package org.mulesoft.language.server.server.modules.astManager

import amf.client.commands.CommandHelper
import amf.core.client.ParserConfig
import amf.core.model.document.BaseUnit
import amf.core.parser.UnspecifiedReference
import amf.core.remote.{Cache, Context, Platform}
import amf.core.services.{RuntimeCompiler, RuntimeValidator}
import amf.core.validation.AMFValidationReport
import amf.internal.environment.Environment
import amf.plugins.features.validation.PlatformValidator

import scala.concurrent.Future

class ParserHelper(val platform:Platform) extends CommandHelper{

    protected def parseInput(config: ParserConfig, env:Environment): Future[BaseUnit] = {
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

    def parseResult(config: ParserConfig,env:Environment): Future[ParseResult] = {

        val validatorInstance = PlatformValidator.instance;

        for {
            unit <- parse(config, env)
            report <- report(unit,config)
        } yield {
            ParseResult(unit,report)
        }
    }

    def parse(config: ParserConfig,env:Environment): Future[BaseUnit] = {
        val res = for {
            _          <- AMFInit()
            _          <- processDialects(config)
            model      <- parseInput(config, env)
        } yield {
            model
        }
        res
    }

    def report(model: BaseUnit, config: ParserConfig):Future[AMFValidationReport] = {
        val customProfileLoaded = if (config.customProfile.isDefined) {
            RuntimeValidator.loadValidationProfile(config.customProfile.get) map { profileName =>
                profileName
            }
        } else {
            Future {
                config.validationProfile
            }
        }
        customProfileLoaded.flatMap(profileName => {
                val result = RuntimeValidator(model, profileName)
                //RuntimeValidator.reset()
                result
            }
        )
    }

    def printModel(model:BaseUnit, config: ParserConfig): Future[Unit] = {
        generateOutput(config,model)
    }
}

object ParserHelper {
    def apply(platform: Platform) = new ParserHelper(platform)
}