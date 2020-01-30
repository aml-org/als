package org.mulesoft.als.server.modules.diagnostic

import amf.ProfileName
import amf.core.model.document.BaseUnit
import amf.core.remote.Aml
import amf.core.services.RuntimeValidator
import amf.core.validation.{AMFValidationReport, AMFValidationResult}
import org.mulesoft.als.server.ClientNotifierModule
import org.mulesoft.als.server.client.ClientNotifier
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.modules.ast._
import org.mulesoft.als.server.modules.common.reconciler.Reconciler
import org.mulesoft.als.server.modules.workspace.DiagnosticsBundle
import org.mulesoft.amfmanager.AmfParseResult
import org.mulesoft.lsp.ConfigType
import org.mulesoft.lsp.feature.diagnostic.{DiagnosticClientCapabilities, DiagnosticConfigType}
import org.mulesoft.lsp.feature.telemetry.{MessageTypes, TelemetryProvider}

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

class DiagnosticManager(private val telemetryProvider: TelemetryProvider,
                        private val clientNotifier: ClientNotifier,
                        private val logger: Logger,
                        private val optimizationKind: DiagnosticNotificationsKind = ALL_TOGETHER)
    extends BaseUnitListener
    with ClientNotifierModule[DiagnosticClientCapabilities, Unit] {

  override val `type`: ConfigType[DiagnosticClientCapabilities, Unit] = DiagnosticConfigType

  override def applyConfig(config: Option[DiagnosticClientCapabilities]): Unit = {}

  private val reconciler: Reconciler = new Reconciler(logger, 300)

  private val notifyParsing: Boolean = optimizationKind == PARSING_BEFORE
  override def initialize(): Future[Unit] = {
    Future.successful()
  }

  private val resultsByUnit: mutable.Map[String, Seq[AMFValidationResult]] = mutable.Map.empty

  /**
    * Called on new AST available
    *
    * @param tuple - (AST, References)
    * @param uuid  - telemetry UUID
    */
  override def onNewAst(tuple: (AmfParseResult, Map[String, DiagnosticsBundle]), uuid: String): Unit = {
    val result     = tuple._1
    val references = tuple._2
    logger.debug("Got new AST:\n" + result.baseUnit.toString, "ValidationManager", "newASTAvailable")
    val uri = result.location
    telemetryProvider.addTimedMessage("Start report",
                                      "DiagnosticManager",
                                      "onNewAst",
                                      MessageTypes.BEGIN_DIAGNOSTIC,
                                      uri,
                                      uuid)

    reconciler
      .shedule(new ValidationRunnable(uri, () => gatherValidationErrors(uri, result, references, uuid)))
      .future andThen {
      case Success(_) =>
        telemetryProvider.addTimedMessage("End report",
                                          "DiagnosticManager",
                                          "onNewAst",
                                          MessageTypes.END_DIAGNOSTIC,
                                          uri,
                                          uuid)

      case Failure(exception) =>
        telemetryProvider.addTimedMessage(s"End report: ${exception.getMessage}",
                                          "DiagnosticManager",
                                          "onNewAst",
                                          MessageTypes.END_DIAGNOSTIC,
                                          uri,
                                          uuid)
        logger.warning("Error on validation: " + exception.toString, "ValidationManager", "newASTAvailable")
        clientNotifier.notifyDiagnostic(ValidationReport(uri, Set.empty).publishDiagnosticsParams)
    }
  }

  private def notifyReport(result: AmfParseResult, references: Map[String, DiagnosticsBundle], step: String): Unit = {

    val errors = DiagnosticConverters.buildIssueResults(merge(result), references)

    logger.debug(s"Number of $step errors is:\n" + errors.flatMap(_.issues).length,
                 "ValidationManager",
                 "newASTAvailable")
    errors.foreach(r => clientNotifier.notifyDiagnostic(r.publishDiagnosticsParams))
  }

  private def merge(result: AmfParseResult): Map[String, Seq[AMFValidationResult]] = {
    val merged = merge(result.groupedErrors, resultsByUnit.toMap)
    result.tree.map(t => t -> merged.getOrElse(t, Nil)).toMap
  }

  private def merge(left: Map[String, Seq[AMFValidationResult]],
                    right: Map[String, Seq[AMFValidationResult]]): Map[String, Seq[AMFValidationResult]] =
    left.map {
      case (k, v) => k -> (v ++ right.getOrElse(k, Nil))
    } ++ right.filter(t => !left.keys.exists(_ == t._1))

  private def gatherValidationErrors(uri: String,
                                     result: AmfParseResult,
                                     references: Map[String, DiagnosticsBundle],
                                     uuid: String): Future[Unit] = {
    val startTime = System.currentTimeMillis()

    if (notifyParsing) notifyReport(result, references, "parsing")
    this
      .report(uri, telemetryProvider, result.baseUnit, uuid)
      .map(report => {
        val endTime = System.currentTimeMillis()
        indexNewReport(report, result, uuid)
        notifyReport(result, references, "model and resolution")

        this.logger.debugDetail(s"It took ${endTime - startTime} milliseconds to validate",
                                "ValidationManager",
                                "gatherValidationErrors")
      })
  }

  private def indexNewReport(report: AMFValidationReport, result: AmfParseResult, uuid: String): Unit = {
    val results: Map[String, Seq[AMFValidationResult]] =
      report.results.groupBy(r => r.location.getOrElse(result.location))

    telemetryProvider.addTimedMessage(s"Got reports: ${result.location}",
                                      "DiagnosticManager",
                                      "onNewAst",
                                      MessageTypes.GOT_DIAGNOSTICS,
                                      result.location,
                                      uuid)

    result.tree.foreach { t =>
      results.get(t) match {
        case Some(r) => resultsByUnit.update(t, r)
        case _       => resultsByUnit.remove(t)
      }
    }
  }

  // check if DialectInstance <- nameAndVersion ?
  // check if .raml (and force RAML vendor)
  private def checkProfileName(baseUnit: BaseUnit): String =
    baseUnit.sourceVendor.map(_.name).getOrElse(Aml.toString)

  private def report(uri: String,
                     telemetryProvider: TelemetryProvider,
                     baseUnit: BaseUnit,
                     uuid: String): Future[AMFValidationReport] = {
    telemetryProvider.addTimedMessage("Start AMF report",
                                      "DiagnosticManager",
                                      "report",
                                      MessageTypes.BEGIN_REPORT,
                                      uri,
                                      uuid)
    val eventualReport = RuntimeValidator(baseUnit.cloneUnit(), ProfileName(checkProfileName(baseUnit)))
    eventualReport.foreach(
      r =>
        telemetryProvider
          .addTimedMessage("End AMF report", "DiagnosticManager", "report", MessageTypes.END_REPORT, uri, uuid))
    eventualReport
  }

  override def onRemoveFile(uri: String): Unit =
    clientNotifier.notifyDiagnostic(AlsPublishDiagnosticsParams(uri, Nil))
}

case class DiagnosticNotificationsKind(kind: String)

object PARSING_BEFORE extends DiagnosticNotificationsKind("PARSING_BEFORE")
object ALL_TOGETHER   extends DiagnosticNotificationsKind("ALL_TOGETHER")
