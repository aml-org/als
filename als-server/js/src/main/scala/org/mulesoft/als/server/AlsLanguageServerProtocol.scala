package org.mulesoft.als.server

import org.mulesoft.als.client.lsp.feature.diagnostic.ClientFilesInProjectMessage
import org.mulesoft.als.client.lsp.feature.serialization.ClientSerializationMessage
import org.mulesoft.als.vscode.NotificationType

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportTopLevel

@JSExportTopLevel("SerializationEventNotification")
object SerializationEventNotification {
  val `type`: NotificationType[ClientSerializationMessage, js.Any] =
    new NotificationType[ClientSerializationMessage, js.Any]("SerializeJSONLD")
}

@JSExportTopLevel("FilesInProjectEventNotification")
object FilesInProjectEventNotification {
  val `type`: NotificationType[ClientFilesInProjectMessage, js.Any] =
    new NotificationType[ClientFilesInProjectMessage, js.Any]("SerializeJSONLD")
}
