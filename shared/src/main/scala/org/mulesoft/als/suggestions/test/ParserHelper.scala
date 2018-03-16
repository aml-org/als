package org.mulesoft.als.suggestions.test

import amf.client.commands.CommandHelper
import amf.core.client.ParserConfig
import amf.core.model.document.BaseUnit
import amf.core.remote.Platform
import amf.core.services.RuntimeValidator
import amf.core.validation.AMFValidationReport

import scala.concurrent.Future

class ParserHelper(val platform:Platform) extends CommandHelper{

    override def parseInput(config: ParserConfig): Future[BaseUnit] = super.parseInput(config)

    def parseResult(config: ParserConfig): Future[ParseResult] = {
        for {
            unit <- parse(config)
            report <- report(unit,config)
        } yield {
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
                config.validationProfile
            }
        }
        customProfileLoaded flatMap  { profileName =>
            RuntimeValidator(model, profileName)
        }
    }
}

object ParserHelper {
    def apply(platform: Platform) = new ParserHelper(platform)
}