package org.mulesoft.als.server.modules.diagnostic

import amf._
import amf.core.model.document.BaseUnit
import amf.core.validation.SeverityLevels.VIOLATION
import amf.core.validation.{AMFValidationReport, AMFValidationResult}
import amf.internal.environment.Environment
import org.mulesoft.als.server.ClientNotifierModule
import org.mulesoft.als.server.client.ClientNotifier
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.amfintegration.AmfImplicits._
import org.mulesoft.amfintegration.{DiagnosticsBundle, ParserHelper}
import org.mulesoft.lsp.ConfigType
import org.mulesoft.lsp.feature.diagnostic.{DiagnosticClientCapabilities, DiagnosticConfigType}
import org.mulesoft.lsp.feature.telemetry.TelemetryProvider

import scala.concurrent.Future

trait DiagnosticManager extends ClientNotifierModule[DiagnosticClientCapabilities, Unit] {
  protected val env: Environment
  override val `type`: ConfigType[DiagnosticClientCapabilities, Unit] =
    DiagnosticConfigType

  override def applyConfig(config: Option[DiagnosticClientCapabilities]): Unit = {
    // not used
  }

  protected val validationGatherer: ValidationGatherer
  protected val telemetryProvider: TelemetryProvider
  protected val optimizationKind: DiagnosticNotificationsKind = ALL_TOGETHER
  protected val notifyParsing: Boolean                        = optimizationKind == PARSING_BEFORE
  protected val logger: Logger
  override def initialize(): Future[Unit] = Future.successful()

  protected def profileName(baseUnit: BaseUnit): ProfileName =
    ParserHelper.profile(baseUnit)

  protected val clientNotifier: ClientNotifier

  protected val managerName: DiagnosticManagerKind

  protected def sendFailedClone(uri: String,
                                telemetryProvider: TelemetryProvider,
                                baseUnit: BaseUnit,
                                uuid: String,
                                e: String): Future[AMFValidationReport] = {
    val msg =
      s"DiagnosticManager suffered an unexpected error while validating: $e"
    logger.warning(msg, "DiagnosticManager", "report")
    Future.successful(failedReportDiagnostic(msg, baseUnit))
  }

  private final def failedReportDiagnostic(msg: String, baseUnit: BaseUnit): AMFValidationReport =
    AMFValidationReport(conforms = false,
                        "",
                        profileName(baseUnit),
                        Seq(AMFValidationResult(msg, VIOLATION, "", None, "", None, baseUnit.location(), None)))

  protected def notifyReport(uri: String,
                             baseUnit: BaseUnit,
                             references: Map[String, DiagnosticsBundle],
                             step: DiagnosticManagerKind,
                             profile: ProfileName): Unit = {
    val allReferences: Set[String] = references.keySet ++ baseUnit.flatRefs.map(_.identifier) + uri
    val errors =
      DiagnosticConverters.buildIssueResults(
        validationGatherer
          .merged()
          .filter(v => allReferences.contains(v._1)),
        references,
        profile
      )
    logger.debug(s"Number of ${step.name} errors is:\n" + errors.flatMap(_.issues).length,
                 "ValidationManager",
                 "newASTAvailable")
    errors.foreach(r => clientNotifier.notifyDiagnostic(r.publishDiagnosticsParams))
  }
}
