package org.mulesoft.als.server.modules.diagnostic.custom

import org.mulesoft.als.server.client.ClientNotifier
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.modules.ast.ResolvedUnitListener
import org.mulesoft.als.server.modules.diagnostic.{
  CustomDiagnosticKind,
  DiagnosticManager,
  DiagnosticManagerKind,
  ValidationGatherer
}
import org.mulesoft.amfintegration.AmfImplicits.BaseUnitImp
import org.mulesoft.amfintegration.{AmfResolvedUnit, DiagnosticsBundle}
import org.mulesoft.lsp.feature.telemetry.{MessageTypes, TelemetryProvider}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success}
class CustomValidationManager(override protected val telemetryProvider: TelemetryProvider,
                              override protected val clientNotifier: ClientNotifier,
                              override protected val logger: Logger,
                              override protected val validationGatherer: ValidationGatherer,
                              val platformValidator: AMFOpaValidator)
    extends DiagnosticManager
    with ResolvedUnitListener {
  override protected val managerName: DiagnosticManagerKind = CustomDiagnosticKind
  override type RunType = CustomValidationRunnable

  private def gatherValidationErrors(uri: String,
                                     resolved: AmfResolvedUnit,
                                     references: Map[String, DiagnosticsBundle],
                                     uuid: String): Future[Unit] = {
    val startTime = System.currentTimeMillis()
    resolved.workspaceConfiguration.map(workspaceConfig => {
      for {
        unit <- resolved.resolvedUnit

      }
    })
  }

  class CustomValidationRunnable(var uri: String, ast: AmfResolvedUnit, uuid: String) extends Runnable[Unit] {
    private var canceled = false

    private val kind = "CustomValidationRunnable"

    def run(): Promise[Unit] = {
      val promise = Promise[Unit]()

      def innerRunGather() =
        gatherValidationErrors(ast.originalUnit.identifier, ast, ast.diagnosticsBundle, uuid) andThen {
          case Success(report) => promise.success(report)
          case Failure(error)  => promise.failure(error)
        }

      telemetryProvider.timeProcess(
        "End report",
        MessageTypes.BEGIN_CUSTOM_DIAGNOSTIC,
        MessageTypes.END_CUSTOM_DIAGNOSTIC,
        s"CustomValidationRunnable : gatherValidationErrors for ${ast.originalUnit.identifier}",
        uri,
        innerRunGather,
        uuid
      )

      promise
    }

    def conflicts(other: Runnable[Any]): Boolean =
      other.asInstanceOf[CustomValidationRunnable].kind == kind && uri == other
        .asInstanceOf[CustomValidationRunnable]
        .uri

    def cancel() {
      canceled = true
    }

    def isCanceled(): Boolean = canceled
  }
}
