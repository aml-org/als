package org.mulesoft.lsp.configuration

import org.mulesoft.lsp.configuration.TraceKind.TraceKind

/** The initialize request is sent as the first request from the client to the server. If the server receives a request
  * or notification before the initialize request it should act as follows:
  *
  * For a request the response should be an error with code: -32002. The message can be picked by the server.
  * Notifications should be dropped, except for the exit notification. This will allow the exit of a server without an
  * initialize request. Until the server has responded to the initialize request with an InitializeResult, the client
  * must not send any additional requests or notifications to the server. In addition the server is not allowed to send
  * any requests or notifications to the client until it has responded with an InitializeResult, with the exception that
  * during the initialize request the server is allowed to send the notifications window/showMessage, window/logMessage
  * and telemetry/event as well as the window/showMessageRequest request to the client.
  *
  * The initialize request may only be sent once.
  *
  * Request:
  *
  * method: ‘initialize’ params: InitializeParams defined as follows:
  *
  * @param capabilities
  *   The capabilities provided by the client (editor or tool)
  * @param trace
  *   The initial trace setting. If omitted trace is disabled ('off').
  * @param rootUri
  *   The rootUri of the workspace. Is None if no folder is open. If both `rootPath` and `rootUri` are set `rootUri`
  *   wins.
  * @param processId
  *   The process Id of the parent process that started the server. Is None if the process has not been started by
  *   another process. If the parent process is not alive then the server should exit (see exit notification) its
  *   process.
  * @param workspaceFolders
  *   The workspace folders configured in the client when the server starts. This property is only available if the
  *   client supports workspace folders. It can be `None` if the client supports workspace folders but none are
  *   configured.
  * @param rootPath
  *   The rootPath of the workspace. Is null if no folder is open.
  * @param initializationOptions
  *   User provided initialization options.
  */
class InitializeParams private (
    val capabilities: ClientCapabilities,
    val trace: TraceKind,
    val locale: Option[String] = None,
    val rootUri: Option[String] = None,
    val processId: Option[Int] = None,
    val workspaceFolders: Option[Seq[WorkspaceFolder]] = None,
    val rootPath: Option[String] = None,
    val initializationOptions: Option[Any] = None
)
