package org.mulesoft.als.server.modules.diagnostic

import amf.core.model.document.BaseUnit
import amf.core.services.RuntimeValidator
import amf.core.validation.{AMFValidationReport, AMFValidationResult}
import amf.{ProfileName, ProfileNames}
import org.mulesoft.als.server.ClientNotifierModule
import org.mulesoft.als.server.client.ClientNotifier
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.modules.ast._
import org.mulesoft.als.server.modules.common.reconciler.Reconciler
import org.mulesoft.als.server.modules.workspace.DiagnosticsBundle
import org.mulesoft.amfintegration.AmfResolvedUnit
import org.mulesoft.amfmanager.{AmfParseResult, ParserHelper}
import org.mulesoft.lsp.ConfigType
import org.mulesoft.lsp.feature.diagnostic.{DiagnosticClientCapabilities, DiagnosticConfigType}
import org.mulesoft.lsp.feature.telemetry.{MessageTypes, TelemetryProvider}
import amf.core.validation.SeverityLevels.VIOLATION
import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js.annotation.JSExport
import scala.util.{Failure, Success}

class DiagnosticManager(private val telemetryProvider: TelemetryProvider,
                        private val clientNotifier: ClientNotifier,
                        private val logger: Logger,
                        private val optimizationKind: DiagnosticNotificationsKind = ALL_TOGETHER)
    extends BaseUnitListener
    with ClientNotifierModule[DiagnosticClientCapabilities, Unit] {

  override val `type`: ConfigType[DiagnosticClientCapabilities, Unit] =
    DiagnosticConfigType

  override def applyConfig(config: Option[DiagnosticClientCapabilities]): Unit = {
    // not used
  }

  private val reconciler: Reconciler = new Reconciler(logger, 300)

  private val notifyParsing: Boolean = optimizationKind == PARSING_BEFORE
  override def initialize(): Future[Unit] = {
    Future.successful()
  }

  private val resultsByUnit: mutable.Map[String, Seq[AMFValidationResult]] =
    mutable.Map.empty

  /**
    * Called on new AST available
    *
    * @param tuple - (AST, References)
    * @param uuid  - telemetry UUID
    */
  override def onNewAst(tuple: BaseUnitListenerParams, uuid: String): Unit = {
    val parsedResult     = tuple.parseResult
    val futureResolvedFn = tuple.resolvedUnit
    val references       = tuple.diagnosticsBundle
    logger.debug("Got new AST:\n" + parsedResult.baseUnit.id, "ValidationManager", "newASTAvailable")
    val uri = parsedResult.location
    telemetryProvider.addTimedMessage("Start report",
                                      "DiagnosticManager",
                                      "onNewAst",
                                      MessageTypes.BEGIN_DIAGNOSTIC,
                                      uri,
                                      uuid)
    reconciler
      .shedule(
        new ValidationRunnable(uri,
                               () => gatherValidationErrors(uri, parsedResult, futureResolvedFn, references, uuid)))
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
        logger.error("Error on validation: " + exception.toString, "ValidationManager", "newASTAvailable")
        clientNotifier.notifyDiagnostic(ValidationReport(uri, Set.empty, ProfileNames.AMF).publishDiagnosticsParams)
    }
  }

  private def notifyReport(result: AmfParseResult,
                           references: Map[String, DiagnosticsBundle],
                           step: String,
                           profile: ProfileName): Unit = {

    val errors =
      DiagnosticConverters.buildIssueResults(merge(result), references, profile)

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
                                     resolved: () => Future[AmfResolvedUnit],
                                     references: Map[String, DiagnosticsBundle],
                                     uuid: String): Future[Unit] = {
    val startTime = System.currentTimeMillis()

    val profile = profileName(result.baseUnit)
    if (notifyParsing) notifyReport(result, references, "parsing", profile)
    this
      .report(uri, telemetryProvider, resolved, result.baseUnit, uuid, profile)
      .map(report => {
        val endTime = System.currentTimeMillis()
        indexNewReport(report, result, uuid)
        notifyReport(result, references, "model and resolution", profile)

        this.logger.debug(s"It took ${endTime - startTime} milliseconds to validate",
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
  private def profileName(baseUnit: BaseUnit): ProfileName = ParserHelper.profile(baseUnit)

  private def report(uri: String,
                     telemetryProvider: TelemetryProvider,
                     futureResolvedFn: () => Future[AmfResolvedUnit],
                     baseUnit: BaseUnit,
                     uuid: String,
                     profile: ProfileName): Future[AMFValidationReport] = {
    telemetryProvider.addTimedMessage("Start AMF report",
                                      "DiagnosticManager",
                                      "report",
                                      MessageTypes.BEGIN_REPORT,
                                      uri,
                                      uuid)
    try {
      futureResolvedFn().map(_.resolvedUnit).flatMap { baseUnit =>
        RuntimeValidator(baseUnit, profile, resolved = true)
      } andThen {
        case _ =>
          telemetryProvider
            .addTimedMessage("End AMF report", "DiagnosticManager", "report", MessageTypes.END_REPORT, uri, uuid)
      } recoverWith {
        case e: Exception => sendFailedClone(uri, telemetryProvider, baseUnit, uuid, e.getMessage)
      }
    } catch {
      case e: Exception =>
        sendFailedClone(uri, telemetryProvider, baseUnit, uuid, e.getMessage)
    }
  }

  private def sendFailedClone(uri: String,
                              telemetryProvider: TelemetryProvider,
                              baseUnit: BaseUnit,
                              uuid: String,
                              e: String) = {
    val msg =
      s"DiagnosticManager suffered an unexpected error while cloning unit: $e"
    logger.warning(msg, "DiagnosticManager", "report")
    telemetryProvider.addTimedMessage(msg, "DiagnosticManager", "report", MessageTypes.DIAGNOSTIC_ERROR, uri, uuid)

    Future.successful(failedReportDiagnostic(msg, baseUnit))
  }

  private final def failedReportDiagnostic(msg: String, baseUnit: BaseUnit): AMFValidationReport =
    AMFValidationReport(conforms = false,
                        "",
                        profileName(baseUnit),
                        Seq(AMFValidationResult(msg, VIOLATION, "", None, "", None, baseUnit.location(), None)))

  override def onRemoveFile(uri: String): Unit =
    clientNotifier.notifyDiagnostic(AlsPublishDiagnosticsParams(uri, Nil, ProfileNames.AMF))

}

case class DiagnosticNotificationsKind(kind: String)

object PARSING_BEFORE extends DiagnosticNotificationsKind("PARSING_BEFORE")
object ALL_TOGETHER   extends DiagnosticNotificationsKind("ALL_TOGETHER")

@JSExport
object DiagnosticNotificationsKind {
  def parsingBefore: DiagnosticNotificationsKind = PARSING_BEFORE
  def allTogether: DiagnosticNotificationsKind   = ALL_TOGETHER
}
