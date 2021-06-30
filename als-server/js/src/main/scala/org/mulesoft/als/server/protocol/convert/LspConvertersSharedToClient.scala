package org.mulesoft.als.server.protocol.convert

import org.mulesoft.als.configuration.AlsConfiguration
import org.mulesoft.als.server.feature.diagnostic.{CleanDiagnosticTreeClientCapabilities, CleanDiagnosticTreeOptions}
import org.mulesoft.als.server.feature.fileusage.{FileUsageClientCapabilities, FileUsageOptions}
import org.mulesoft.als.server.feature.renamefile.{
  RenameFileActionClientCapabilities,
  RenameFileActionOptions,
  RenameFileActionResult
}
import org.mulesoft.als.server.feature.serialization.{
  ConversionClientCapabilities,
  ConversionRequestOptions,
  SerializationClientCapabilities,
  SerializationResult,
  SerializationServerOptions,
  SerializedDocument
}
import org.mulesoft.als.server.feature.workspace.FilesInProjectParams
import org.mulesoft.als.server.modules.diagnostic.AlsPublishDiagnosticsParams
import org.mulesoft.als.server.protocol.actions.{
  ClientRenameFileActionClientCapabilities,
  ClientRenameFileActionResult,
  ClientRenameFileActionServerOptions
}
import org.mulesoft.als.server.protocol.configuration._
import org.mulesoft.als.server.protocol.diagnostic.{ClientAlsPublishDiagnosticsParams, ClientFilesInProjectParams}
import org.mulesoft.als.server.protocol.serialization.{ClientSerializationResult, ClientSerializedDocument}
import org.mulesoft.als.server.protocol.textsync.{
  ClientDidFocusParams,
  ClientIndexDialectParams,
  DidFocusParams,
  IndexDialectParams
}
import org.mulesoft.lsp.configuration.{FormattingOptions, _}
import org.mulesoft.lsp.feature.diagnostic.{ClientPublishDiagnosticsParams, PublishDiagnosticsParams}

import scala.language.implicitConversions
import scala.scalajs.js
import scala.scalajs.js.JSConverters._

object LspConvertersSharedToClient {

  implicit class ClientSerializationClientCapabilitiesConverter(v: SerializationClientCapabilities) {
    def toClient: ClientSerializationClientCapabilities =
      ClientSerializationClientCapabilities(v)
  }

  implicit class ClientFileUsageClientCapabilitiesConverter(v: FileUsageClientCapabilities) {
    def toClient: ClientFileUsageClientCapabilities =
      ClientFileUsageClientCapabilities(v)
  }

  implicit class ClientCleanDiagnosticTreeClientCapabilitiesConverter(v: CleanDiagnosticTreeClientCapabilities) {
    def toClient: ClientCleanDiagnosticTreeClientCapabilities =
      ClientCleanDiagnosticTreeClientCapabilities(v)
  }

  implicit class ClientSerializationRequestClientCapabilitiesConverter(v: ConversionClientCapabilities) {
    def toClient: ClientConversionClientCapabilities =
      ClientConversionClientCapabilities(v)
  }

  implicit class ClientWorkspaceClientCapabilitiesConverter(v: WorkspaceClientCapabilities) {
    def toClient: ClientWorkspaceClientCapabilities =
      ClientWorkspaceClientCapabilities(v)
  }

  implicit class ClientAlsClientCapabilitiesConverter(v: AlsClientCapabilities) {
    def toClient: ClientAlsClientCapabilities =
      ClientAlsClientCapabilities(v)
  }

  implicit class ClientAlsClientConfigurationConverter(v: AlsConfiguration) {
    def toClient: ClientAlsConfiguration =
      ClientAlsConfiguration(v)
  }

  implicit class ClientUpdateFormatOptionsParamsConverter(v: FormattingOptions) {
    def toClient: ClientFormattingOptions =
      ClientFormattingOptions(v)
  }

  implicit class ClientFilesInProjectParamsConverter(v: FilesInProjectParams) {
    def toClient: ClientFilesInProjectParams =
      ClientFilesInProjectParams(v)
  }

  implicit class ClientAlsInitializeParamsConverter(v: AlsInitializeParams) {
    def toClient: ClientAlsInitializeParams =
      ClientAlsInitializeParams(v)
  }

  implicit class ClientServerCapabilitiesConverter(v: AlsServerCapabilities) {
    def toClient: ClientAlsServerCapabilities =
      ClientAlsServerCapabilities(v)
  }

  implicit class ClientInitializeResultConverter(v: AlsInitializeResult) {
    def toClient: ClientAlsInitializeResult =
      ClientAlsInitializeResult(v)
  }

  implicit class ClientFileUsageOptionsConverter(v: FileUsageOptions) {
    def toClient: ClientFileUsageOptions =
      ClientFileUsageOptions(v)
  }

  implicit class ClientCleanDiagnosticTreeOptionsConverter(v: CleanDiagnosticTreeOptions) {
    def toClient: ClientCleanDiagnosticTreeOptions =
      ClientCleanDiagnosticTreeOptions(v)
  }

  implicit class ClientSerializationRequestOptionsConverter(v: ConversionRequestOptions) {
    def toClient: ClientConversionOptions =
      ClientConversionOptions(v)
  }

  implicit class ClientSerializationServerOptionsConverter(v: SerializationServerOptions) {
    def toClient: ClientSerializationServerOptions =
      ClientSerializationServerOptions(v)
  }

  implicit class ClientSerializationMessageConverter(v: SerializationResult[js.Any]) {
    def toClient: ClientSerializationResult =
      ClientSerializationResult(v)
  }

  implicit class ClientDidFocusParamsConverter(v: DidFocusParams) {
    def toClient: ClientDidFocusParams =
      ClientDidFocusParams(v)
  }

  implicit class ClientIndexDialectParamsConverter(v: IndexDialectParams) {
    def toClient: ClientIndexDialectParams =
      ClientIndexDialectParams(v)
  }

  implicit class ClientSerializedDocumentConverter(v: SerializedDocument) {
    def toClient: ClientSerializedDocument = ClientSerializedDocument(v)
  }

  implicit class ClientAlsPublishDiagnosticsParamsConverter(v: AlsPublishDiagnosticsParams) {
    def toClient: ClientAlsPublishDiagnosticsParams =
      ClientAlsPublishDiagnosticsParams(v)
  }

  implicit class ClientRenameFileActionServerOptionsConverter(i: RenameFileActionOptions) {
    def toClient: ClientRenameFileActionServerOptions =
      ClientRenameFileActionServerOptions(i)
  }

  implicit class ClientRenameFileActionResultConverter(i: RenameFileActionResult) {
    def toClient: ClientRenameFileActionResult =
      ClientRenameFileActionResult(i)
  }

  implicit class ClientRenameFileActionClientCapabilitiesConverter(i: RenameFileActionClientCapabilities) {
    def toClient: ClientRenameFileActionClientCapabilities =
      ClientRenameFileActionClientCapabilities(i)
  }
}
