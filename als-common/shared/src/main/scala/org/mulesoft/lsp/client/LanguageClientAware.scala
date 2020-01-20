package org.mulesoft.lsp.client

import scala.scalajs.js.annotation.JSExportAll

@JSExportAll
trait LspLanguageClientAware {
  def connect(languageClient: LspLanguageClient): Unit
}

@JSExportAll
trait AlsLanguageClientAware[S] {
  def connectAls(languageClient: AlsLanguageClient[S]): Unit
}
