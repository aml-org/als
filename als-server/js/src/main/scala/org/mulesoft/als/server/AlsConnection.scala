package org.mulesoft.als.server

import org.mulesoft.als.client.convert.LspConvertersClientToShared._
import org.mulesoft.als.client.convert.LspConvertersSharedToClient._
import org.mulesoft.als.client.lsp.common.{ClientLocation, ClientLocationLink, ClientTextDocumentPositionParams}
import org.mulesoft.als.client.lsp.configuration.{ClientInitializeParams, ClientInitializeResult}
import org.mulesoft.als.client.lsp.feature.completion.{
  ClientCompletionItem,
  ClientCompletionList,
  ClientCompletionParams
}
import org.mulesoft.als.client.lsp.feature.documentsymbol.{
  ClientDocumentSymbol,
  ClientDocumentSymbolParams,
  ClientSymbolInformation
}
import org.mulesoft.als.client.lsp.feature.link.{ClientDocumentLink, ClientDocumentLinkParams}
import org.mulesoft.als.client.lsp.feature.reference.ClientReferenceParams
import org.mulesoft.als.client.lsp.textsync.{ClientDidChangeTextDocumentParams, ClientDidOpenTextDocumentParams}
import org.mulesoft.als.client.lsp.workspace.ClientExecuteCommandParams
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.vscode.{RequestHandler => ClientRequestHandler, RequestHandler0 => ClientRequestHandler0, _}
import org.mulesoft.lsp.feature.completion.CompletionRequestType
import org.mulesoft.lsp.feature.definition.DefinitionRequestType
import org.mulesoft.lsp.feature.diagnostic.PublishDiagnosticsParams
import org.mulesoft.lsp.feature.documentsymbol.DocumentSymbolRequestType
import org.mulesoft.lsp.feature.link.DocumentLinkRequestType
import org.mulesoft.lsp.feature.reference.ReferenceRequestType
import org.mulesoft.lsp.feature.{RequestHandler, RequestType}
import org.mulesoft.lsp.server.LanguageServer

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.annotation.JSExport
import scala.scalajs.js.|

