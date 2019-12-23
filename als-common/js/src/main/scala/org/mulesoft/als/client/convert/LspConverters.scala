package org.mulesoft.als.client.convert

import org.mulesoft.als.client.lsp.command.ClientCommand
import org.mulesoft.als.client.lsp.feature.completion.{
  ClientCompletionContext,
  ClientCompletionItem,
  ClientCompletionItemClientCapabilities,
  ClientCompletionItemKindClientCapabilities
}
import org.mulesoft.lsp
import org.mulesoft.lsp.feature.completion.{
  CompletionContext,
  CompletionItemClientCapabilities,
  CompletionItemKindClientCapabilities
}

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
}
