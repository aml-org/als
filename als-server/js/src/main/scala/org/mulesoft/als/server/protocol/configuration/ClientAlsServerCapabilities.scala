package org.mulesoft.als.server.protocol.configuration

import org.mulesoft.als.server.protocol.convert.LspConvertersSharedToClient._
import org.mulesoft.lsp.configuration.{ClientStaticRegistrationOptions, ClientWorkspaceServerCapabilities}
import org.mulesoft.lsp.convert.LspConvertersSharedToClient._
import org.mulesoft.lsp.feature.codeactions.ClientCodeActionOptions
import org.mulesoft.lsp.feature.completion.ClientCompletionOptions
import org.mulesoft.lsp.feature.link.ClientDocumentLinkOptions
import org.mulesoft.lsp.feature.rename.ClientRenameOptions
import org.mulesoft.lsp.textsync.ClientTextDocumentSyncOptions

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.{UndefOr, |}
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientAlsServerCapabilities extends js.Object {
  def textDocumentSync: UndefOr[Int | ClientTextDocumentSyncOptions]             = js.native
  def completionProvider: UndefOr[ClientCompletionOptions]                       = js.native
  def definitionProvider: Boolean                                                = js.native
  def implementationProvider: UndefOr[Boolean | ClientStaticRegistrationOptions] = js.native
  def typeDefinitionProvider: UndefOr[Boolean | ClientStaticRegistrationOptions] = js.native
  def referencesProvider: Boolean                                                = js.native
  def documentSymbolProvider: Boolean                                            = js.native
  def renameProvider: UndefOr[ClientRenameOptions]                               = js.native
  def codeActionProvider: UndefOr[ClientCodeActionOptions]                       = js.native
  def documentLinkProvider: UndefOr[ClientDocumentLinkOptions]                   = js.native
  def workspace: UndefOr[ClientWorkspaceServerCapabilities]                      = js.native
  def experimental: UndefOr[js.Object]                                           = js.native
  def serialization: UndefOr[ClientSerializationServerOptions]                   = js.native
  def cleanDiagnostics: UndefOr[ClientCleanDiagnosticTreeOptions]                = js.native
  def fileUsage: UndefOr[ClientFileUsageOptions]                                 = js.native
  def conversion: UndefOr[ClientConversionOptions]                               = js.native
  def documentHighlightProvider: UndefOr[Boolean]
  def hoverProvider: UndefOr[Boolean] = js.native
}

object ClientAlsServerCapabilities {
  def apply(internal: AlsServerCapabilities): ClientAlsServerCapabilities =
    js.Dynamic
      .literal(
        textDocumentSync = internal.textDocumentSync
          .map(eitherToUnionWithMapping(_.id, _.toClient))
          .orUndefined
          .asInstanceOf[js.Any],
        completionProvider = internal.completionProvider.map(_.toClient).orUndefined,
        definitionProvider = internal.definitionProvider,
        implementationProvider = internal.implementationProvider
          .map(eitherToUnionWithMapping(_.booleanValue(), _.toClient))
          .orUndefined
          .asInstanceOf[js.Any],
        typeDefinitionProvider = internal.typeDefinitionProvider
          .map(eitherToUnionWithMapping(_.booleanValue(), _.toClient))
          .orUndefined
          .asInstanceOf[js.Any],
        referencesProvider = internal.referencesProvider,
        documentSymbolProvider = internal.documentSymbolProvider,
        renameProvider = internal.renameProvider.map(_.toClient).orUndefined,
        codeActionProvider = internal.codeActionProvider.map(_.toClient).orUndefined,
        documentLinkProvider = internal.documentLinkProvider.map(_.toClient).orUndefined,
        workspace = internal.workspace.map(_.toClient).orUndefined,
        experimental = internal.experimental.collect {
          case js: js.Object => js
        }.orUndefined,
        serialization = internal.serialization.map(_.toClient).orUndefined,
        cleanDiagnostics = internal.cleanDiagnostics.map(_.toClient).orUndefined,
        fileUsage = internal.fileUsage.map(_.toClient).orUndefined,
        conversion = internal.conversion.map(_.toClient).orUndefined,
        documentHighlightProvider = internal.documentHighlightProvider.orUndefined,
        hoverProvider = internal.hoverProvider.orUndefined
      )
      .asInstanceOf[ClientAlsServerCapabilities]
}

// $COVERAGE-ON$
