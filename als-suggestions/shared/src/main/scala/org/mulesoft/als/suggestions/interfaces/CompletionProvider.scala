package org.mulesoft.als.suggestions.interfaces

import org.mulesoft.lsp.feature.completion.CompletionItem

import scala.concurrent.Future

trait CompletionProvider {
  def suggest(): Future[Seq[CompletionItem]]
}
