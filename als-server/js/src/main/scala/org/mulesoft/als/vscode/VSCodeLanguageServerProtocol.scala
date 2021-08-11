package org.mulesoft.als.vscode

import org.mulesoft.als.server.protocol.configuration.{ClientAlsInitializeParams, ClientAlsInitializeResult}
import org.mulesoft.lsp.edit.{ClientTextEdit, ClientWorkspaceEdit}
import org.mulesoft.lsp.feature.codeactions.{ClientCodeAction, ClientCodeActionParams}
import org.mulesoft.lsp.feature.command.ClientCommand
import org.mulesoft.lsp.feature.common.{ClientLocation, ClientLocationLink, ClientRange}
import org.mulesoft.lsp.feature.completion.{ClientCompletionItem, ClientCompletionList, ClientCompletionParams}
import org.mulesoft.lsp.feature.definition.ClientDefinitionParams
import org.mulesoft.lsp.feature.diagnostic.ClientPublishDiagnosticsParams
import org.mulesoft.lsp.feature.documenthighlight.{ClientDocumentHighlight, ClientDocumentHighlightParams}
import org.mulesoft.lsp.feature.documentsymbol.{
  ClientDocumentSymbol,
  ClientDocumentSymbolParams,
  ClientSymbolInformation
}
import org.mulesoft.lsp.feature.folding.{ClientFoldingRange, ClientFoldingRangeParams}
import org.mulesoft.lsp.feature.formatting.{ClientDocumentFormattingParams, ClientDocumentRangeFormattingParams}
import org.mulesoft.lsp.feature.hover.{ClientHover, ClientHoverParams}
import org.mulesoft.lsp.feature.implementation.ClientImplementationParams
import org.mulesoft.lsp.feature.link.{ClientDocumentLink, ClientDocumentLinkParams}
import org.mulesoft.lsp.feature.reference.ClientReferenceParams
import org.mulesoft.lsp.feature.rename.{ClientPrepareRenameParams, ClientPrepareRenameResult, ClientRenameParams}
import org.mulesoft.lsp.feature.selection.{ClientSelectionRange, ClientSelectionRangeParams}
import org.mulesoft.lsp.feature.telemetry.ClientTelemetryMessage
import org.mulesoft.lsp.feature.typedefinition.ClientTypeDefinitionParams
import org.mulesoft.lsp.textsync.{
  ClientDidChangeTextDocumentParams,
  ClientDidCloseTextDocumentParams,
  ClientDidOpenTextDocumentParams
}
import org.mulesoft.lsp.workspace.ClientExecuteCommandParams

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport
import scala.scalajs.js.|

// Exclude from coverage because native functions are not tracked
// $COVERAGE-OFF$
/** vscode-languageserver-protocol */
@js.native
@JSImport("vscode-languageserver-protocol", "createProtocolConnection")
object ProtocolConnection extends js.Function3[MessageReader, MessageWriter, Logger, ProtocolConnection] {
  def apply(reader: MessageReader, writer: MessageWriter, logger: Logger): ProtocolConnection = js.native
}

@js.native
trait ProtocolConnection extends js.Object {

  /**
    * Sends a request and returns a promise resolving to the result of the request.
    *
    * @param type  The type of request to sent.
    * @param token An optional cancellation token.
    * @returns A promise resolving to the request's result.
    */
  def sendRequest[R, E](`type`: RequestType0[R, E], token: CancellationToken): Thenable[R]

  /**
    * Sends a request and returns a promise resolving to the result of the request.
    *
    * @param type   The type of request to sent.
    * @param params The request's parameter.
    * @param token  An optional cancellation token.
    * @returns A promise resolving to the request's result.
    */
  def sendRequest[P, R, E](`type`: RequestType[P, R, E], params: P, token: CancellationToken): Thenable[R]

  /**
    * Sends a request and returns a promise resolving to the result of the request.
    *
    * @param method the request's method name.
    * @param token  An optional cancellation token.
    * @returns A promise resolving to the request's result.
    */
  def sendRequest[R](method: String, token: CancellationToken): Thenable[R]

  /**
    * Sends a request and returns a promise resolving to the result of the request.
    *
    * @param method the request's method name.
    * @param param The request's parameter.
    * @param token  An optional cancellation token.
    * @returns A promise resolving to the request's result.
    */
  def sendRequest[R](method: String, param: js.Any, token: CancellationToken): Thenable[R]

  /**
    * Installs a request handler.
    *
    * @param type    The request type to install the handler for.
    * @param handler The actual handler.
    */
  def onRequest[R, E](`type`: RequestType0[R, E], handler: RequestHandler0[R, E]): Unit

