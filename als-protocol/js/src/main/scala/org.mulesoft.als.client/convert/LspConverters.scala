package org.mulesoft.als.client.convert

import org.mulesoft.als.client.lsp.{Command, CompletionItem}
import org.mulesoft.lsp

import scala.language.implicitConversions

object LspConverters {
  implicit def toClientCompletionItem(item: lsp.feature.completion.CompletionItem): CompletionItem =
    new CompletionItem(item)

  implicit def toClientCommand(command: lsp.command.Command): Command =
    new Command(command)
}
