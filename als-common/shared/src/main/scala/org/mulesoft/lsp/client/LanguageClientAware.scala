package org.mulesoft.lsp.client

import scala.scalajs.js.annotation.JSExportAll

@JSExportAll
trait LanguageClientAware {
  def connect(languageClient: LanguageClient): Unit
}
