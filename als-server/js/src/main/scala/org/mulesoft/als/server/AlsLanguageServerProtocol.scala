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
import org.mulesoft.als.vscode.{NotificationType, ParameterStructures, RequestType}
import org.mulesoft.lsp.feature.common.{ClientLocation, ClientTextDocumentIdentifier}

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportTopLevel

@JSExportTopLevel("SerializationEventNotification")
object SerializationEventNotification {
  val `type`: NotificationType[ClientSerializationResult] =
    new NotificationType[ClientSerializationResult]("serializeJSONLD", ParameterStructures.auto)
}

@JSExportTopLevel("UpdateConfigurationNotification")
object UpdateClientConfigurationNotification {
  val `type`: NotificationType[ClientUpdateConfigurationParams] =
    new NotificationType[ClientUpdateConfigurationParams]("updateConfiguration", ParameterStructures.auto)
}

@JSExportTopLevel("FilesInProjectEventNotification")
object FilesInProjectEventNotification {
  val `type`: NotificationType[ClientFilesInProjectParams] =
    new NotificationType[ClientFilesInProjectParams]("filesInProject", ParameterStructures.auto)
}

@JSExportTopLevel("AlsCapabilitiesNotification")
object AlsClientCapabilitiesNotification {
  val `type`: NotificationType[ClientAlsClientCapabilities] =
    new NotificationType[ClientAlsClientCapabilities]("notifyAlsClientCapabilities", ParameterStructures.auto)
}

@JSExportTopLevel("CleanDiagnosticTreeRequestType")
object ClientCleanDiagnosticTreeRequestType {
  val `type`: RequestType[ClientCleanDiagnosticTreeParams, js.Array[ClientAlsPublishDiagnosticsParams], js.Any] =
    new RequestType[ClientCleanDiagnosticTreeParams, js.Array[ClientAlsPublishDiagnosticsParams], js.Any](
      "cleanDiagnosticTree",
      ParameterStructures.auto)
}

@JSExportTopLevel("FileUsageRequestType")
object FileUsageRequest {
  val `type`: RequestType[ClientTextDocumentIdentifier, js.Array[ClientLocation], js.Any] =
    new RequestType[ClientTextDocumentIdentifier, js.Array[ClientLocation], js.Any]("fileUsage",
                                                                                    ParameterStructures.auto)
}

@JSExportTopLevel("ConversionRequestType")
object ClientConversionRequestType {
  val `type`: RequestType[ClientConversionParams, js.Array[ClientSerializedDocument], js.Any] =
    new RequestType[ClientConversionParams, js.Array[ClientSerializedDocument], js.Any]("conversion",
                                                                                        ParameterStructures.auto)
}

@JSExportTopLevel("SerializationRequestType")
object ClientSerializationRequestType {
  val `type`: RequestType[ClientSerializationParams, ClientSerializationResult, js.Any] =
    new RequestType[ClientSerializationParams, ClientSerializationResult, js.Any]("serialization",
                                                                                  ParameterStructures.auto)
}

@JSExportTopLevel("RenameFileActionRequestType")
object ClientCleanRenameFileActionRequestType {
  val `type`: RequestType[ClientRenameFileActionParams, ClientRenameFileActionResult, js.Any] =
    new RequestType[ClientRenameFileActionParams, ClientRenameFileActionResult, js.Any]("renameFile",
                                                                                        ParameterStructures.auto)
}
