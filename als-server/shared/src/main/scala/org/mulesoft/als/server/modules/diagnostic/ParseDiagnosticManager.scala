package org.mulesoft.als.server.modules.diagnostic

import amf.core.client.common.validation.{ProfileName, ProfileNames}
import amf.core.client.scala.validation.AMFValidationResult
import org.mulesoft.als.logger.Logger
import org.mulesoft.als.server.client.platform.ClientNotifier
import org.mulesoft.als.server.modules.ast._
import org.mulesoft.amfintegration.amfconfiguration.AmfParseResult
import org.mulesoft.lsp.feature.link.DocumentLink
import org.mulesoft.lsp.feature.telemetry.MessageTypes

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ParseDiagnosticManager(
    override protected val clientNotifier: ClientNotifier,
    override protected val validationGatherer: ValidationGatherer,
    override protected val optimizationKind: DiagnosticNotificationsKind
) extends BaseUnitListener
    with DiagnosticManager {
  override val managerName: DiagnosticManagerKind = ParserDiagnosticKind

  /** Called on new AST available
    *
    * @param params
    *   \- (AST, References)
    * @param uuid
    *   \- telemetry UUID
    */
  override def onNewAst(params: BaseUnitListenerParams, uuid: String): Future[Unit] = synchronized {
    val parsedResult = params.parseResult
    val references =
      (if (params.tree)
         projectReferences(params.parseResult.uri, params.parseResult.context.state.projectState.projectErrors)
       else
         Map.empty) ++
        params.locationLinks
    Logger.debug(s"Got new AST: ${parsedResult.result.baseUnit.id}", "ParseDiagnosticManager", "newASTAvailable")
    val uri = parsedResult.location
    Logger.timeProcess(
      "Start report",
      MessageTypes.BEGIN_DIAGNOSTIC_PARSE,
      MessageTypes.END_DIAGNOSTIC_PARSE,
      "ParseDiagnosticManager : onNewAst",
      uri,
      innerGatherValidations(uuid, parsedResult, references, uri),
      uuid
    )
  }

  private def innerGatherValidations(
      uuid: String,
      parsedResult: AmfParseResult,
      references: Map[String, Seq[DocumentLink]],
      uri: String
  )() =
    gatherValidationErrors(parsedResult, references, uuid) recoverWith { case exception: Exception =>
      Logger.error("Error on validation: " + exception.getMessage, "ParseDiagnosticManager", "newASTAvailable")
      Future {
        clientNotifier.notifyDiagnostic(ValidationReport(uri, Set.empty, ProfileNames.AMF).publishDiagnosticsParams)
      }
    }

  private def gatherValidationErrors(
      result: AmfParseResult,
      references: Map[String, Seq[DocumentLink]],
      uuid: String
  ): Future[Unit] = {
    val profile: ProfileName = profileName(result.result.baseUnit)
    validationGatherer.indexNewReport(
      ErrorsWithTree(result.location, filteredResults(result).map(new AlsValidationResult(_)), Option(result.tree)),
      managerName,
      uuid
    )
    if (notifyParsing) notifyReport(result.location, result.result.baseUnit, references, managerName, profile)
    Future.unit
  }

  private def filteredResults(parseResult: AmfParseResult): Seq[AMFValidationResult] =
    parseResult.result.results
      .filterNot(specNotFoundForIsolated(_, parseResult))

  private def specNotFoundForIsolated(r: AMFValidationResult, parseResult: AmfParseResult): Boolean =
    isSpecNotFound(r) && !isMainFile(r, parseResult)

  // if there is a location and there is a main file defined, assert they are the same, it there is no main file, return true
  private def isMainFile(r: AMFValidationResult, parseResult: AmfParseResult) =
    r.location
      .exists(location =>
        parseResult.context.state.projectState.config.mainFile
          .map(mf => concatUri(parseResult.context.state.projectState.config.folder, mf))
          .contains(location)
      )

  private def concatUri(workspaceFolder: String, mainFile: String): String = {
    if (workspaceFolder.endsWith("/")) workspaceFolder.concat(mainFile)
    else s"$workspaceFolder/$mainFile"
  }

  private def isSpecNotFound(r: AMFValidationResult) =
    r.validationId == DiagnosticConstants.specNotFoundCode

  override def onRemoveFile(uri: String): Unit = {
    validationGatherer.removeFile(uri, managerName)
    if (notifyParsing) clientNotifier.notifyDiagnostic(AlsPublishDiagnosticsParams(uri, Nil, ProfileNames.AMF))
  }
}

object DiagnosticConstants {
  final val specNotFoundCode = "http://a.ml/vocabularies/amf/parser#couldnt-guess-root"
}
