package org.mulesoft.high.level.amfmanager

import amf.ProfileNames
import amf.client.commands.CommandHelper
import amf.core.annotations.SourceVendor
import amf.core.client.ParserConfig
import amf.core.model.document.{BaseUnit, EncodesModel}
import amf.core.parser.UnspecifiedReference
import amf.core.remote._
import amf.core.services.{RuntimeCompiler, RuntimeValidator}
import amf.core.validation.AMFValidationReport
import amf.internal.environment.Environment

import scala.concurrent.Future

class ParserHelper(val platform: Platform) extends CommandHelper {

  private def parseInput(url: String, env: Environment): Future[BaseUnit] = {

    val inputFile = ensureUrl(url)

    RuntimeCompiler(
      inputFile,
      None,
      None,
      Context(platform),
      UnspecifiedReference,
      Cache(),
      None,
      env
    )
  }

  def parseResult(url: String): Future[ParseResult] = {
    for {
      unit   <- parse(url)
      report <- report(unit)
    } yield ParseResult(unit, report)
  }

  def parse(url: String, env: Environment = Environment()): Future[BaseUnit] = {
    for {
      _     <- AmfInitializationHandler.init()
      model <- parseInput(url, env)
    } yield model
  }

  def printModel(model: BaseUnit, config: ParserConfig): Future[Unit] = {
    generateOutput(config, model)
  }

  def report(model: BaseUnit): Future[AMFValidationReport] = {
    val ann = model match {
      case d: EncodesModel => d.encodes.annotations.find(classOf[SourceVendor])
      case _               => model.annotations.find(classOf[SourceVendor])
    }
    val pn = ann.map(_.vendor) match {
      case Some(Raml10) => ProfileNames.RAML10
      case Some(Raml08) => ProfileNames.RAML08
      case Some(Raml)   => ProfileNames.RAML
      case Some(Oas20)  => ProfileNames.OAS20
      case Some(Oas30)  => ProfileNames.OAS30
      case Some(Oas)    => ProfileNames.OAS
      case Some(Aml)    => ProfileNames.AML
      case _            => ProfileNames.AMF
    }
    RuntimeValidator(model, pn)
  }
}

object ParserHelper {
  def apply(platform: Platform) = new ParserHelper(platform)
}
