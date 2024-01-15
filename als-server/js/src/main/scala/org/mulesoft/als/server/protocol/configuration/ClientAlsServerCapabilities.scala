package org.mulesoft.als.server.protocol.configuration

import org.mulesoft.als.server.protocol.actions.ClientRenameFileActionServerOptions
import org.mulesoft.als.server.protocol.convert.LspConvertersSharedToClient._
import org.mulesoft.als.server.protocol.diagnostic.ClientCustomValidationOptions
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
  def cleanDiagnosticTree: UndefOr[ClientCleanDiagnosticTreeOptions]             = js.native
  def fileUsage: UndefOr[ClientFileUsageOptions]                                 = js.native
  def conversion: UndefOr[ClientConversionOptions]                               = js.native
  def documentHighlightProvider: UndefOr[Boolean]                                = js.native
  def hoverProvider: UndefOr[Boolean]                                            = js.native
  def foldingRangeProvider: UndefOr[Boolean]                                     = js.native
  def renameFileAction: UndefOr[ClientRenameFileActionServerOptions]             = js.native
  def selectionRangeProvider: UndefOr[Boolean]                                   = js.native
  def documentFormattingProvider: UndefOr[Boolean]                               = js.native
  def documentRangeFormattingProvider: UndefOr[Boolean]                          = js.native
  def workspaceConfiguration: UndefOr[ClientWorkspaceConfigurationServerOptions] = js.native
  def customValidations: UndefOr[ClientCustomValidationOptions]                  = js.native
  def hotReload: UndefOr[Boolean]                                                = js.native
  def newCachingLogic: UndefOr[Boolean]                                          = js.native
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
        definitionProvider = internal.definitionProvider
          .map(eitherToUnionWithMapping(_.booleanValue(), _.toClient))
          .orUndefined
          .asInstanceOf[js.Any],
        implementationProvider = internal.implementationProvider
          .map(eitherToUnionWithMapping(_.booleanValue(), _.toClient))
          .orUndefined
          .asInstanceOf[js.Any],
        typeDefinitionProvider = internal.typeDefinitionProvider
          .map(eitherToUnionWithMapping(_.booleanValue(), _.toClient))
          .orUndefined
          .asInstanceOf[js.Any],
        referencesProvider = internal.referencesProvider
          .map(eitherToUnionWithMapping(_.booleanValue(), _.toClient))
          .orUndefined
          .asInstanceOf[js.Any],
        documentSymbolProvider = internal.documentSymbolProvider
          .map(eitherToUnionWithMapping(_.booleanValue(), _.toClient))
          .orUndefined
          .asInstanceOf[js.Any],
        renameProvider = internal.renameProvider.map(_.toClient).orUndefined,
        codeActionProvider = internal.codeActionProvider.map(_.toClient).orUndefined,
        documentLinkProvider = internal.documentLinkProvider.map(_.toClient).orUndefined,
        workspace = internal.workspace.map(_.toClient).orUndefined,
        experimental = internal.experimental.collect { case js: js.Object =>
          js
        }.orUndefined,
        serialization = internal.serialization.map(_.toClient).orUndefined,
        cleanDiagnosticTree = internal.cleanDiagnostics.map(_.toClient).orUndefined,
        fileUsage = internal.fileUsage.map(_.toClient).orUndefined,
        conversion = internal.conversion.map(_.toClient).orUndefined,
        documentHighlightProvider = internal.documentHighlightProvider.orUndefined,
        hoverProvider = internal.hoverProvider.orUndefined,
        foldingRangeProvider = internal.foldingRangeProvider.orUndefined,
        renameFileAction = internal.renameFileAction.map(_.toClient).orUndefined,
        selectionRangeProvider = internal.selectionRange
          .map(eitherToUnionWithMapping(_.booleanValue(), _.toClient))
          .orUndefined
          .asInstanceOf[js.Any],
        documentFormattingProvider = internal.documentFormattingProvider
          .map(eitherToUnionWithMapping(_.booleanValue(), _.toClient))
          .orUndefined
          .asInstanceOf[js.Any],
        documentRangeFormattingProvider = internal.documentRangeFormattingProvider
          .map(eitherToUnionWithMapping(_.booleanValue(), _.toClient))
          .orUndefined
          .asInstanceOf[js.Any],
        workspaceConfiguration = internal.workspaceConfiguration.map(_.toClient).orUndefined,
        customValidations = internal.customValidations.map(_.toClient).orUndefined,
        hotReload = internal.hotReload.orUndefined,
        newCachingLogic = internal.newCachingLogic.orUndefined
      )
      .asInstanceOf[ClientAlsServerCapabilities]
}

// $COVERAGE-ON$
