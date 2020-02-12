package org.mulesoft.als.server.protocol.client

import org.mulesoft.als.server.feature.serialization.SerializationMessage
import org.mulesoft.als.server.feature.workspace.FilesInProjectParams

trait AlsLanguageClient[S] {
  def notifySerialization(params: SerializationMessage[S]): Unit

  def notifyProjectFiles(params: FilesInProjectParams): Unit
}
