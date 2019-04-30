package org.mulesoft.lsp.client

trait LanguageClientAware {
  def connect(languageClient: LanguageClient): Unit
}