  /**
    * Installs a request handler.
    *
    * @param type    The request type to install the handler for.
    * @param handler The actual handler.
    */
  def onRequest[P, R, E](`type`: RequestType[P, R, E], handler: RequestHandler[P, R, E]): Unit

  /**
    * Installs a request handler.
    *
    * @param methods The method name to install the handler for.
    * @param handler The actual handler.
    */
//  def onRequest[R, E](method: String, handler: GenericRequestHandler[R, E]): Unit

  /**
    * Sends a notification.
    *
    * @param type the notification's type to send.
    */
//  def sendNotification[RO](`type`: NotificationType0[RO]): Unit

  /**
    * Sends a notification.
    *
    * @param type   the notification's type to send.
    * @param params the notification's parameters.
    */
  def sendNotification[P](`type`: NotificationType[P], params: P): Unit

  /**
    * Sends a notification.
    *
    * @param method the notification's method name.
    */
//  def sendNotification(method: String): Unit

  /**
    * Sends a notification.
    *
    * @param method the notification's method name.
    * @param params the notification's parameters.
    */
//  def sendNotification(method: String, params: js.Any): Unit

  /**
    * Installs a notification handler.
    *
    * @param type    The notification type to install the handler for.
    * @param handler The actual handler.
    */
  def onNotification(`type`: NotificationType0, handler: NotificationHandler0): Unit

  /**
    * Installs a notification handler.
    *
    * @param type    The notification type to install the handler for.
    * @param handler The actual handler.
    */
  def onNotification[P](`type`: NotificationType[P], handler: NotificationHandler[P]): Unit

  /**
    * Installs a notification handler.
    *
    * @param methods The method name to install the handler for.
    * @param handler The actual handler.
    */
  def onNotification(method: String, handler: GenericNotificationHandler): Unit

  /**
    * Enables tracing mode for the connection.
    */
  def trace(value: Trace, tracer: Tracer, sendNotification: Boolean): Unit

  def trace(value: Trace, tracer: Tracer, traceOptions: TraceOptions): Unit

  /**
    * An event emitter firing when an error occurs on the connection.
    */
  def onError: Event[js.Tuple3[Error, js.UndefOr[Message], js.UndefOr[Int]]]

  /**
    * An event emitter firing when the connection got closed.
    */
  def onClose: Event[Unit]

  /**
    * An event emiiter firing when the connection receives a notification that ies not
    * handled.
    */
  def onUnhandledNotification: Event[NotificationMessage]

  /**
    * An event emitter firing when the connection got disposed.
    */
  def onDispose: Event[Unit]

  /**
    * Actively disposes the connection.
    */
  def dispose(): Unit

  /**
    * Turns the connection into listening mode
    */
  def listen(): Unit
}

/** Notifications */
@js.native
@JSImport("vscode-languageserver-protocol", "DidChangeTextDocumentNotification")
object DidChangeTextDocumentNotification extends js.Object {
  val `type`: NotificationType[ClientDidChangeTextDocumentParams] = js.native
}

@js.native
@JSImport("vscode-languageserver-protocol", "DidOpenTextDocumentNotification")
object DidOpenTextDocumentNotification extends js.Object {
  val `type`: NotificationType[ClientDidOpenTextDocumentParams] = js.native
}

@js.native
@JSImport("vscode-languageserver-protocol", "DidCloseTextDocumentNotification")
object DidCloseTextDocumentNotification extends js.Object {
  val `type`: NotificationType[ClientDidCloseTextDocumentParams] = js.native
}

@js.native
@JSImport("vscode-languageserver-protocol", "InitializedNotification")
object InitializedNotification extends js.Object {
  val `type`: NotificationType[js.Any] = js.native
}

@js.native
@JSImport("vscode-languageserver-protocol", "ExitNotification")
object ExitNotification extends js.Object {
  val `type`: NotificationType0 = js.native
}

@js.native
@JSImport("vscode-languageserver-protocol", "PublishDiagnosticsNotification")
object PublishDiagnosticsNotification extends js.Object {
  val `type`: NotificationType[ClientPublishDiagnosticsParams] = js.native
}

@js.native
@JSImport("vscode-languageserver-protocol", "TelemetryEventNotification")
object TelemetryEventNotification extends js.Object {
  val `type`: NotificationType[ClientTelemetryMessage] = js.native
}

/** Requests */
@js.native
@JSImport("vscode-languageserver-protocol", "CompletionRequest")
object CompletionRequest extends js.Object {
  val `type`: RequestType[ClientCompletionParams, ClientCompletionList | js.Array[ClientCompletionItem], js.Any] =
    js.native
}

@js.native
@JSImport("vscode-languageserver-protocol", "DocumentSymbolRequest")
object DocumentSymbolRequest extends js.Object {
  val `type`: RequestType[ClientDocumentSymbolParams,
                          js.Array[ClientDocumentSymbol] | js.Array[ClientSymbolInformation],
                          js.Any] = js.native
}

