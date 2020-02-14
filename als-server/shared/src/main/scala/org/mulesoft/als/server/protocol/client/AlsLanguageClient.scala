package org.mulesoft.als.server.protocol.client

import org.mulesoft.als.server.feature.serialization.SerializationResult
import org.mulesoft.als.server.feature.workspace.FilesInProjectParams

trait AlsLanguageClient[S] {
  def notifySerialization(params: SerializationResult[S]): Unit

  def notifyProjectFiles(params: FilesInProjectParams): Unit
}
