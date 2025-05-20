package org.mulesoft.als.suggestions.antlr.plugins

import org.mulesoft.lsp.feature.completion.CompletionItem

import scala.concurrent.Future

trait CompletionPlugin {
  def suggest(): Future[Seq[CompletionItem]]
}
