package org.mulesoft.als.client.convert

import org.mulesoft.als.client.lsp.command.ClientCommand
import org.mulesoft.als.client.lsp.edit.ClientNewFileOptions
import org.mulesoft.als.client.lsp.feature.completion.{
  ClientCompletionClientCapabilities,
  ClientCompletionContext,
  ClientCompletionItem,
  ClientCompletionItemClientCapabilities,
  ClientCompletionItemKindClientCapabilities
}
import org.mulesoft.als.client.lsp.feature.diagnostic.ClientDiagnosticClientCapabilities
import org.mulesoft.lsp
import org.mulesoft.lsp.edit.NewFileOptions
import org.mulesoft.lsp.feature.completion.{
  CompletionClientCapabilities,
  CompletionContext,
  CompletionItemClientCapabilities,
  CompletionItemKindClientCapabilities
}
import org.mulesoft.lsp.feature.diagnostic.DiagnosticClientCapabilities

import scala.language.implicitConversions

object LspConverters {
  implicit def toClientCompletionItem(item: lsp.feature.completion.CompletionItem): ClientCompletionItem =
    new ClientCompletionItem(item)

  implicit def toClientCommand(command: lsp.command.Command): ClientCommand =
    new ClientCommand(command)

  implicit def toClientCompletionItemKindClientCapabilities(
      completionItemKind: CompletionItemKindClientCapabilities): ClientCompletionItemKindClientCapabilities =
    new ClientCompletionItemKindClientCapabilities(completionItemKind)

  implicit def toClientCompletionItemClientCapabilities(
      completionItemKind: CompletionItemClientCapabilities): ClientCompletionItemClientCapabilities =
    new ClientCompletionItemClientCapabilities(completionItemKind)

  implicit def toClientCompletionContext(completionContext: CompletionContext): ClientCompletionContext =
    new ClientCompletionContext(completionContext)

  implicit def toClientDiagnosticClientCapabilities(
      diagnosticClientCapabilities: DiagnosticClientCapabilities): ClientDiagnosticClientCapabilities =
    new ClientDiagnosticClientCapabilities(diagnosticClientCapabilities)

  implicit def toClientCompletionClientCapabilities(
      completionClientCapabilities: CompletionClientCapabilities): ClientCompletionClientCapabilities =
    new ClientCompletionClientCapabilities(completionClientCapabilities)

  implicit def toClientNewFileOptions(newFileOptions: NewFileOptions): ClientNewFileOptions =
    new ClientNewFileOptions(newFileOptions)

}
