package org.mulesoft.als.server

import java.io.StringWriter

import org.mulesoft.als.server.client.AlsClientNotifier
import org.mulesoft.lsp.feature.serialization.SerializationMessage
import org.mulesoft.lsp.feature.workspace.FilesInProjectParams
import org.yaml.builder.{DocBuilder, JsonOutputBuilder}

abstract class SerializationProps[S](val alsClientNotifier: AlsClientNotifier[S]) {

  def newDocBuilder(): DocBuilder[S]
}
// Default
case class JvmSerializationProps(override val alsClientNotifier: AlsClientNotifier[StringWriter])
    extends SerializationProps[StringWriter](alsClientNotifier) {
  override def newDocBuilder(): DocBuilder[StringWriter] = JsonOutputBuilder()
}

object EmptyJvmSerializationProps
    extends JvmSerializationProps(new AlsClientNotifier[StringWriter] {
      override def notifyProjectFiles(params: FilesInProjectParams): Unit = {}

      override def notifySerialization(params: SerializationMessage[StringWriter]): Unit = {}
    })
