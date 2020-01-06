package org.mulesoft.als.client.lsp.configuration

import org.mulesoft.als.client.lsp.feature.completion.ClientCompletionClientCapabilities
import org.mulesoft.als.client.lsp.feature.diagnostic.ClientDiagnosticClientCapabilities
import org.mulesoft.als.client.lsp.feature.documentsymbol.ClientDocumentSymbolClientCapabilities
import org.mulesoft.lsp.configuration.TextDocumentClientCapabilities

import scala.scalajs.js
import org.mulesoft.als.client.convert.LspConvertersSharedToClient._
import org.mulesoft.als.client.lsp.feature.codeactions.ClientCodeActionCapabilities
import org.mulesoft.als.client.lsp.feature.definition.ClientDefinitionClientCapabilities
import org.mulesoft.als.client.lsp.feature.link.ClientDocumentLinkClientCapabilities
import org.mulesoft.als.client.lsp.feature.reference.ClientReferenceClientCapabilities
import org.mulesoft.als.client.lsp.feature.rename.ClientRenameClientCapabilities

import scala.scalajs.js.JSConverters._

@js.native
trait ClientTextDocumentClientCapabilities extends js.Object {
//  def synchronization: js.UndefOr[ClientSynchronizationClientCapabilities] = js.native

  def publishDiagnostics: js.UndefOr[ClientDiagnosticClientCapabilities] =
    js.native

  def completion: js.UndefOr[ClientCompletionClientCapabilities] =
    js.native

  def references: js.UndefOr[ClientReferenceClientCapabilities] = js.native

  def documentSymbol: js.UndefOr[ClientDocumentSymbolClientCapabilities] = js.native

  def definition: js.UndefOr[ClientDefinitionClientCapabilities] = js.native

  def rename: js.UndefOr[ClientRenameClientCapabilities] = js.native

  def codeActionCapabilities: js.UndefOr[ClientCodeActionCapabilities] = js.native

  def documentLink: js.UndefOr[ClientDocumentLinkClientCapabilities] = js.native
}

object ClientTextDocumentClientCapabilities {
  def apply(internal: TextDocumentClientCapabilities): ClientTextDocumentClientCapabilities =
    js.Dynamic
      .literal(
        publishDiagnostics = internal.publishDiagnostics.map(_.toClient).orUndefined,
        completion = internal.completion.map(_.toClient).orUndefined,
        references = internal.references.map(_.toClient).orUndefined,
        documentSymbol = internal.documentSymbol.map(_.toClient).orUndefined,
        definition = internal.definition.map(_.toClient).orUndefined,
        rename = internal.rename.map(_.toClient).orUndefined,
        codeActionCapabilities = internal.codeActionCapabilities.map(_.toClient).orUndefined,
        documentLink = internal.documentLink.map(_.toClient).orUndefined
      )
      .asInstanceOf[ClientTextDocumentClientCapabilities]
}
