package org.mulesoft.als.server

import org.mulesoft.als.server.protocol.configuration.ClientAlsClientCapabilities
import org.mulesoft.als.server.protocol.diagnostic.{ClientCleanDiagnosticTreeParams, ClientFilesInProjectMessage}
import org.mulesoft.als.server.protocol.serialization.{
  ClientConversionParams,
  ClientSerializationMessage,
  ClientSerializedDocument
}
import org.mulesoft.als.vscode.{NotificationType, RequestType}
import org.mulesoft.lsp.feature.diagnostic.ClientPublishDiagnosticsParams

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
@JSExportTopLevel("AlsClientCapabilitiesNotification")
object AlsClientCapabilitiesNotification {
  val `type`: NotificationType[ClientAlsClientCapabilities, js.Any] =
    new NotificationType[ClientAlsClientCapabilities, js.Any]("NotifyAlsClientCapabilities")
}

@JSExportTopLevel("ClientCleanDiagnosticTreeRequestType")
object ClientCleanDiagnosticTreeRequestType {
  val `type`: RequestType[ClientCleanDiagnosticTreeParams, js.Array[ClientPublishDiagnosticsParams], js.Any, js.Any] =
    new RequestType[ClientCleanDiagnosticTreeParams, js.Array[ClientPublishDiagnosticsParams], js.Any, js.Any](
      "CleanDiagnosticTree")
}

@JSExportTopLevel("ClientConversionRequestType")
object ClientConversionRequestType {
  val `type`: RequestType[ClientConversionParams, js.Array[ClientSerializedDocument], js.Any, js.Any] =
    new RequestType[ClientConversionParams, js.Array[ClientSerializedDocument], js.Any, js.Any]("Conversion")
}
