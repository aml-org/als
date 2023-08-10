package org.mulesoft.als.server.modules.diagnostic

import amf.core.client.common.validation.ProfileName
import amf.core.client.common.validation.SeverityLevels.VIOLATION
import amf.core.client.scala.model.document.{BaseUnit, ExternalFragment}
import amf.core.client.scala.validation.{AMFValidationReport, AMFValidationResult}
import org.mulesoft.als.logger.Logger
import org.mulesoft.als.server.ClientNotifierModule
import org.mulesoft.als.server.client.platform.ClientNotifier
import org.mulesoft.amfintegration.AmfImplicits.BaseUnitImp
import org.mulesoft.amfintegration.amfconfiguration.ProfileMatcher
import org.mulesoft.lsp.ConfigType
import org.mulesoft.lsp.feature.diagnostic.{DiagnosticClientCapabilities, DiagnosticConfigType}
import org.mulesoft.lsp.feature.link.DocumentLink
import org.mulesoft.lsp.feature.telemetry.TelemetryProvider

import scala.concurrent.Future

trait DiagnosticManager extends BasicDiagnosticManager[DiagnosticClientCapabilities, Unit] {
  override val `type`: ConfigType[DiagnosticClientCapabilities, Unit] =
    DiagnosticConfigType

  override def applyConfig(config: Option[DiagnosticClientCapabilities]): Unit = {
    // not used
  }

  def projectReferences(uri: String, projectErrors: Seq[AMFValidationResult]): Map[String, Seq[DocumentLink]] =
    projectErrors
      .map(v => {
        v.location.getOrElse(uri) -> Seq.empty
      })
      .toMap
}

trait BasicDiagnosticManager[C, S] extends ClientNotifierModule[C, S] {

  protected val validationGatherer: ValidationGatherer
  protected val telemetryProvider: TelemetryProvider
  protected val optimizationKind: DiagnosticNotificationsKind = ALL_TOGETHER
  protected val notifyParsing: Boolean                        = optimizationKind == PARSING_BEFORE

  override def initialize(): Future[Unit] = Future.successful()

  protected def profileName(baseUnit: BaseUnit): ProfileName =
    ProfileMatcher.profile(baseUnit)

  protected val clientNotifier: ClientNotifier

  protected val managerName: DiagnosticManagerKind

  protected def sendFailedClone(
      uri: String,
      telemetryProvider: TelemetryProvider,
      baseUnit: BaseUnit,
      uuid: String,
      e: String
  ): Future[AMFValidationReport] = {
    val msg =
      s"DiagnosticManager suffered an unexpected error while validating: $e"
    Logger.warning(msg, "DiagnosticManager", "report")
    Future.successful(failedReportDiagnostic(msg, baseUnit))
  }

  private final def failedReportDiagnostic(msg: String, baseUnit: BaseUnit): AMFValidationReport =
    AMFValidationReport(
      "",
      profileName(baseUnit),
      Seq(AMFValidationResult(msg, VIOLATION, "", None, "", None, baseUnit.location(), None))
    )

  protected def notifyReport(
      uri: String,
      baseUnit: BaseUnit,
      references: Map[String, Seq[DocumentLink]],
      step: DiagnosticManagerKind,
      profile: ProfileName
  ): Unit = {
    val allReferences: Set[String] = references.keySet ++ baseUnit.flatRefs.map(_.identifier) + uri
    val errors =
      DiagnosticConverters.buildIssueResults(
        validationGatherer
          .merged()
          .filter(v => allReferences.contains(v._1)),
        references,
        profile,
        (
          baseUnit +: baseUnit.flatRefs
        ).map(bu => bu.identifier -> bu.isInstanceOf[ExternalFragment]).toMap
      )
    Logger.debug(
      s"Number of ${step.name} errors is:\n" + errors.flatMap(_.issues).length,
      "ValidationManager",
      "newASTAvailable"
    )
    errors.foreach(r => clientNotifier.notifyDiagnostic(r.publishDiagnosticsParams))
  }
}
