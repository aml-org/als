package org.mulesoft.als.server

import org.mulesoft.als.server.feature.diagnostic.CleanDiagnosticTreeRequestType
import org.mulesoft.als.server.feature.fileusage.FileUsageRequestType
import org.mulesoft.als.server.feature.serialization.{ConversionRequestType, SerializationResult}
import org.mulesoft.als.server.feature.workspace.FilesInProjectParams
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.protocol.client.{AlsLanguageClient, AlsLanguageClientAware}
import org.mulesoft.als.server.protocol.configuration.{
  ClientAlsInitializeParams,
  ClientAlsInitializeResult,
  ClientUpdateConfigurationParams,
  ClientUpdateFormatOptionsParams
}
import org.mulesoft.als.server.protocol.convert.LspConvertersClientToShared._
import org.mulesoft.als.server.protocol.convert.LspConvertersSharedToClient._
import org.mulesoft.als.server.protocol.diagnostic.{
  ClientAlsPublishDiagnosticsParams,
  ClientCleanDiagnosticTreeParams,
  ClientFilesInProjectParams
}
import org.mulesoft.als.server.protocol.serialization.{
  ClientConversionParams,
  ClientSerializationParams,
  ClientSerializationResult,
  ClientSerializedDocument
}
import org.mulesoft.als.vscode.{RequestHandler => ClientRequestHandler, RequestHandler0 => ClientRequestHandler0, _}
import org.mulesoft.lsp.client.{LspLanguageClient, LspLanguageClientAware}
import org.mulesoft.lsp.convert.LspConvertersClientToShared._
import org.mulesoft.lsp.convert.LspConvertersSharedToClient._
import org.mulesoft.lsp.edit.ClientWorkspaceEdit
import org.mulesoft.lsp.feature.RequestHandler
import org.mulesoft.lsp.feature.common.{
  ClientLocation,
  ClientLocationLink,
  ClientRange,
  ClientTextDocumentIdentifier,
  ClientTextDocumentPositionParams
}
import org.mulesoft.lsp.feature.completion.{
  ClientCompletionItem,
  ClientCompletionList,
  ClientCompletionParams,
  CompletionRequestType
}
import org.mulesoft.lsp.feature.definition.{ClientDefinitionParams, DefinitionRequestType}
import org.mulesoft.lsp.feature.diagnostic.{ClientPublishDiagnosticsParams, PublishDiagnosticsParams}
import org.mulesoft.lsp.feature.documenthighlight.{ClientDocumentHighlight, ClientDocumentHighlightParams}
import org.mulesoft.lsp.feature.documentsymbol.{
  ClientDocumentSymbol,
  ClientDocumentSymbolParams,
  ClientSymbolInformation,
  DocumentSymbolRequestType
}
import org.mulesoft.lsp.feature.highlight.DocumentHighlightRequestType
import org.mulesoft.lsp.feature.implementation.{ClientImplementationParams, ImplementationRequestType}
import org.mulesoft.lsp.feature.link.{ClientDocumentLink, ClientDocumentLinkParams, DocumentLinkRequestType}
import org.mulesoft.lsp.feature.reference.{ClientReferenceParams, ReferenceRequestType}
import org.mulesoft.lsp.feature.rename.{
  ClientPrepareRenameParams,
  ClientPrepareRenameResult,
  ClientRenameParams,
  PrepareRenameRequestType,
  RenameRequestType
}
import org.mulesoft.lsp.feature.telemetry.{ClientTelemetryMessage, TelemetryMessage}
import org.mulesoft.lsp.feature.typedefinition.{ClientTypeDefinitionParams, TypeDefinitionRequestType}
import org.mulesoft.lsp.textsync.{
  ClientDidChangeTextDocumentParams,
  ClientDidCloseTextDocumentParams,
  ClientDidOpenTextDocumentParams
}
import org.mulesoft.lsp.workspace.ClientExecuteCommandParams

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}
import scala.scalajs.js.|

