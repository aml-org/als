package org.mulesoft.als.server

import org.mulesoft.als.server.protocol.configuration.ClientAlsClientCapabilities
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
import org.mulesoft.lsp.feature.diagnostic.ClientPublishDiagnosticsParams

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportTopLevel

@JSExportTopLevel("SerializationEventNotification")
object SerializationEventNotification {
  val `type`: NotificationType[ClientSerializationResult, js.Any] =
    new NotificationType[ClientSerializationResult, js.Any]("SerializeJSONLD")
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
object FileUsageRequestType {
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
