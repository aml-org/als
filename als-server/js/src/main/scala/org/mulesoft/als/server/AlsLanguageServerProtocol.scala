package org.mulesoft.als.server

import org.mulesoft.als.server.protocol.actions.{ClientRenameFileActionParams, ClientRenameFileActionResult}
import org.mulesoft.als.server.protocol.configuration.{ClientAlsClientCapabilities, ClientUpdateConfigurationParams}
import org.mulesoft.als.server.protocol.diagnostic.{
  ClientAlsPublishDiagnosticsParams,
  ClientCleanDiagnosticTreeParams,
  ClientFilesInProjectParams
}
import org.mulesoft.als.server.protocol.serialization.{
  ClientConversionParams,
  ClientSerializationParams,
  ClientSerializationResult,
  ClientSerializedDocument
}
import org.mulesoft.als.vscode.{NotificationType, RequestType}
import org.mulesoft.lsp.feature.common.{ClientLocation, ClientTextDocumentIdentifier}

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportTopLevel

@JSExportTopLevel("SerializationEventNotification")
object SerializationEventNotification {
  val `type`: NotificationType[ClientSerializationResult, js.Any] =
    new NotificationType[ClientSerializationResult, js.Any]("SerializeJSONLD")
}

@JSExportTopLevel("UpdateConfigurationNotification")
object UpdateClientConfigurationNotification {
  val `type`: NotificationType[ClientUpdateConfigurationParams, js.Any] =
    new NotificationType[ClientUpdateConfigurationParams, js.Any]("UpdateConfiguration")
}

@JSExportTopLevel("FilesInProjectEventNotification")
object FilesInProjectEventNotification {
  val `type`: NotificationType[ClientFilesInProjectParams, js.Any] =
    new NotificationType[ClientFilesInProjectParams, js.Any]("FilesInProject")
}
@JSExportTopLevel("AlsCapabilitiesNotification")
object AlsClientCapabilitiesNotification {
  val `type`: NotificationType[ClientAlsClientCapabilities, js.Any] =
    new NotificationType[ClientAlsClientCapabilities, js.Any]("NotifyAlsClientCapabilities")
}

@JSExportTopLevel("CleanDiagnosticTreeRequestType")
object ClientCleanDiagnosticTreeRequestType {
  val `type`
    : RequestType[ClientCleanDiagnosticTreeParams, js.Array[ClientAlsPublishDiagnosticsParams], js.Any, js.Any] =
    new RequestType[ClientCleanDiagnosticTreeParams, js.Array[ClientAlsPublishDiagnosticsParams], js.Any, js.Any](
      "CleanDiagnosticTree")
}

@JSExportTopLevel("FileUsageRequestType")
object FileUsageRequest {
  val `type`: RequestType[ClientTextDocumentIdentifier, js.Array[ClientLocation], js.Any, js.Any] =
    new RequestType[ClientTextDocumentIdentifier, js.Array[ClientLocation], js.Any, js.Any]("FileUsage")
}

@JSExportTopLevel("ConversionRequestType")
object ClientConversionRequestType {
  val `type`: RequestType[ClientConversionParams, js.Array[ClientSerializedDocument], js.Any, js.Any] =
    new RequestType[ClientConversionParams, js.Array[ClientSerializedDocument], js.Any, js.Any]("Conversion")
}

@JSExportTopLevel("SerializationRequestType")
object ClientSerializationRequestType {
  val `type`: RequestType[ClientSerializationParams, ClientSerializationResult, js.Any, js.Any] =
    new RequestType[ClientSerializationParams, ClientSerializationResult, js.Any, js.Any]("Serialization")
}

@JSExportTopLevel("RenameFileActionRequestType")
object ClientCleanRenameFileActionRequestType {
  val `type`: RequestType[ClientRenameFileActionParams, ClientRenameFileActionResult, js.Any, js.Any] =
    new RequestType[ClientRenameFileActionParams, ClientRenameFileActionResult, js.Any, js.Any]("RenameFile")
}
