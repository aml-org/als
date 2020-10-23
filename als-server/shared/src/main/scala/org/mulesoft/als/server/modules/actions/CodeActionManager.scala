package org.mulesoft.als.server.modules.actions

import java.util.UUID

import org.mulesoft.als.actions.codeactions.plugins.base.CodeActionFactory
import org.mulesoft.als.actions.codeactions.plugins.base.CodeActionParamsImpl.CodeActionParamsImpl
import org.mulesoft.als.common.DirectoryResolver
import org.mulesoft.als.common.edits.codeaction.AbstractCodeAction
import org.mulesoft.als.configuration.AlsConfigurationReader
import org.mulesoft.als.server.RequestModule
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.workspace.WorkspaceManager
import org.mulesoft.amfintegration.AmfInstance
import org.mulesoft.lsp.feature.TelemeteredRequestHandler
import org.mulesoft.lsp.feature.codeactions._
import org.mulesoft.lsp.feature.telemetry.MessageTypes.MessageTypes
import org.mulesoft.lsp.feature.telemetry.{MessageTypes, TelemetryProvider}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CodeActionManager(allActions: Seq[CodeActionFactory],
                        workspaceManager: WorkspaceManager,
                        configuration: AlsConfigurationReader,
                        telemetryProvider: TelemetryProvider,
                        amfInstance: AmfInstance,
                        private val logger: Logger,
                        directoryResolver: DirectoryResolver)
    extends RequestModule[CodeActionCapabilities, CodeActionOptions] {

  /**
    * actually used actions, filtered against client provided kinds
    */
  private var usedActions: Seq[CodeActionFactory] = Nil

  override def getRequestHandlers: Seq[TelemeteredRequestHandler[_, _]] = Seq(
    new TelemeteredRequestHandler[CodeActionParams, Seq[CodeAction]] {
      override def `type`: CodeActionRequestType.type = CodeActionRequestType

      /**
        * check which actions apply to the request, and respond accordingly
        * @param params client request params
        * @return used actions result list
        */
      override def task(params: CodeActionParams): Future[Seq[CodeAction]] = {
        val uuid = UUID.randomUUID().toString
        for {
          (bu, allr) <- workspaceManager.getRelationships(params.textDocument.uri, uuid)
          results: Seq[Seq[AbstractCodeAction]] <- {
            val requestParams = params.toRequestParams(
              bu.unit,
              bu.tree,
              bu.yPartBranch,
              bu.definedBy,
              configuration,
              allr,
              telemetryProvider,
              uuid,
              amfInstance,
              directoryResolver
            )
            Future.sequence {
              usedActions
                .map(_(requestParams))
                .filter(_.isApplicable)
                .map(_.run(requestParams))
            }
          }
        } yield {
          val flatResults = results.flatten
          val finalResults = {
            if (!configuration.supportsDocumentChanges)
              flatResults.filterNot(_.needsWorkspaceEdit)
            else flatResults
          }
          finalResults.map(_.toCodeAction(configuration.supportsDocumentChanges))
        }
      }

      override protected def telemetry: TelemetryProvider = telemetryProvider

      override protected def code(params: CodeActionParams): String = "CodeActionsManager"

      override protected def beginType(params: CodeActionParams): MessageTypes = MessageTypes.BEGIN_CODE_ACTION

      override protected def endType(params: CodeActionParams): MessageTypes = MessageTypes.END_CODE_ACTION

      override protected def msg(params: CodeActionParams): String =
        s"""Requested code action for ${params.textDocument.uri} at ${params.range}
           |availableActions: $usedActions
           |supports documentChanges: ${configuration.supportsDocumentChanges}""".stripMargin

      override protected def uri(params: CodeActionParams): String = params.textDocument.uri
    }
  )

  override val `type`: CodeActionConfigType.type = CodeActionConfigType

  /**
    * filter actions in regards to client supported kinds
    * if config is Some(Empty) then no action will be used
    * if config is None, then no filter will be used (ergo, all actions enabled)
    *
    * @param config all client supported kinds
    * @return all server supported kinds
    */
  override def applyConfig(config: Option[CodeActionCapabilities]): CodeActionOptions = {
    config match {
      case Some(c) =>
        usedActions = allActions
          .filter(a => c.codeActionLiteralSupport.forall(_.codeActionKind.valueSet.contains(a.kind)))
      case None => usedActions = allActions
    }
    logger.debug(s"actions to be used:\n${usedActions.map(_.title).mkString("\n")}",
                 "CodeActionManager",
                 "applyConfig")
    logger.debug(s"supports documentChanges: ${configuration.supportsDocumentChanges}",
                 "CodeActionManager",
                 "applyConfig")
    CodeActionRegistrationOptions(Some(allActions.map(_.kind).distinct))
  }

  override def initialize(): Future[Unit] = Future.successful()
}
