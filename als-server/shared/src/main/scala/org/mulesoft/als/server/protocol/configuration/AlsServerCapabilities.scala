package org.mulesoft.als.server.protocol.configuration

import org.mulesoft.als.server.feature.configuration.UpdateConfigurationServerOptions
import org.mulesoft.als.server.feature.configuration.workspace.WorkspaceConfigurationOptions
import org.mulesoft.als.server.feature.diagnostic.{CleanDiagnosticTreeOptions, CustomValidationOptions}
import org.mulesoft.als.server.feature.fileusage.FileUsageOptions
import org.mulesoft.als.server.feature.renamefile.RenameFileActionOptions
import org.mulesoft.als.server.feature.serialization.{ConversionRequestOptions, SerializationServerOptions}
import org.mulesoft.lsp.configuration.{WorkDoneProgressOptions, WorkspaceServerCapabilities}
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
  * @param serialization
  *   the server provides serialization of the resolved model notifications
  * @param cleanDiagnostics
  *   the server supports request for clean full diagnostics over a given uri
  */
case class AlsServerCapabilities(
    textDocumentSync: Option[Either[TextDocumentSyncKind, TextDocumentSyncOptions]] = None,
    completionProvider: Option[CompletionOptions] = None,
    definitionProvider: Option[Either[Boolean, WorkDoneProgressOptions]] = None,
    implementationProvider: Option[Either[Boolean, WorkDoneProgressOptions]] = None,
    typeDefinitionProvider: Option[Either[Boolean, WorkDoneProgressOptions]] = None,
    referencesProvider: Option[Either[Boolean, WorkDoneProgressOptions]] = None,
    documentSymbolProvider: Option[Either[Boolean, WorkDoneProgressOptions]] = None,
    renameProvider: Option[RenameOptions] = None,
    codeActionProvider: Option[CodeActionOptions] = None,
    documentLinkProvider: Option[DocumentLinkOptions] = None,
    workspace: Option[WorkspaceServerCapabilities] = None,
    experimental: Option[AnyRef] = None,
    serialization: Option[SerializationServerOptions] = None,
    cleanDiagnostics: Option[CleanDiagnosticTreeOptions] = None,
    fileUsage: Option[FileUsageOptions] = None,
    conversion: Option[ConversionRequestOptions] = None,
    documentHighlightProvider: Option[Boolean] = None,
    hoverProvider: Option[Boolean] = None,
    foldingRangeProvider: Option[Boolean] = None,
    selectionRange: Option[Either[Boolean, WorkDoneProgressOptions]] = None,
    renameFileAction: Option[RenameFileActionOptions] = None,
    updateConfiguration: Option[UpdateConfigurationServerOptions] = None,
    documentFormattingProvider: Option[Either[Boolean, WorkDoneProgressOptions]] = None,
    documentRangeFormattingProvider: Option[Either[Boolean, WorkDoneProgressOptions]] = None,
    workspaceConfiguration: Option[WorkspaceConfigurationOptions] = None,
    customValidations: Option[CustomValidationOptions] = None,
    hotReload: Option[Boolean] = None,
    disableValidationAllTraces: Option[Boolean] = None
)

object AlsServerCapabilities {
  def empty: AlsServerCapabilities = AlsServerCapabilities()
}
