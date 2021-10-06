package org.mulesoft.lsp.configuration

import org.mulesoft.lsp.convert.LspConvertersSharedToClient._
import org.mulesoft.lsp.feature.codeactions.ClientCodeActionCapabilities
import org.mulesoft.lsp.feature.completion.ClientCompletionClientCapabilities
import org.mulesoft.lsp.feature.definition.ClientDefinitionClientCapabilities
import org.mulesoft.lsp.feature.diagnostic.ClientDiagnosticClientCapabilities
import org.mulesoft.lsp.feature.documenthighlight.ClientDocumentHighlightCapabilities
import org.mulesoft.lsp.feature.documentsymbol.ClientDocumentSymbolClientCapabilities
import org.mulesoft.lsp.feature.folding.ClientFoldingRangeCapabilities
import org.mulesoft.lsp.feature.formatting.{
  ClientDocumentFormattingClientCapabilities,
  ClientDocumentRangeFormattingClientCapabilities
}
import org.mulesoft.lsp.feature.hover.ClientHoverClientCapabilities
import org.mulesoft.lsp.feature.implementation.ClientImplementationClientCapabilities
import org.mulesoft.lsp.feature.link.ClientDocumentLinkClientCapabilities
import org.mulesoft.lsp.feature.reference.ClientReferenceClientCapabilities
import org.mulesoft.lsp.feature.rename.ClientRenameClientCapabilities
import org.mulesoft.lsp.feature.selection.ClientSelectionRangeCapabilities
import org.mulesoft.lsp.feature.typedefinition.ClientTypeDefinitionClientCapabilities
import org.mulesoft.lsp.textsync.ClientSynchronizationClientCapabilities

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientTextDocumentClientCapabilities extends js.Object {
  def synchronization: js.UndefOr[ClientSynchronizationClientCapabilities] = js.native

  def publishDiagnostics: js.UndefOr[ClientDiagnosticClientCapabilities] =
    js.native

  def completion: js.UndefOr[ClientCompletionClientCapabilities] =
    js.native

  def references: js.UndefOr[ClientReferenceClientCapabilities] = js.native

  def documentSymbol: js.UndefOr[ClientDocumentSymbolClientCapabilities] = js.native

  def definition: js.UndefOr[ClientDefinitionClientCapabilities] = js.native

  def implementation: js.UndefOr[ClientImplementationClientCapabilities] = js.native

  def typeDefinition: js.UndefOr[ClientTypeDefinitionClientCapabilities] = js.native

  def rename: js.UndefOr[ClientRenameClientCapabilities] = js.native

  def codeActionCapabilities: js.UndefOr[ClientCodeActionCapabilities] = js.native

  def documentLink: js.UndefOr[ClientDocumentLinkClientCapabilities] = js.native

  def documentHighlight: js.UndefOr[ClientDocumentHighlightCapabilities] = js.native

  def hover: js.UndefOr[ClientHoverClientCapabilities] = js.native

  def foldingRange: js.UndefOr[ClientFoldingRangeCapabilities] = js.native

  def selectionRange: js.UndefOr[ClientSelectionRangeCapabilities] = js.native

  def documentFormatting: js.UndefOr[ClientDocumentFormattingClientCapabilities] = js.native

  def documentRangeFormatting: js.UndefOr[ClientDocumentRangeFormattingClientCapabilities] = js.native
}

object ClientTextDocumentClientCapabilities {
  def apply(internal: TextDocumentClientCapabilities): ClientTextDocumentClientCapabilities =
    js.Dynamic
      .literal(
        synchronization = internal.synchronization.map(_.toClient).orUndefined,
        publishDiagnostics = internal.publishDiagnostics.map(_.toClient).orUndefined,
        completion = internal.completion.map(_.toClient).orUndefined,
        references = internal.references.map(_.toClient).orUndefined,
        documentSymbol = internal.documentSymbol.map(_.toClient).orUndefined,
        definition = internal.definition.map(_.toClient).orUndefined,
        implementation = internal.implementation.map(_.toClient).orUndefined,
        typeDefinition = internal.typeDefinition.map(_.toClient).orUndefined,
        rename = internal.rename.map(_.toClient).orUndefined,
        codeActionCapabilities = internal.codeActionCapabilities.map(_.toClient).orUndefined,
        documentLink = internal.documentLink.map(_.toClient).orUndefined,
        documentHighlight = internal.documentHighlight.map(_.toClient).orUndefined,
        hover = internal.hover.map(_.toClient).orUndefined,
        foldingRange = internal.foldingRange.map(_.toClient).orUndefined,
        selectionRange = internal.selectionRange.map(_.toClient).orUndefined,
        documentFormatting = internal.documentFormatting.map(_.toClient).orUndefined,
        documentRangeFormatting = internal.documentRangeFormatting.map(_.toClient).orUndefined
      )
      .asInstanceOf[ClientTextDocumentClientCapabilities]
}
// $COVERAGE-ON$
