// $COVERAGE-OFF$
package org.mulesoft.language.server

import amf.client.commands.{CmdLineParser, ParseCommand, TranslateCommand, ValidateCommand}
import amf.core.client.{ExitCodes, ParserConfig}
import amf.core.unsafe.PlatformSecrets

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.language.postfixOps
import amf.core.client.{ExitCodes, ParserConfig}
import amf.core.model.document.BaseUnit
import amf.core.remote.Platform
import amf.core.services.RuntimeValidator
import amf.core.validation.AMFValidationReport
import amf.plugins.features.validation.emitters.ValidationReportJSONLDEmitter

import scala.concurrent.Future
import scala.util.{Failure, Success}

object Test extends PlatformSecrets {
  def main(args: Array[String]): Unit = {
    val config = CmdLineParser.parse(
      "validate -in \"RAML 1.0\" -mime-in \"application/yaml\" /Users/munch/apis/Instagram/test53.raml".split(" "));

    CmdLineParser.parse(args) match {
      case Some(cfg) =>
        cfg.mode match {
          case Some(ParserConfig.TRANSLATE) => Await.result(runTranslate(cfg), 1 day)
          case Some(ParserConfig.VALIDATE)  => Await.result(runValidate(cfg), 1 day)
          case Some(ParserConfig.PARSE)     => Await.ready(runParse(cfg), 1 day)
          case _                            => failCommand()
        }
      case _ => System.exit(ExitCodes.WrongInvocation)
    }
    System.exit(ExitCodes.Success)
  }

  def failCommand(): Unit = {
    System.err.println("Wrong command")
    System.exit(ExitCodes.WrongInvocation)
  }
  def runTranslate(config: ParserConfig): Future[Any] = TranslateCommand(platform).run(config)
  def runValidate(config: ParserConfig): Future[Any]  = ValidateCommand(platform).run(config)
  def runParse(config: ParserConfig): Future[Any]     = ParseCommand(platform).run(config)
}
// $COVERAGE-ON$
