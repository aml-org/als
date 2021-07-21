package org.mulesoft.als.server.modules.diagnostic

import amf._
import amf.internal.environment.Environment
import org.mulesoft.als.server.client.ClientNotifier
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.modules.ast._
import org.mulesoft.amfintegration.{AmfParseResult, DiagnosticsBundle}
import org.mulesoft.lsp.feature.telemetry.{MessageTypes, TelemetryProvider}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

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
  override def onNewAst(tuple: BaseUnitListenerParams, uuid: String): Unit =
    try {
      val parsedResult = tuple.parseResult
      val references   = tuple.diagnosticsBundle
      logger.debug("Got new AST:\n" + parsedResult.baseUnit.id, "ParseDiagnosticManager", "newASTAvailable")
      val uri = parsedResult.location
      telemetryProvider.timeProcess(
        "Start report",
        MessageTypes.BEGIN_DIAGNOSTIC_PARSE,
        MessageTypes.END_DIAGNOSTIC_PARSE,
        "ParseDiagnosticManager : onNewAst",
        uri,
        innerGatherValidations(uuid, parsedResult, references, uri),
        uuid
      )
    } catch {
      case e: Exception =>
        logger.error(e.getMessage, "ParseDiagnosticManager", "newASTAvailable")
    }

  private def innerGatherValidations(uuid: String,
                                     parsedResult: AmfParseResult,
                                     references: Map[String, DiagnosticsBundle],
                                     uri: String)() =
    gatherValidationErrors(parsedResult, references, uuid) recoverWith {
      case exception: Exception =>
        logger.error("Error on validation: " + exception.toString, "ParseDiagnosticManager", "newASTAvailable")
        Future {
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