case class ProtocolConnectionLanguageClient(connection: ProtocolConnection)
    extends LspLanguageClient
    with AlsLanguageClient[js.Any] {
  override def publishDiagnostic(params: PublishDiagnosticsParams): Unit = {
    val clientParams: ClientPublishDiagnosticsParams = params.toClient
    connection
      .sendNotification[ClientPublishDiagnosticsParams, js.Any](PublishDiagnosticsNotification.`type`, clientParams)
  }

  override def notifyTelemetry(params: TelemetryMessage): Unit = {
    connection.sendNotification[ClientTelemetryMessage, js.Any](TelemetryEventNotification.`type`, params.toClient)
  }

  override def notifySerialization(params: SerializationResult[js.Any]): Unit =
    connection
      .sendNotification[ClientSerializationResult, js.Any](SerializationEventNotification.`type`, params.toClient)

  override def notifyProjectFiles(params: FilesInProjectParams): Unit = {
    connection
      .sendNotification[ClientFilesInProjectParams, js.Any](FilesInProjectEventNotification.`type`, params.toClient)
  }
}

@JSExportAll
@JSExportTopLevel("ProtocolConnectionBinder")
object ProtocolConnectionBinder {
  def bind(protocolConnection: ProtocolConnection,
           languageServer: LanguageServer,
           clientAware: LspLanguageClientAware with AlsLanguageClientAware[js.Any],
           serializationProps: JsSerializationProps): Unit = {
    def resolveHandler[P, R](`type`: org.mulesoft.lsp.feature.RequestType[P, R]): RequestHandler[P, R] = {
      val maybeHandler = languageServer.resolveHandler(`type`)
      if (maybeHandler.isEmpty) throw new UnsupportedOperationException else maybeHandler.get
    }

    clientAware.connect(ProtocolConnectionLanguageClient(protocolConnection))
    clientAware.connectAls(ProtocolConnectionLanguageClient(protocolConnection))

    val initializeHandlerJs
      : js.Function2[ClientAlsInitializeParams, CancellationToken, Thenable[ClientAlsInitializeResult]] =
      (param: ClientAlsInitializeParams, _: CancellationToken) =>
        languageServer
          .initialize(param.toShared)
          .map(_.toClient)
          .toJSPromise
          .asInstanceOf[Thenable[ClientAlsInitializeResult]]

    protocolConnection.onRequest(
      InitializeRequest.`type`,
      initializeHandlerJs
        .asInstanceOf[ClientRequestHandler[ClientAlsInitializeParams, ClientAlsInitializeResult, js.Any]])

    val initializedHandlerJs: js.Function2[js.Any, CancellationToken, Unit] = (_: js.Any, _: CancellationToken) =>
      languageServer.initialized()

    protocolConnection.onNotification(InitializedNotification.`type`,
                                      initializedHandlerJs.asInstanceOf[NotificationHandler[js.Any]])

    val exitHandlerJs: js.Function1[CancellationToken, Unit] = (_: CancellationToken) => languageServer.exit()

    protocolConnection.onNotification(ExitNotification.`type`, exitHandlerJs.asInstanceOf[NotificationHandler0])

    val shutdownHandlerJs: js.Function1[CancellationToken, js.Any] = (_: CancellationToken) =>
      languageServer.shutdown()

    protocolConnection.onRequest(ShutdownRequest.`type`,
                                 shutdownHandlerJs.asInstanceOf[ClientRequestHandler0[js.Any, js.Any]])

    val onDidChangeHandlerJs: js.Function2[ClientDidChangeTextDocumentParams, CancellationToken, Unit] =
      (param: ClientDidChangeTextDocumentParams, _: CancellationToken) =>
        languageServer.textDocumentSyncConsumer.didChange(param.toShared)

    protocolConnection.onNotification(
      DidChangeTextDocumentNotification.`type`,
      onDidChangeHandlerJs.asInstanceOf[NotificationHandler[ClientDidChangeTextDocumentParams]])

    val onDidOpenHandlerJs: js.Function2[ClientDidOpenTextDocumentParams, CancellationToken, Unit] =
      (param: ClientDidOpenTextDocumentParams, _: CancellationToken) =>
        languageServer.textDocumentSyncConsumer.didOpen(param.toShared)

    protocolConnection.onNotification(
      DidOpenTextDocumentNotification.`type`,
      onDidOpenHandlerJs.asInstanceOf[NotificationHandler[ClientDidOpenTextDocumentParams]])

    val onUpdateClientConfigurationJs: js.Function2[ClientUpdateConfigurationParams, CancellationToken, Unit] =
      (param: ClientUpdateConfigurationParams, _: CancellationToken) =>
        languageServer.updateConfiguration(param.toShared)

    protocolConnection.onNotification(
      UpdateClientConfigurationNotification.`type`,
      onUpdateClientConfigurationJs.asInstanceOf[NotificationHandler[ClientUpdateConfigurationParams]])

    val onDidCloseHandlerJs: js.Function2[ClientDidCloseTextDocumentParams, CancellationToken, Unit] =
      (param: ClientDidCloseTextDocumentParams, _: CancellationToken) =>
        languageServer.textDocumentSyncConsumer.didClose(param.toShared)

    protocolConnection.onNotification(
      DidCloseTextDocumentNotification.`type`,
      onDidCloseHandlerJs.asInstanceOf[NotificationHandler[ClientDidCloseTextDocumentParams]])

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
        languageServer.workspaceService
          .executeCommand(param.toShared)
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

    // DocumentHighlight
    val onDocumentHighlightHandlerJs
      : js.Function2[ClientDocumentHighlightParams, CancellationToken, Thenable[js.Array[ClientDocumentHighlight]]] =
      (param: ClientDocumentHighlightParams, _: CancellationToken) =>
        resolveHandler(DocumentHighlightRequestType)(param.toShared)
          .map(_.map(_.toClient).toJSArray)
          .toJSPromise
          .asInstanceOf[Thenable[js.Array[ClientDocumentHighlight]]]

    protocolConnection.onRequest(
      DocumentHighlightRequest.`type`,
      onDocumentHighlightHandlerJs
        .asInstanceOf[ClientRequestHandler[ClientDocumentHighlightParams, js.Array[ClientDocumentHighlight], js.Any]]
    )
    // End DocumentHighlight

    // FindFileUsage
    val onFindFileUsageHandlerJs
      : js.Function2[ClientTextDocumentIdentifier, CancellationToken, Thenable[js.Array[ClientLocation]]] =
      (param: ClientTextDocumentIdentifier, _: CancellationToken) =>
        resolveHandler(FileUsageRequestType)(param.toShared)
          .map(_.map(_.toClient).toJSArray)
          .toJSPromise
          .asInstanceOf[Thenable[js.Array[ClientLocation]]]

    protocolConnection.onRequest(
      FileUsageRequest.`type`,
      onFindFileUsageHandlerJs
        .asInstanceOf[ClientRequestHandler[ClientTextDocumentIdentifier, js.Array[ClientLocation], js.Any]]
    )
    // End FindFileUsage

    // Definition
    val onDefinitionHandlerJs
      : js.Function2[ClientDefinitionParams,
                     CancellationToken,
                     Thenable[ClientLocation | js.Array[ClientLocation] | js.Array[ClientLocationLink]]] =
      (param: ClientDefinitionParams, _: CancellationToken) =>
        resolveHandler(DefinitionRequestType)(param.toShared)
          .map(_.toClient)
          .toJSPromise
          .asInstanceOf[Thenable[ClientLocation | js.Array[ClientLocation] | js.Array[ClientLocationLink]]]

    protocolConnection.onRequest(
      DefinitionRequest.`type`,
      onDefinitionHandlerJs
        .asInstanceOf[ClientRequestHandler[ClientDefinitionParams,
                                           ClientLocation | js.Array[ClientLocation] | js.Array[ClientLocationLink],
                                           js.Any]]
    )
    // End Definition

    // Implementation
    val onImplementationHandlerJs
      : js.Function2[ClientImplementationParams,
                     CancellationToken,
                     Thenable[ClientLocation | js.Array[ClientLocation] | js.Array[ClientLocationLink]]] =
      (param: ClientImplementationParams, _: CancellationToken) =>
        resolveHandler(ImplementationRequestType)(param.toShared)
          .map(_.toClient)
          .toJSPromise
          .asInstanceOf[Thenable[ClientLocation | js.Array[ClientLocation] | js.Array[ClientLocationLink]]]

    protocolConnection.onRequest(
      ImplementationRequest.`type`,
      onImplementationHandlerJs
        .asInstanceOf[ClientRequestHandler[ClientImplementationParams,
                                           ClientLocation | js.Array[ClientLocation] | js.Array[ClientLocationLink],
                                           js.Any]]
    )
    // End Implementation

    // TypeDefinition
    val onTypeDefinitionHandlerJs
      : js.Function2[ClientTypeDefinitionParams,
                     CancellationToken,
                     Thenable[ClientLocation | js.Array[ClientLocation] | js.Array[ClientLocationLink]]] =
      (param: ClientTypeDefinitionParams, _: CancellationToken) =>
        resolveHandler(TypeDefinitionRequestType)(param.toShared)
          .map(_.toClient)
          .toJSPromise
          .asInstanceOf[Thenable[ClientLocation | js.Array[ClientLocation] | js.Array[ClientLocationLink]]]

    protocolConnection.onRequest(
      TypeDefinitionRequest.`type`,
      onTypeDefinitionHandlerJs
        .asInstanceOf[ClientRequestHandler[ClientTypeDefinitionParams,
                                           ClientLocation | js.Array[ClientLocation] | js.Array[ClientLocationLink],
                                           js.Any]]
    )
    // End TypeDefinition

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

    // Rename
    val onRenameHandlerJs: js.Function2[ClientRenameParams, CancellationToken, Thenable[ClientWorkspaceEdit]] =
      (param: ClientRenameParams, _: CancellationToken) =>
        resolveHandler(RenameRequestType)(param.toShared)
          .map(_.toClient)
          .toJSPromise
          .asInstanceOf[Thenable[ClientWorkspaceEdit]]

    protocolConnection.onRequest(
      RenameRequest.`type`,
      onRenameHandlerJs
        .asInstanceOf[ClientRequestHandler[ClientRenameParams, ClientWorkspaceEdit, js.Any]]
    )
    // End Rename

    // PrepareRename
    val onPrepareRenameHandlerJs
      : js.Function2[ClientPrepareRenameParams, CancellationToken, Thenable[ClientRange | ClientPrepareRenameResult]] =
      (param: ClientPrepareRenameParams, _: CancellationToken) =>
        resolveHandler(PrepareRenameRequestType)(param.toShared)
          .map(_.map(_.toClient).orUndefined)
          .toJSPromise
          .asInstanceOf[Thenable[ClientRange | ClientPrepareRenameResult]]

    protocolConnection.onRequest(
      PrepareRenameRequest.`type`,
      onPrepareRenameHandlerJs
        .asInstanceOf[ClientRequestHandler[ClientPrepareRenameParams, ClientRange | ClientPrepareRenameResult, js.Any]]
    )
    // End PrepareRename

    // CleanDiagnosticTree
    val onCleanDiagnosticTreeHandlerJs: js.Function2[ClientCleanDiagnosticTreeParams,
                                                     CancellationToken,
                                                     Thenable[js.Array[ClientAlsPublishDiagnosticsParams]]] =
      (param: ClientCleanDiagnosticTreeParams, _: CancellationToken) =>
        resolveHandler(CleanDiagnosticTreeRequestType)(param.toShared)
          .map(_.map(_.toClient).toJSArray)
          .toJSPromise
          .asInstanceOf[Thenable[js.Array[ClientAlsPublishDiagnosticsParams]]]

    protocolConnection.onRequest(
      ClientCleanDiagnosticTreeRequestType.`type`,
      onCleanDiagnosticTreeHandlerJs
        .asInstanceOf[
          ClientRequestHandler[ClientCleanDiagnosticTreeParams, js.Array[ClientAlsPublishDiagnosticsParams], js.Any]]
    )
    // End CleanDiagnosticTree

    // ConversionRequest

    val onConversionHandlerJs
      : js.Function2[ClientConversionParams, CancellationToken, Thenable[js.Array[ClientSerializedDocument]]] =
      (param: ClientConversionParams, _: CancellationToken) =>
        resolveHandler(ConversionRequestType)(param.toShared)
          .map(_.toClient)
          .toJSPromise
          .asInstanceOf[Thenable[js.Array[ClientSerializedDocument]]]

    protocolConnection.onRequest(
      ClientConversionRequestType.`type`,
      onConversionHandlerJs
        .asInstanceOf[ClientRequestHandler[ClientConversionParams, js.Array[ClientSerializedDocument], js.Any]]
    )
    // End Conversion Request

    // SerializedJSONLD request

    val onSerializedHandlerJs
      : js.Function2[ClientSerializationParams, CancellationToken, Thenable[ClientSerializationResult]] =
      (param: ClientSerializationParams, _: CancellationToken) =>
        resolveHandler(serializationProps.requestType)(param.toShared)
          .map(s => new ClientSerializationMessageConverter(s).toClient)
          .toJSPromise
          .asInstanceOf[Thenable[ClientSerializationResult]]

    protocolConnection.onRequest(
      ClientSerializationRequestType.`type`,
      onSerializedHandlerJs
        .asInstanceOf[ClientRequestHandler[ClientSerializationParams, ClientSerializationResult, js.Any]]
    )
    // End SerializedJSONLD request

  }
}
