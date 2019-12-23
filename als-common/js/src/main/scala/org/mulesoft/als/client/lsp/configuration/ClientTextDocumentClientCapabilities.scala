package org.mulesoft.als.client.lsp.configuration

import org.mulesoft.als.client.convert.LspConverters._
import org.mulesoft.als.client.lsp.feature.completion.ClientCompletionClientCapabilities
import org.mulesoft.als.client.lsp.feature.diagnostic.ClientDiagnosticClientCapabilities
import org.mulesoft.lsp.configuration.TextDocumentClientCapabilities

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

@JSExportAll
@JSExportTopLevel(name = "TextDocumentClientCapabilities")
class ClientTextDocumentClientCapabilities(private val internal: TextDocumentClientCapabilities) {
  def synchronization: js.UndefOr[ClientSynchronizationClientCapabilities] = internal.synchronization.orUndefined
  def publishDiagnostics: js.UndefOr[ClientDiagnosticClientCapabilities] =
    internal.publishDiagnostics.map(toClientDiagnosticClientCapabilities).orUndefined
  def completion: js.UndefOr[ClientCompletionClientCapabilities] =
    internal.completion.map(toClientCompletionClientCapabilities).orUndefined
  def references: js.UndefOr[ClientReferenceClientCapabilities]          = internal.references.orUndefined
  def documentSymbol: js.UndefOr[ClientDocumentSymbolClientCapabilities] = internal.documentSymbol.orUndefined
  def definition: js.UndefOr[ClientDefinitionClientCapabilities]         = internal.definition.orUndefined
  def rename: js.UndefOr[ClientRenameClientCapabilities]                 = internal.rename.orUndefined
  def codeActionCapabilities: js.UndefOr[ClientCodeActionCapabilities]   = internal.codeActionCapabilities.orUndefined
  def documentLink: js.UndefOr[ClientDocumentLinkClientCapabilities]     = internal.documentLink.orUndefined
}
