package org.mulesoft.lsp.configuration

import org.mulesoft.lsp.feature.codeactions.CodeActionOptions
import org.mulesoft.lsp.feature.completion.CompletionOptions
import org.mulesoft.lsp.feature.link.DocumentLinkOptions
import org.mulesoft.lsp.feature.rename.RenameOptions
import org.mulesoft.lsp.textsync.TextDocumentSyncKind.TextDocumentSyncKind
import org.mulesoft.lsp.textsync.TextDocumentSyncOptions

/** @param textDocumentSync
  *   Defines how text documents are synced. Is either a detailed structure defining each notification or for backwards
  *   compatibility the TextDocumentSyncKind number. If omitted it defaults to `TextDocumentSyncKind.None`.
  * @param completionProvider
  *   The server provides completion support.
  * @param definitionProvider
  *   The server provides goto definition support.
  * @param implementationProvider
  *   The server provides goto implementation support.
  * @param referencesProvider
  *   The server provides find references support.
  * @param documentSymbolProvider
  *   The server provides document symbol support.
  * @param renameProvider
  *   The server provides rename support. RenameOptions may only be specified if the client states that it supports
  *   `prepareSupport` in its initial `initialize` request.
  * @param experimental
  *   Experimental server capabilities.
  */
class ServerCapabilities(
    val textDocumentSync: Option[Either[TextDocumentSyncKind, TextDocumentSyncOptions]] = None,
    val completionProvider: Option[CompletionOptions] = None,
    val definitionProvider: Boolean = false,
    val implementationProvider: Boolean = false,
    val typeDefinitionProvider: Boolean = false,
    val referencesProvider: Boolean = false,
    val documentSymbolProvider: Boolean = false,
    val renameProvider: Option[RenameOptions] = None,
    val codeActionProvider: Option[CodeActionOptions] = None,
    val documentLinkProvider: Option[DocumentLinkOptions] = None,
    val workspace: Option[WorkspaceServerCapabilities] = None,
    val documentFormattingProvider: Boolean = false,
    val documentRangeFormattingProvider: Boolean = false,
    val experimental: Option[AnyRef] = None
)
