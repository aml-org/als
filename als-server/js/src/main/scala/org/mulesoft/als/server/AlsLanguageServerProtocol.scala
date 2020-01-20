package org.mulesoft.als.server

import org.mulesoft.als.client.lsp.configuration.ClientAlsClientCapabilities
import org.mulesoft.als.client.lsp.feature.serialization.ClientSerializationMessage
import org.mulesoft.als.vscode.NotificationType

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExportTopLevel, JSImport}

@JSExportTopLevel("AlsLanguageServerProtocol.SerializationEventNotification")
object SerializationEventNotification {
  val `type`: NotificationType[ClientSerializationMessage, js.Any] =
    new NotificationType[ClientSerializationMessage, js.Any]("SerializeJSONLD")
}

@JSExportTopLevel("AlsLanguageServerProtocol.AlsClientCapabilitiesNotification")
object AlsClientCapabilitiesNotification {
  val `type`: NotificationType[ClientAlsClientCapabilities, js.Any] =
    new NotificationType[ClientAlsClientCapabilities, js.Any]("NotifyAlsClientCapabilities")
}
