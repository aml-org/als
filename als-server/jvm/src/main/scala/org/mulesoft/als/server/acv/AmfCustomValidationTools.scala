package org.mulesoft.als.server.acv

import amf.aml.client.scala.AMLConfiguration
import amf.aml.client.scala.model.document.DialectInstance
import amf.custom.validation.client.scala.report.OPAValidatorReportLoader
import amf.custom.validation.client.scala.report.model.{AMLOpaReport, ValidationProfileWrapper}
import amf.custom.validation.internal.report.loaders.ProfileDialectLoader
import amf.custom.validation.internal.report.parser.{AMFValidationOpaAdapter, OpaValidatorReportParser}
import org.mulesoft.als.logger.Logger

import java.util.concurrent.CompletableFuture
import scala.compat.java8.FutureConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object AmfCustomValidationTools {
  def parseReport(
      serializedReport: String,
      apiUri: String,
      profileContent: String
  ): CompletableFuture[CustomDiagnosticReport] = {
    ProfileDialectLoader.dialect
      .flatMap {
        AMLConfiguration
          .predefined()
          .withDialect(_)
          .baseUnitClient()
          .parseContent(profileContent)
          .map { r => r.baseUnit }
          .flatMap {
            case dialectProfile: DialectInstance =>
              OpaValidatorReportParser.parse(serializedReport).map { report =>
                AMLOpaReport(report.baseUnit.asInstanceOf[DialectInstance], ValidationProfileWrapper(dialectProfile))
              }
            case _ =>
              val errorMsg = "unexpected result from parsing the custom validation profile"
              Logger.error(errorMsg, "AmfCustomValidationTools", "parseReport")
              Future.failed(new RuntimeException(errorMsg))
          }
          .map(CustomDiagnosticReportBuilder.toDiagnosticReport)
          .recover { case e: Exception =>
            Logger.error(e.getMessage, "AmfCustomValidationTools", "parseReport")
            throw e
          }
      }
      .toJava
      .toCompletableFuture
  }

}
