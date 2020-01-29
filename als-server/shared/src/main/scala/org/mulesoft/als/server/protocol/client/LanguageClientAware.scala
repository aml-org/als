package org.mulesoft.als.server.protocol.client

import scala.scalajs.js.annotation.JSExportAll

@JSExportAll
trait AlsLanguageClientAware[S] {
  def connectAls(languageClient: AlsLanguageClient[S]): Unit
}
