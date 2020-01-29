package org.mulesoft.als.server.protocol.client

import org.mulesoft.lsp.feature.serialization.SerializationMessage
import org.mulesoft.lsp.feature.workspace.FilesInProjectParams

trait AlsLanguageClient[S] {
  def notifySerialization(params: SerializationMessage[S]): Unit

  def notifyProjectFiles(params: FilesInProjectParams): Unit
}
