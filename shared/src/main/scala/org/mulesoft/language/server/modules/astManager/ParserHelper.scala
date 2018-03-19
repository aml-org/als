package org.mulesoft.language.server.server.modules.astManager

import amf.client.commands.CommandHelper
import amf.core.client.ParserConfig
import amf.core.model.document.BaseUnit
import amf.core.remote.Platform
import amf.core.services.RuntimeValidator
import amf.core.validation.AMFValidationReport
import amf.plugins.features.validation.PlatformValidator

import scala.concurrent.Future

class ParserHelper(val platform:Platform) extends CommandHelper{

    override def parseInput(config: ParserConfig): Future[BaseUnit] = super.parseInput(config)

    def parseResult(config: ParserConfig): Future[ParseResult] = {

        val validatorInstance = PlatformValidator.instance;
        println(validatorInstance)

        for {
            unit <- parse(config)
            report <- report(unit,config)
        } yield {
            println("Finished parsing and validating in ParserHelper")
            ParseResult(unit,report)
        }
    }

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

    def report(model: BaseUnit, config: ParserConfig):Future[AMFValidationReport] = {
        println("Reporting unit")
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
                RuntimeValidator.reset()
                result
            }
        )
    }
}

object ParserHelper {
    def apply(platform: Platform) = new ParserHelper(platform)
}