@js.native
@JSImport("vscode-languageserver-protocol", "DocumentLinkRequest")
object DocumentLinkRequest extends js.Object {
  val `type`: RequestType[ClientDocumentLinkParams, js.Array[ClientDocumentLink], js.Any] = js.native
}

@js.native
@JSImport("vscode-languageserver-protocol", "DocumentHighlightRequest")
object DocumentHighlightRequest extends js.Object {
  val `type`: RequestType[ClientDocumentHighlightParams, js.Array[ClientDocumentHighlight], js.Any] = js.native
}

@js.native
@JSImport("vscode-languageserver-protocol", "DefinitionRequest")
object DefinitionRequest extends js.Object {
  val `type`: RequestType[ClientDefinitionParams,
                          ClientLocation | js.Array[ClientLocation] | js.Array[ClientLocationLink],
                          js.Any] = js.native
}

@js.native
@JSImport("vscode-languageserver-protocol", "ImplementationRequest")
object ImplementationRequest extends js.Object {
  val `type`: RequestType[ClientImplementationParams,
                          ClientLocation | js.Array[ClientLocation] | js.Array[ClientLocationLink],
                          js.Any] = js.native
}

@js.native
@JSImport("vscode-languageserver-protocol", "TypeDefinitionRequest")
object TypeDefinitionRequest extends js.Object {
  val `type`: RequestType[ClientTypeDefinitionParams,
                          ClientLocation | js.Array[ClientLocation] | js.Array[ClientLocationLink],
                          js.Any] = js.native
}

@js.native
@JSImport("vscode-languageserver-protocol", "ReferencesRequest")
object ReferencesRequest extends js.Object {
  val `type`: RequestType[ClientReferenceParams, js.Array[ClientLocation], js.Any] = js.native
}

@js.native
@JSImport("vscode-languageserver-protocol", "HoverRequest")
object HoverRequest extends js.Object {
  val `type`: RequestType[ClientHoverParams, ClientHover, js.Any] = js.native
}

@js.native
@JSImport("vscode-languageserver-protocol", "FoldingRangeRequest")
object FoldingRangeRequest extends js.Object {
  val `type`: RequestType[ClientFoldingRangeParams, ClientFoldingRange, js.Any] = js.native
}

@js.native
@JSImport("vscode-languageserver-protocol", "SelectionRangeRequest")
object SelectionRangeRequest extends js.Object {
  val `type`: RequestType[ClientSelectionRangeParams, js.Array[ClientSelectionRange], js.Any] = js.native
}

@js.native
@JSImport("vscode-languageserver-protocol", "RenameRequest")
object RenameRequest extends js.Object {
  val `type`: RequestType[ClientRenameParams, ClientWorkspaceEdit, js.Any] = js.native
}

@js.native
@JSImport("vscode-languageserver-protocol", "PrepareRenameRequest")
object PrepareRenameRequest extends js.Object {
  val `type`: RequestType[ClientPrepareRenameParams, ClientRange | ClientPrepareRenameResult, js.Any] =
    js.native
}

@js.native
@JSImport("vscode-languageserver-protocol", "CodeActionRequest")
object CodeActionRequest extends js.Object {
  val `type`: RequestType[ClientCodeActionParams, js.Array[ClientCommand] | js.Array[ClientCodeAction], js.Any] =
    js.native
}

@js.native
@JSImport("vscode-languageserver-protocol", "ExecuteCommandRequest")
object ExecuteCommandRequest extends js.Object {
  val `type`: RequestType[ClientExecuteCommandParams, js.Any, js.Any] = js.native
}

@js.native
@JSImport("vscode-languageserver-protocol", "InitializeRequest")
object InitializeRequest extends js.Object {
  val `type`: RequestType[ClientAlsInitializeParams, ClientAlsInitializeResult, js.Any] = js.native
}

@js.native
@JSImport("vscode-languageserver-protocol", "ShutdownRequest")
object ShutdownRequest extends js.Object {
  val `type`: RequestType0[js.Any, js.Any] = js.native
}

@js.native
@JSImport("vscode-languageserver-protocol", "DocumentFormattingRequest")
object DocumentFormattingRequest extends js.Object {
  val `type`: RequestType[ClientDocumentFormattingParams, js.Array[ClientTextEdit], js.Any] = js.native
}

@js.native
@JSImport("vscode-languageserver-protocol", "DocumentRangeFormattingRequest")
object DocumentRangeFormattingRequest extends js.Object {
  val `type`: RequestType[ClientDocumentRangeFormattingParams, js.Array[ClientTextEdit], js.Any] = js.native
}
