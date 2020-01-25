package org.mulesoft.lsp.configuration

import org.mulesoft.lsp.feature.codeactions.CodeActionOptions
import org.mulesoft.lsp.feature.completion.CompletionOptions
import org.mulesoft.lsp.feature.link.DocumentLinkOptions
import org.mulesoft.lsp.feature.rename.RenameOptions
import org.mulesoft.lsp.textsync.TextDocumentSyncKind.TextDocumentSyncKind
import org.mulesoft.lsp.textsync.TextDocumentSyncOptions

/**
  * @param textDocumentSync       Defines how text documents are synced. Is either a detailed structure defining each
  *                               notification or for backwards compatibility the TextDocumentSyncKind number. If
  *                               omitted it defaults to `TextDocumentSyncKind.None`.
  * @param completionProvider     The server provides completion support.
  * @param definitionProvider     The server provides goto definition support.
  * @param referencesProvider     The server provides find references support.
  * @param documentSymbolProvider The server provides document symbol support.
  * @param renameProvider         The server provides rename support. RenameOptions may only be
  *                               specified if the client states that it supports
  *                               `prepareSupport` in its initial `initialize` request.
  * @param experimental           Experimental server capabilities.
  */
case class ServerCapabilities(textDocumentSync: Option[Either[TextDocumentSyncKind, TextDocumentSyncOptions]] = None,
                              completionProvider: Option[CompletionOptions] = None,
                              definitionProvider: Boolean = false,
                              referencesProvider: Boolean = false,
                              documentSymbolProvider: Boolean = false,
                              renameProvider: Option[RenameOptions] = None,
                              codeActionProvider: Option[CodeActionOptions] = None,
                              documentLinkProvider: Option[DocumentLinkOptions] = None,
                              workspace: Option[WorkspaceServerCapabilities] = None,
                              experimental: Option[AnyRef] = None)

object ServerCapabilities {
  def empty = ServerCapabilities()
}
