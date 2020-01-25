package org.mulesoft.lsp.client

import org.mulesoft.lsp.feature.serialization.SerializationMessage

trait AlsLanguageClient[S] {
  def notifySerialization(params: SerializationMessage[S]): Unit
}