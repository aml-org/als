package org.mulesoft.als.server.modules.diagnostic

import amf._
import amf.internal.environment.Environment
import org.mulesoft.als.server.client.ClientNotifier
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.modules.ast._
import org.mulesoft.amfintegration.DiagnosticsBundle
import org.mulesoft.amfmanager.AmfParseResult
import org.mulesoft.lsp.feature.telemetry.{MessageTypes, TelemetryProvider}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

class ParseDiagnosticManager(override protected val telemetryProvider: TelemetryProvider,
                             override protected val clientNotifier: ClientNotifier,
                             override protected val logger: Logger,
                             override protected val env: Environment,
                             override protected val validationGatherer: ValidationGatherer,
                             override protected val optimizationKind: DiagnosticNotificationsKind)
    extends BaseUnitListener
    with DiagnosticManager {
  override val managerName: DiagnosticManagerKind = ParserDiagnosticKind

  /**
    * Called on new AST available
    *
    * @param tuple - (AST, References)
    * @param uuid  - telemetry UUID
    */
  override def onNewAst(tuple: BaseUnitListenerParams, uuid: String): Unit = {
    val parsedResult = tuple.parseResult
    val references   = tuple.diagnosticsBundle
    logger.debug("Got new AST:\n" + parsedResult.baseUnit.id, "ValidationManager", "newASTAvailable")
    val uri = parsedResult.location
    telemetryProvider.addTimedMessage("Start report",
                                      "DiagnosticManager",
                                      "onNewAst",
                                      MessageTypes.BEGIN_DIAGNOSTIC,
                                      uri,
                                      uuid)

    gatherValidationErrors(parsedResult, references, uuid) andThen {
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

  private def gatherValidationErrors(result: AmfParseResult,
                                     references: Map[String, DiagnosticsBundle],
                                     uuid: String): Future[Unit] = {
    val profile: ProfileName = profileName(result.baseUnit)
    validationGatherer.indexNewReport(ErrorsWithTree(result.location, result.eh.getErrors, Option(result.tree)),
                                      managerName,
                                      uuid)
    if (notifyParsing) notifyReport(result.location, result.baseUnit, references, managerName, profile)
    Future.unit
  }

  override def onRemoveFile(uri: String): Unit = {
    validationGatherer.removeFile(uri, managerName)
    if (notifyParsing) clientNotifier.notifyDiagnostic(AlsPublishDiagnosticsParams(uri, Nil, ProfileNames.AMF))
  }
}
