package org.mulesoft.als.server

import io.scalajs.nodejs.console
import org.mulesoft.als.client.convert.LspConvertersClientToShared._
import org.mulesoft.als.client.convert.LspConvertersSharedToClient._
import org.mulesoft.als.client.lsp.configuration.{ClientInitializeParams, ClientInitializeResult}
import org.mulesoft.als.client.lsp.feature.completion.{ClientCompletionItem, ClientCompletionList, ClientCompletionParams}
import org.mulesoft.als.client.lsp.textsync.{ClientDidChangeTextDocumentParams, ClientDidOpenTextDocumentParams}
import org.mulesoft.als.vscode.{RequestHandler => ClientRequestHandler, RequestHandler0 => ClientRequestHandler0, _}
import org.mulesoft.lsp.feature.completion.CompletionRequestType
import org.mulesoft.lsp.feature.{RequestHandler, RequestType}
import org.mulesoft.lsp.server.LanguageServer

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.annotation.JSExport
import scala.scalajs.js.|

@JSExport
class AlsConnection(private val protocolConnection: ProtocolConnection, private val inner: LanguageServer) {
  protocolConnection.listen()

  private def resolveHandler[P, R](`type`: RequestType[P, R]): RequestHandler[P, R] = {
    val maybeHandler = inner.resolveHandler(`type`)
    if (maybeHandler.isEmpty) throw new UnsupportedOperationException else maybeHandler.get
  }

  val initializeHandlerJs: js.Function2[ClientInitializeParams, CancellationToken, Thenable[ClientInitializeResult]] = (param: ClientInitializeParams, _: CancellationToken) =>
    inner.initialize(param.toShared)
      .map(_.toClient)
      .toJSPromise
      .asInstanceOf[Thenable[ClientInitializeResult]]

  protocolConnection.onRequest(InitializeRequest.`type`, initializeHandlerJs.asInstanceOf[ClientRequestHandler[ClientInitializeParams, ClientInitializeResult, js.Any]])

  val initializedHandlerJs: js.Function2[js.Any, CancellationToken, Unit] = (_: js.Any, _: CancellationToken) =>
    inner.initialized()

  protocolConnection.onNotification(InitializedNotification.`type`, initializedHandlerJs.asInstanceOf[NotificationHandler[js.Any]])

  val exitHandlerJs: js.Function1[CancellationToken, Unit] = (_: CancellationToken) => inner.exit()

  protocolConnection.onNotification(ExitNotification.`type`, exitHandlerJs.asInstanceOf[NotificationHandler0])

  val shutdownHandlerJs: js.Function1[CancellationToken, js.Any] = (_: CancellationToken) => inner.shutdown()

  protocolConnection.onRequest(ShutdownRequest.`type`, shutdownHandlerJs.asInstanceOf[ClientRequestHandler0[js.Any, js.Any]])

  val onDidChangeHandlerJs: js.Function2[ClientDidChangeTextDocumentParams, CancellationToken, Unit] = (param: ClientDidChangeTextDocumentParams, _: CancellationToken) =>
    inner.textDocumentSyncConsumer.didChange(param.toShared)

  protocolConnection.onNotification(DidChangeTextDocumentNotification.`type`, onDidChangeHandlerJs.asInstanceOf[NotificationHandler[ClientDidChangeTextDocumentParams]])

  val onDidOpenHandlerJs: js.Function2[ClientDidOpenTextDocumentParams, CancellationToken, Unit] = (param: ClientDidOpenTextDocumentParams, _: CancellationToken) =>
    inner.textDocumentSyncConsumer.didOpen(param.toShared)

  protocolConnection.onNotification(DidOpenTextDocumentNotification.`type`, onDidOpenHandlerJs.asInstanceOf[NotificationHandler[ClientDidOpenTextDocumentParams]])

  val onCompletionHandlerJs: js.Function2[ClientCompletionParams, CancellationToken, Thenable[ClientCompletionList | js.Array[ClientCompletionItem]]] = (param: ClientCompletionParams, _: CancellationToken) =>
    resolveHandler(CompletionRequestType)(param.toShared)
      .map(_.toClient)
      .toJSPromise
      .asInstanceOf[Thenable[ClientCompletionList | js.Array[ClientCompletionItem]]]

  protocolConnection.onRequest(CompletionRequest.`type`, onCompletionHandlerJs.asInstanceOf[ClientRequestHandler[ClientCompletionParams, ClientCompletionList | js.Array[ClientCompletionItem], js.Any]])

}