@JSExport
class AlsConnection(private val protocolConnection: ProtocolConnection,
                    private val inner: LanguageServer,
                    private val logger: Logger) {
  protocolConnection.listen()

  private def resolveHandler[P, R](`type`: RequestType[P, R]): RequestHandler[P, R] = {
    val maybeHandler = inner.resolveHandler(`type`)
    if (maybeHandler.isEmpty) throw new UnsupportedOperationException else maybeHandler.get
  }

  val initializeHandlerJs: js.Function2[ClientInitializeParams, CancellationToken, Thenable[ClientInitializeResult]] =
    (param: ClientInitializeParams, _: CancellationToken) =>
      inner
        .initialize(param.toShared)
        .map(_.toClient)
        .toJSPromise
        .asInstanceOf[Thenable[ClientInitializeResult]]

  protocolConnection.onRequest(
    InitializeRequest.`type`,
    initializeHandlerJs.asInstanceOf[ClientRequestHandler[ClientInitializeParams, ClientInitializeResult, js.Any]])

  val initializedHandlerJs: js.Function2[js.Any, CancellationToken, Unit] = (_: js.Any, _: CancellationToken) =>
    inner.initialized()

  protocolConnection.onNotification(InitializedNotification.`type`,
                                    initializedHandlerJs.asInstanceOf[NotificationHandler[js.Any]])

  val exitHandlerJs: js.Function1[CancellationToken, Unit] = (_: CancellationToken) => inner.exit()

  protocolConnection.onNotification(ExitNotification.`type`, exitHandlerJs.asInstanceOf[NotificationHandler0])

  val shutdownHandlerJs: js.Function1[CancellationToken, js.Any] = (_: CancellationToken) => inner.shutdown()

  protocolConnection.onRequest(ShutdownRequest.`type`,
                               shutdownHandlerJs.asInstanceOf[ClientRequestHandler0[js.Any, js.Any]])

  val onDidChangeHandlerJs: js.Function2[ClientDidChangeTextDocumentParams, CancellationToken, Unit] =
    (param: ClientDidChangeTextDocumentParams, _: CancellationToken) =>
      inner.textDocumentSyncConsumer.didChange(param.toShared)

  protocolConnection.onNotification(
    DidChangeTextDocumentNotification.`type`,
    onDidChangeHandlerJs.asInstanceOf[NotificationHandler[ClientDidChangeTextDocumentParams]])

  val onDidOpenHandlerJs: js.Function2[ClientDidOpenTextDocumentParams, CancellationToken, Unit] =
    (param: ClientDidOpenTextDocumentParams, _: CancellationToken) =>
      inner.textDocumentSyncConsumer.didOpen(param.toShared)

  protocolConnection.onNotification(
    DidOpenTextDocumentNotification.`type`,
    onDidOpenHandlerJs.asInstanceOf[NotificationHandler[ClientDidOpenTextDocumentParams]])

  val onCompletionHandlerJs: js.Function2[ClientCompletionParams,
                                          CancellationToken,
                                          Thenable[ClientCompletionList | js.Array[ClientCompletionItem]]] =
    (param: ClientCompletionParams, _: CancellationToken) =>
      resolveHandler(CompletionRequestType)(param.toShared)
        .map(_.toClient)
        .toJSPromise
        .asInstanceOf[Thenable[ClientCompletionList | js.Array[ClientCompletionItem]]]

  protocolConnection.onRequest(
    CompletionRequest.`type`,
    onCompletionHandlerJs.asInstanceOf[
      ClientRequestHandler[ClientCompletionParams, ClientCompletionList | js.Array[ClientCompletionItem], js.Any]]
  )

  val onDocumentSymbolHandlerJs
    : js.Function2[ClientDocumentSymbolParams,
                   CancellationToken,
                   Thenable[js.Array[ClientDocumentSymbol] | js.Array[ClientSymbolInformation]]] =
    (param: ClientDocumentSymbolParams, _: CancellationToken) =>
      resolveHandler(DocumentSymbolRequestType)(param.toShared)
        .map(_.toClient)
        .toJSPromise
        .asInstanceOf[Thenable[js.Array[ClientDocumentSymbol] | js.Array[ClientSymbolInformation]]]

  protocolConnection.onRequest(
    DocumentSymbolRequest.`type`,
    onDocumentSymbolHandlerJs
      .asInstanceOf[ClientRequestHandler[ClientDocumentSymbolParams,
                                         js.Array[ClientDocumentSymbol] | js.Array[ClientSymbolInformation],
                                         js.Any]]
  )

  // COMMAND
  val onExecuteCommandHandlerJs: js.Function2[ClientExecuteCommandParams, CancellationToken, Thenable[js.Any]] =
    (param: ClientExecuteCommandParams, _: CancellationToken) => {
      logger.debug(param.command, "AlsConnection", "CommandExecutor")
      inner.workspaceService
        .executeCommand(param.toShared)
        .map {
          case Some(validations: Seq[PublishDiagnosticsParams]) =>
            logger.debug(s"validations size: ${validations.size}", "AlsConnection", "CommandExecutor")
            validations.map(v => v.toClient).toJSArray
          // TODO: ALS-950: Return serialized JSON-LD
          case other => other
        }
        .toJSPromise
        .asInstanceOf[Thenable[js.Any]]
    }

  protocolConnection.onRequest(
    ExecuteCommandRequest.`type`,
    onExecuteCommandHandlerJs
      .asInstanceOf[ClientRequestHandler[ClientExecuteCommandParams, js.Any, js.Any]]
  )
  // End Command

  // DocumentLink
  val onDocumentLinkHandlerJs
    : js.Function2[ClientDocumentLinkParams, CancellationToken, Thenable[js.Array[ClientDocumentLink]]] =
    (param: ClientDocumentLinkParams, _: CancellationToken) =>
      resolveHandler(DocumentLinkRequestType)(param.toShared)
        .map(_.map(_.toClient).toJSArray)
        .toJSPromise
        .asInstanceOf[Thenable[js.Array[ClientDocumentLink]]]

  protocolConnection.onRequest(
    DocumentLinkRequest.`type`,
    onDocumentLinkHandlerJs
      .asInstanceOf[ClientRequestHandler[ClientDocumentLinkParams, js.Array[ClientDocumentLink], js.Any]]
  )
  // End DocumentLink

  // Definition
  val onDefinitionHandlerJs
    : js.Function2[ClientTextDocumentPositionParams,
                   CancellationToken,
                   Thenable[ClientLocation | js.Array[ClientLocation] | js.Array[ClientLocationLink]]] =
    (param: ClientTextDocumentPositionParams, _: CancellationToken) =>
      resolveHandler(DefinitionRequestType)(param.toShared)
        .map(_.toClient)
        .toJSPromise
        .asInstanceOf[Thenable[ClientLocation | js.Array[ClientLocation] | js.Array[ClientLocationLink]]]

  protocolConnection.onRequest(
    DefinitionRequest.`type`,
    onDefinitionHandlerJs
      .asInstanceOf[ClientRequestHandler[ClientTextDocumentPositionParams,
                                         ClientLocation | js.Array[ClientLocation] | js.Array[ClientLocationLink],
                                         js.Any]]
  )
  // End Definition

  // References
  val onReferencesHandlerJs
    : js.Function2[ClientReferenceParams, CancellationToken, Thenable[js.Array[ClientLocation]]] =
    (param: ClientReferenceParams, _: CancellationToken) =>
      resolveHandler(ReferenceRequestType)(param.toShared)
        .map(_.map(_.toClient).toJSArray)
        .toJSPromise
        .asInstanceOf[Thenable[js.Array[ClientLocation]]]

  protocolConnection.onRequest(
    ReferencesRequest.`type`,
    onReferencesHandlerJs
      .asInstanceOf[ClientRequestHandler[ClientReferenceParams, js.Array[ClientLocation], js.Any]]
  )
  // End References

}
