package org.mulesoft.als.vscode

import org.mulesoft.als.client.lsp.common.{ClientLocation, ClientLocationLink, ClientTextDocumentPositionParams}
import org.mulesoft.als.client.lsp.configuration.{ClientInitializeParams, ClientInitializeResult}
import org.mulesoft.als.client.lsp.feature.completion.{
  ClientCompletionItem,
  ClientCompletionList,
  ClientCompletionParams
}
import org.mulesoft.als.client.lsp.feature.diagnostic.ClientPublishDiagnosticsParams
import org.mulesoft.als.client.lsp.feature.documentsymbol.{
  ClientDocumentSymbol,
  ClientDocumentSymbolParams,
  ClientSymbolInformation
}
import org.mulesoft.als.client.lsp.feature.link.{ClientDocumentLink, ClientDocumentLinkParams}
import org.mulesoft.als.client.lsp.feature.reference.ClientReferenceParams
import org.mulesoft.als.client.lsp.feature.telemetry.ClientTelemetryMessage
import org.mulesoft.als.client.lsp.textsync.{
  ClientDidChangeTextDocumentParams,
  ClientDidCloseTextDocumentParams,
  ClientDidOpenTextDocumentParams
}
import org.mulesoft.als.client.lsp.workspace.ClientExecuteCommandParams

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
  def sendRequest[R, E, RO](`type`: RequestType0[R, E, RO], token: CancellationToken): Thenable[R]

  /**
    * Sends a request and returns a promise resolving to the result of the request.
    *
    * @param type   The type of request to sent.
    * @param params The request's parameter.
    * @param token  An optional cancellation token.
    * @returns A promise resolving to the request's result.
    */
  def sendRequest[P, R, E, RO](`type`: RequestType[P, R, E, RO], params: P, token: CancellationToken): Thenable[R]

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
  def onRequest[R, E, RO](`type`: RequestType0[R, E, RO], handler: RequestHandler0[R, E]): Unit

  /**
    * Installs a request handler.
    *
    * @param type    The request type to install the handler for.
    * @param handler The actual handler.
    */
  def onRequest[P, R, E, RO](`type`: RequestType[P, R, E, RO], handler: RequestHandler[P, R, E]): Unit

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
  def sendNotification[P, RO](`type`: NotificationType[P, RO], params: P): Unit

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
  def onNotification[RO](`type`: NotificationType0[RO], handler: NotificationHandler0): Unit

  /**
    * Installs a notification handler.
    *
    * @param type    The notification type to install the handler for.
    * @param handler The actual handler.
    */
  def onNotification[P, RO](`type`: NotificationType[P, RO], handler: NotificationHandler[P]): Unit

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
  val `type`: NotificationType[ClientDidChangeTextDocumentParams, js.Any] = js.native
}

@js.native
@JSImport("vscode-languageserver-protocol", "DidOpenTextDocumentNotification")
object DidOpenTextDocumentNotification extends js.Object {
  val `type`: NotificationType[ClientDidOpenTextDocumentParams, js.Any] = js.native
}

@js.native
@JSImport("vscode-languageserver-protocol", "DidCloseTextDocumentNotification")
object DidCloseTextDocumentNotification extends js.Object {
  val `type`: NotificationType[ClientDidCloseTextDocumentParams, js.Any] = js.native
}

@js.native
@JSImport("vscode-languageserver-protocol", "InitializedNotification")
object InitializedNotification extends js.Object {
  val `type`: NotificationType[js.Any, js.Any] = js.native
}

@js.native
@JSImport("vscode-languageserver-protocol", "ExitNotification")
object ExitNotification extends js.Object {
  val `type`: NotificationType0[js.Any] = js.native
}

@js.native
@JSImport("vscode-languageserver-protocol", "PublishDiagnosticsNotification")
object PublishDiagnosticsNotification extends js.Object {
  val `type`: NotificationType[ClientPublishDiagnosticsParams, js.Any] = js.native
}

@js.native
@JSImport("vscode-languageserver-protocol", "TelemetryEventNotification")
object TelemetryEventNotification extends js.Object {
  val `type`: NotificationType[ClientTelemetryMessage, js.Any] = js.native
}

/** Requests */
@js.native
@JSImport("vscode-languageserver-protocol", "CompletionRequest")
object CompletionRequest extends js.Object {
  val `type`
    : RequestType[ClientCompletionParams, ClientCompletionList | js.Array[ClientCompletionItem], js.Any, js.Any] =
    js.native
}

@js.native
@JSImport("vscode-languageserver-protocol", "DocumentSymbolRequest")
object DocumentSymbolRequest extends js.Object {
  val `type`: RequestType[ClientDocumentSymbolParams,
                          js.Array[ClientDocumentSymbol] | js.Array[ClientSymbolInformation],
                          js.Any,
                          js.Any] = js.native
}

@js.native
@JSImport("vscode-languageserver-protocol", "DocumentLinkRequest")
object DocumentLinkRequest extends js.Object {
  val `type`: RequestType[ClientDocumentLinkParams, js.Array[ClientDocumentLink], js.Any, js.Any] = js.native
}

@js.native
@JSImport("vscode-languageserver-protocol", "DefinitionRequest")
object DefinitionRequest extends js.Object {
  val `type`: RequestType[ClientTextDocumentPositionParams,
                          ClientLocation | js.Array[ClientLocation] | js.Array[ClientLocationLink],
                          js.Any,
                          js.Any] = js.native
}

@js.native
@JSImport("vscode-languageserver-protocol", "ReferencesRequest")
object ReferencesRequest extends js.Object {
  val `type`: RequestType[ClientReferenceParams, js.Array[ClientLocation], js.Any, js.Any] = js.native
}

@js.native
@JSImport("vscode-languageserver-protocol", "ExecuteCommandRequest")
object ExecuteCommandRequest extends js.Object {
  val `type`: RequestType[ClientExecuteCommandParams, js.Any, js.Any, js.Any] = js.native
}

@js.native
@JSImport("vscode-languageserver-protocol", "InitializeRequest")
object InitializeRequest extends js.Object {
  val `type`: RequestType[ClientInitializeParams, ClientInitializeResult, js.Any, js.Any] = js.native
}

@js.native
@JSImport("vscode-languageserver-protocol", "ShutdownRequest")
object ShutdownRequest extends js.Object {
  val `type`: RequestType0[js.Any, js.Any, js.Any] = js.native
}
