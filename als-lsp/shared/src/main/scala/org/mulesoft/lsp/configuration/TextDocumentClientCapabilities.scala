package org.mulesoft.lsp.configuration

import org.mulesoft.lsp.feature.codeactions.CodeActionCapabilities
import org.mulesoft.lsp.feature.completion.CompletionClientCapabilities
import org.mulesoft.lsp.feature.definition.DefinitionClientCapabilities
import org.mulesoft.lsp.feature.diagnostic.DiagnosticClientCapabilities
import org.mulesoft.lsp.feature.documentFormatting.DocumentFormattingClientCapabilities
import org.mulesoft.lsp.feature.documentRangeFormatting.DocumentRangeFormattingClientCapabilities
import org.mulesoft.lsp.feature.documentsymbol.DocumentSymbolClientCapabilities
import org.mulesoft.lsp.feature.folding.FoldingRangeCapabilities
import org.mulesoft.lsp.feature.hover.HoverClientCapabilities
import org.mulesoft.lsp.feature.highlight.DocumentHighlightCapabilities
import org.mulesoft.lsp.feature.implementation.ImplementationClientCapabilities
import org.mulesoft.lsp.feature.typedefinition.TypeDefinitionClientCapabilities
import org.mulesoft.lsp.feature.link.DocumentLinkClientCapabilities
import org.mulesoft.lsp.feature.reference.ReferenceClientCapabilities
import org.mulesoft.lsp.feature.rename.RenameClientCapabilities
import org.mulesoft.lsp.feature.selectionRange.SelectionRangeCapabilities
import org.mulesoft.lsp.textsync.SynchronizationClientCapabilities

/** Text document specific client capabilities.
  */
case class TextDocumentClientCapabilities(
    synchronization: Option[SynchronizationClientCapabilities] = None,
    publishDiagnostics: Option[DiagnosticClientCapabilities] = None,
    completion: Option[CompletionClientCapabilities] = None,
    references: Option[ReferenceClientCapabilities] = None,
    documentSymbol: Option[DocumentSymbolClientCapabilities] = None,
    definition: Option[DefinitionClientCapabilities] = None,
    implementation: Option[ImplementationClientCapabilities] = None,
    typeDefinition: Option[TypeDefinitionClientCapabilities] = None,
    rename: Option[RenameClientCapabilities] = None,
    codeActionCapabilities: Option[CodeActionCapabilities] = None,
    documentLink: Option[DocumentLinkClientCapabilities] = None,
    hover: Option[HoverClientCapabilities] = None,
    documentHighlight: Option[DocumentHighlightCapabilities] = None,
    foldingRange: Option[FoldingRangeCapabilities] = None,
    selectionRange: Option[SelectionRangeCapabilities] = None,
    documentFormatting: Option[DocumentFormattingClientCapabilities] = None,
    documentRangeFormatting: Option[DocumentRangeFormattingClientCapabilities] = None
)
