package org.mulesoft.lsp.workspace

import org.mulesoft.lsp.feature.documentsymbol.SymbolInformation

import scala.concurrent.Future

trait WorkspaceService {

  /** The workspace/executeCommand request is sent from the client to the server to trigger command execution on the
    * server. In most cases the server creates a WorkspaceEdit structure and applies the changes to the workspace using
    * the request workspace/applyEdit which is sent from the server to the client.
    *
    * Registration Options: ExecuteCommandRegistrationOptions
    */
  def executeCommand(params: ExecuteCommandParams): Future[AnyRef]

  /** The workspace symbol request is sent from the client to the server to list project-wide symbols matching the query
    * string.
    *
    * Registration Options: void
    */
  def symbol(params: WorkspaceSymbolParams): Future[List[SymbolInformation]] = {
    throw new UnsupportedOperationException()
  }

  /** A notification sent from the client to the server to signal the change of configuration settings.
    */
  def didChangeConfiguration(params: DidChangeConfigurationParams) = {}

  /** The watched files notification is sent from the client to the server when the client detects changes to file
    * watched by the language client.
    */
  def didChangeWatchedFiles(params: DidChangeWatchedFilesParams): Future[Unit] = Future.unit

  /** The workspace/didChangeWorkspaceFolders notification is sent from the client to the server to inform the server
    * about workspace folder configuration changes. The notification is sent by default if both
    * ServerCapabilities/workspaceFolders and ClientCapabilities/workspace/workspaceFolders are true; or if the server
    * has registered to receive this notification it first.
    */
  def didChangeWorkspaceFolders(params: DidChangeWorkspaceFoldersParams): Future[Unit] = Future.unit

}
