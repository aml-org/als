package org.mulesoft.als.server.protocol.convert

import org.mulesoft.als.server.protocol.configuration._
import org.mulesoft.als.server.protocol.diagnostic.ClientFilesInProjectMessage
import org.mulesoft.als.server.protocol.serialization.ClientSerializationMessage
import org.mulesoft.als.server.protocol.textsync.{
  ClientDidFocusParams,
  ClientIndexDialectParams,
  DidFocusParams,
  IndexDialectParams
}
import org.mulesoft.lsp.configuration._
import org.mulesoft.lsp.feature.diagnostic._
import org.mulesoft.lsp.feature.serialization.{
  SerializationClientCapabilities,
  SerializationMessage,
  SerializationServerOptions
}
import org.mulesoft.lsp.feature.workspace.FilesInProjectParams

import scala.language.implicitConversions
import scala.scalajs.js

object LspConvertersSharedToClient {

  implicit class ClientSerializationClientCapabilitiesConverter(v: SerializationClientCapabilities) {
    def toClient: ClientSerializationClientCapabilities =
      ClientSerializationClientCapabilities(v)
  }

  implicit class ClientCleanDiagnosticTreeClientCapabilitiesConverter(v: CleanDiagnosticTreeClientCapabilities) {
    def toClient: ClientCleanDiagnosticTreeClientCapabilities =
      ClientCleanDiagnosticTreeClientCapabilities(v)
  }

  implicit class ClientWorkspaceClientCapabilitiesConverter(v: WorkspaceClientCapabilities) {
    def toClient: ClientWorkspaceClientCapabilities =
      ClientWorkspaceClientCapabilities(v)
  }

  implicit class ClientAlsClientCapabilitiesConverter(v: AlsClientCapabilities) {
    def toClient: ClientAlsClientCapabilities =
      ClientAlsClientCapabilities(v)
  }

  implicit class ClientFilesInProjectParamsConverter(v: FilesInProjectParams) {
    def toClient: ClientFilesInProjectMessage =
      ClientFilesInProjectMessage(v)
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

  implicit class ClientCleanDiagnosticTreeOptionsConverter(v: CleanDiagnosticTreeOptions) {
    def toClient: ClientCleanDiagnosticTreeOptions =
      ClientCleanDiagnosticTreeOptions(v)
  }

  implicit class ClientSerializationServerOptionsConverter(v: SerializationServerOptions) {
    def toClient: ClientSerializationServerOptions =
      ClientSerializationServerOptions(v)
  }

  implicit class ClientSerializationMessageConverter(v: SerializationMessage[js.Any]) {
    def toClient: ClientSerializationMessage =
      ClientSerializationMessage(v.model)
  }

  implicit class ClientDidFocusParamsConverter(v: DidFocusParams) {
    def toClient: ClientDidFocusParams =
      ClientDidFocusParams(v)
  }

  implicit class ClientIndexDialectParamsConverter(v: IndexDialectParams) {
    def toClient: ClientIndexDialectParams =
      ClientIndexDialectParams(v)
  }
}
