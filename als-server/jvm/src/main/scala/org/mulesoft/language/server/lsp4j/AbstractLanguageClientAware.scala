package org.mulesoft.language.server.lsp4j

import org.eclipse.lsp4j.services.{LanguageClient, LanguageClientAware}
import org.mulesoft.language.common.logger.Logger

import scala.collection.mutable

trait AbstractLanguageClientAware extends LanguageClientAware with Logger {
  val clients: mutable.LinkedHashSet[LanguageClient] = mutable.LinkedHashSet()

  override def connect(client: LanguageClient): Unit = {
    debug(s"Client connected: $client", "AbstractLanguageClientAware", "connect")
    clients.add(client)
  }
}
