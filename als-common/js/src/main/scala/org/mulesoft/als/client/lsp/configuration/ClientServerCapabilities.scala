package org.mulesoft.als.client.lsp.configuration

import org.mulesoft.als.client.convert.LspConvertersSharedToClient._
import org.mulesoft.als.client.lsp.feature.codeactions.ClientCodeActionOptions
import org.mulesoft.als.client.lsp.feature.completion.ClientCompletionOptions
import org.mulesoft.lsp.configuration.ServerCapabilities

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.UndefOr

@js.native
trait ClientServerCapabilities extends js.Object {
  def textDocumentSync: UndefOr[Int]                       = js.native // todo: Right??
  def completionProvider: UndefOr[ClientCompletionOptions] = js.native
  def definitionProvider: Boolean                          = js.native
  def referencesProvider: Boolean                          = js.native
  def documentSymbolProvider: Boolean                      = js.native
//  def renameProvider: UndefOr[ClientRenameOptions] = js.native
//  def codeActionProvider: UndefOr[ClientCodeActionOptions] = js.native
//  def documentLinkProvider: UndefOr[ClientDocumentLinkOptions] = js.native
  def experimental: UndefOr[js.Object] = js.native
}

object ClientServerCapabilities {
  def apply(internal: ServerCapabilities): ClientServerCapabilities =
    js.Dynamic
      .literal(
        textDocumentSync = internal.textDocumentSync.collect { case Left(l) => l.id }.orUndefined,
        completionProvider = internal.completionProvider.map(_.toClient).orUndefined,
        definitionProvider = internal.definitionProvider,
        referencesProvider = internal.referencesProvider,
        documentSymbolProvider = internal.documentSymbolProvider,
//        renameProvider = internal.renameProvider.map(_.toClient).orUndefined,
//        codeActionProvider = internal.codeActionProvider.map(_.toClient).orUndefined,
//        documentLinkProvider = internal.documentLinkProvider.map(_.toClient).orUndefined,
        experimental = internal.experimental.collect { case js: js.Object => js }.orUndefined
      )
      .asInstanceOf[ClientServerCapabilities]
}
