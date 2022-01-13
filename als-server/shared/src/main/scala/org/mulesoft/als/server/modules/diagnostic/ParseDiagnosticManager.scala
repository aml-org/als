package org.mulesoft.als.server.modules.diagnostic

import amf.core.client.common.validation.{ProfileName, ProfileNames}
import org.mulesoft.als.logger.Logger
import org.mulesoft.als.server.client.platform.ClientNotifier
import org.mulesoft.als.server.modules.ast._
import org.mulesoft.amfintegration.DiagnosticsBundle
import org.mulesoft.amfintegration.amfconfiguration.AmfParseResult
import org.mulesoft.lsp.feature.telemetry.{MessageTypes, TelemetryProvider}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ParseDiagnosticManager(override protected val telemetryProvider: TelemetryProvider,
                             override protected val clientNotifier: ClientNotifier,
                             override protected val logger: Logger,
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
  override def onNewAst(tuple: BaseUnitListenerParams, uuid: String): Future[Unit] = synchronized {
    val parsedResult = tuple.parseResult
    val references   = tuple.diagnosticsBundle
    logger.debug("Got new AST:\n" + parsedResult.result.baseUnit.id, "ParseDiagnosticManager", "newASTAvailable")
    val uri = parsedResult.location
    if (tuple.tree) {
      val projectErrors          = tuple.parseResult.context.state.projectState.projectErrors
      val locations: Set[String] = projectErrors.flatMap(_.location).toSet

      validationGatherer.indexNewReport(
        ErrorsWithTree(uri, projectErrors.map(new AlsValidationResult(_)), Some(locations ++ Set(uri))),
        ProjectDiagnosticKind,
        uuid
      )
    }
    telemetryProvider.timeProcess(
      "Start report",
      MessageTypes.BEGIN_DIAGNOSTIC_PARSE,
      MessageTypes.END_DIAGNOSTIC_PARSE,
      "ParseDiagnosticManager : onNewAst",
      uri,
      innerGatherValidations(uuid, parsedResult, references, uri),
      uuid
    )
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
    val profile: ProfileName = profileName(result.result.baseUnit)
    validationGatherer.indexNewReport(
      ErrorsWithTree(result.location, result.result.results.map(new AlsValidationResult(_)), Option(result.tree)),
      managerName,
      uuid)
    if (notifyParsing) notifyReport(result.location, result.result.baseUnit, references, managerName, profile)
    Future.unit
  }

  override def onRemoveFile(uri: String): Unit = {
    validationGatherer.removeFile(uri, managerName)
    if (notifyParsing) clientNotifier.notifyDiagnostic(AlsPublishDiagnosticsParams(uri, Nil, ProfileNames.AMF))
  }
}
