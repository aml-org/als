package org.mulesoft.als.server.modules.diagnostic

import amf.core.client.common.validation.{ProfileName, ProfileNames}
import amf.core.client.scala.model.document.BaseUnit
import amf.core.client.scala.validation.AMFValidationReport
import org.mulesoft.als.logger.Logger
import org.mulesoft.als.server.client.ClientNotifier
import org.mulesoft.als.server.modules.ast._
import org.mulesoft.als.server.modules.common.reconciler.Runnable
import org.mulesoft.amfintegration.AmfImplicits._
import org.mulesoft.amfintegration.{AmfResolvedUnit, DiagnosticsBundle}
import org.mulesoft.lsp.feature.telemetry.{MessageTypes, TelemetryProvider}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success}

class ResolutionDiagnosticManager(override protected val telemetryProvider: TelemetryProvider,
                                  override protected val clientNotifier: ClientNotifier,
                                  override protected val logger: Logger,
                                  override protected val validationGatherer: ValidationGatherer)
    extends ResolvedUnitListener
    with DiagnosticManager {
  type RunType = ValidationRunnable
  override val managerName: DiagnosticManagerKind = ResolutionDiagnosticKind
  protected override def runnable(ast: AmfResolvedUnit, uuid: String): ValidationRunnable = {
    logger.debug(s"Add runnable ${ast.baseUnit.identifier}", "ResolutionDiagnosticManager", "runnable")
    new ValidationRunnable(ast.baseUnit.identifier, ast, uuid)
  }

  protected override def onNewAstPreprocess(resolved: AmfResolvedUnit, uuid: String): Unit =
    logger.debug("Got new AST:\n" + resolved.baseUnit.id, "ResolutionDiagnosticManager", "newASTAvailable")

  protected override def onFailure(uuid: String, uri: String, exception: Throwable): Unit = {
    logger.error(s"Error on validation: ${exception.toString}", "ResolutionDiagnosticManager", "newASTAvailable")
    clientNotifier.notifyDiagnostic(ValidationReport(uri, Set.empty, ProfileNames.AMF).publishDiagnosticsParams)
  }

  protected override def onSuccess(uuid: String, uri: String): Unit =
    logger.debug(s"End report: $uuid", "ResolutionDiagnosticManager", "newASTAvailable")

  private def gatherValidationErrors(uri: String,
                                     resolved: AmfResolvedUnit,
                                     references: Map[String, DiagnosticsBundle],
                                     uuid: String): Future[Unit] = {
    val startTime = System.currentTimeMillis()

    val profile = profileName(resolved.baseUnit)
    this
      .report(uri, telemetryProvider, resolved, uuid, profile)
      .map(report => {
        val endTime = System.currentTimeMillis()
        validationGatherer
          .indexNewReport(
            ErrorsWithTree(uri, report.results.map(new AlsValidationResult(_)), Some(tree(resolved.baseUnit))),
            managerName,
            uuid)
        notifyReport(uri, resolved.baseUnit, references, managerName, profile)

        this.logger.debug(s"It took ${endTime - startTime} milliseconds to validate",
                          "ResolutionDiagnosticManager",
                          "gatherValidationErrors")
      })
  }

  private def tree(baseUnit: BaseUnit): Set[String] =
    baseUnit.flatRefs
      .map(bu => bu.identifier)
      .toSet + baseUnit.identifier

  private def report(uri: String,
                     telemetryProvider: TelemetryProvider,
                     resolved: AmfResolvedUnit,
                     uuid: String,
                     profile: ProfileName): Future[AMFValidationReport] = {
    telemetryProvider.timeProcess(
      "AMF report",
      MessageTypes.BEGIN_REPORT,
      MessageTypes.END_REPORT,
      "ResolutionDiagnosticManager - resolution diagnostics",
      uri,
      tryValidationReport(uri, telemetryProvider, resolved, uuid, profile),
      uuid
    )

  }

  private def tryValidationReport(uri: String,
                                  telemetryProvider: TelemetryProvider,
                                  resolved: AmfResolvedUnit,
                                  uuid: String,
                                  profile: ProfileName)() =
    try {
      resolved.getLast.flatMap { r =>
        r.resolvedUnit
          .flatMap { result =>
            r.configuration
              .report(result.baseUnit)
              .map(rep => AMFValidationReport(rep.model, rep.profile, rep.results ++ result.results))
          }
      } recoverWith {
        case e: Exception =>
          sendFailedClone(uri, telemetryProvider, resolved.baseUnit, uuid, e.getMessage)
      }
    } catch {
      case e: Exception =>
        sendFailedClone(uri, telemetryProvider, resolved.baseUnit, uuid, e.getMessage)
    }

  override def onRemoveFile(uri: String): Unit = {
    validationGatherer.removeFile(uri, managerName)
    clientNotifier.notifyDiagnostic(AlsPublishDiagnosticsParams(uri, Nil, ProfileNames.AMF))
  }

  class ValidationRunnable(var uri: String, ast: AmfResolvedUnit, uuid: String) extends Runnable[Unit] {
    private var canceled = false

    private val kind = "ValidationRunnable"

    def run(): Promise[Unit] = {
      val promise = Promise[Unit]()

      logger.debug("Running", "ResolutionDiagnosticManager", "run")

      def innerRunGather(): Future[Unit] =
        gatherValidationErrors(ast.baseUnit.identifier, ast, ast.diagnosticsBundle, uuid) andThen {
          case Success(report) => promise.success(report)

          case Failure(error) => promise.failure(error)
        }

      telemetryProvider.timeProcess(
        "End report",
        MessageTypes.BEGIN_DIAGNOSTIC_RESOLVED,
        MessageTypes.END_DIAGNOSTIC_RESOLVED,
        "ResolutionDiagnosticManager : onNewAst",
        uri,
        innerRunGather,
        uuid
      )

      promise
    }

    def conflicts(other: Runnable[Any]): Boolean =
      other.asInstanceOf[ValidationRunnable].kind == kind && uri == other
        .asInstanceOf[ValidationRunnable]
        .uri

    def cancel(): Unit = {
      canceled = true
    }

    def isCanceled(): Boolean = canceled
  }

}
