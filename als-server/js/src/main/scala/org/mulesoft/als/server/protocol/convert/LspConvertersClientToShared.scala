package org.mulesoft.als.server.protocol.convert

import org.mulesoft.als.configuration.AlsFormattingOptions
import org.mulesoft.als.server.feature.diagnostic.{CleanDiagnosticTreeClientCapabilities, CleanDiagnosticTreeOptions, CleanDiagnosticTreeParams}
import org.mulesoft.als.server.feature.fileusage.{FileUsageClientCapabilities, FileUsageOptions}
import org.mulesoft.als.server.feature.configuration.UpdateConfigurationParams
import org.mulesoft.als.server.feature.serialization._
import org.mulesoft.als.server.protocol.configuration.{ClientAlsFormattingOptions, _}
import org.mulesoft.als.server.protocol.diagnostic.ClientCleanDiagnosticTreeParams
import org.mulesoft.als.server.protocol.serialization.{ClientConversionParams, ClientSerializationParams}
import org.mulesoft.als.server.protocol.textsync.{ClientDidFocusParams, ClientIndexDialectParams, DidFocusParams, IndexDialectParams}
import org.mulesoft.lsp.configuration.{ClientStaticRegistrationOptions, StaticRegistrationOptions, TraceKind}
import org.mulesoft.lsp.convert.LspConvertersClientToShared.{ClientWorkspaceServerCapabilitiesConverter, CompletionOptionsConverter, TextDocumentClientCapabilitiesConverter, TextDocumentSyncOptionsConverter, WorkspaceClientCapabilitiesConverter, WorkspaceFolderConverter}
import org.mulesoft.lsp.textsync.{ClientTextDocumentSyncOptions, TextDocumentSyncKind}
import org.mulesoft.lsp.convert.LspConvertersClientToShared._

object LspConvertersClientToShared {
  // $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

  implicit class ClientSerializationServerOptionsConverter(v: ClientSerializationServerOptions) {
    def toShared: SerializationServerOptions =
      SerializationServerOptions(v.supportsSerialization)
  }

  implicit class ClientConversionOptionsConverter(v: ClientConversionOptions) {
    def toShared: ConversionRequestOptions =
      ConversionRequestOptions(v.supported.toSeq.map(s => ConversionConfig(s.from, s.to)))
  }

  implicit class ClientFileUsageOptionsConverter(v: ClientFileUsageOptions) {
    def toShared: FileUsageOptions =
      FileUsageOptions(v.supported)
  }

  implicit class ClientCleanDiagnosticTreeOptionsConverter(v: ClientCleanDiagnosticTreeOptions) {
    def toShared: CleanDiagnosticTreeOptions =
      CleanDiagnosticTreeOptions(v.supported)
  }

  implicit class ClientCleanDiagnosticTreeParamsConverter(v: ClientCleanDiagnosticTreeParams) {
    def toShared: CleanDiagnosticTreeParams =
      CleanDiagnosticTreeParams(v.textDocument.toShared)
  }

  implicit class AlsClientCapabilitiesConverter(v: ClientAlsClientCapabilities){
    def toShared: AlsClientCapabilities = AlsClientCapabilities(
      v.workspace.map(_.toShared).toOption,
      v.textDocument.map(_.toShared).toOption,
      v.experimental.toOption,
      serialization = v.serialization.map(_.toShared).toOption,
      cleanDiagnosticTree = v.cleanDiagnosticTree.map(_.toShared).toOption,
      fileUsage = v.fileUsage.map(_.toShared).toOption,
      conversion =  v.conversion.map(_.toShared).toOption
    )
  }

  implicit class InitializeParamsConverter(v: ClientAlsInitializeParams) {
    def toShared: AlsInitializeParams =
      AlsInitializeParams(
        Option(v.capabilities).map(c => new AlsClientCapabilitiesConverter(c).toShared),
        v.trace.toOption.map(TraceKind.withName),
        v.rootUri.toOption,
        Option(v.processId),
        Option(v.workspaceFolders).map(_.map(_.toShared).toSeq),
        v.rootPath.toOption,
        v.initializationOptions.toOption,
      )
  }

  implicit class SerializationClientCapabilitiesConverter(v: ClientSerializationClientCapabilities) {
    def toShared: SerializationClientCapabilities = {
      SerializationClientCapabilities(v.acceptsNotification)
    }
  }

  implicit class ConversionConfigConverter(v: ClientConversionConfig) {
    def toShared: ConversionConfig = {
      ConversionConfig(v.from, v.to)
    }
  }

  implicit class SerializationRequestClientCapabilitiesConverter(v: ClientConversionClientCapabilities) {
    def toShared: ConversionClientCapabilities = {
      ConversionClientCapabilities(v.supported)
    }
  }

  implicit class FileUsageClientCapabilitiesConverter(v: ClientFileUsageClientCapabilities) {
    def toShared: FileUsageClientCapabilities = {
      FileUsageClientCapabilities(v.fileUsageSupport)
    }
  }

  implicit class CleanDiagnosticTreeClientCapabilitiesConverter(v: ClientCleanDiagnosticTreeClientCapabilities) {
    def toShared: CleanDiagnosticTreeClientCapabilities = {
      CleanDiagnosticTreeClientCapabilities(v.enableCleanDiagnostic)
    }
  }

  implicit class ServerCapabilitiesConverter(v: ClientAlsServerCapabilities) {
    def toShared: AlsServerCapabilities =
      AlsServerCapabilities(
        v.textDocumentSync.toOption.map((textDocumentSync: Any) => textDocumentSync match {
          case value: Int => Left(TextDocumentSyncKind(value))
          case _ => Right(textDocumentSync.asInstanceOf[ClientTextDocumentSyncOptions].toShared)
        }),
        v.completionProvider.toOption.map(_.toShared),
        v.definitionProvider,
        v.implementationProvider.toOption.map(staticRegistrationToShared),
        v.typeDefinitionProvider.toOption.map(staticRegistrationToShared),
        v.referencesProvider,
        v.documentSymbolProvider,
        None,
        None,
        None,
        v.workspace.toOption.map(_.toShared),
        v.experimental.toOption,
        v.serialization.toOption.map(_.toShared),
        v.cleanDiagnostics.toOption.map(_.toShared),
        v.fileUsage.toOption.map(_.toShared),
        v.conversion.toOption.map(_.toShared),
        v.documentHighlightProvider.toOption,
        v.hoverProvider.toOption
      )
  }

  private def staticRegistrationToShared: Any => Either[Boolean, StaticRegistrationOptions] = {
    case value: Boolean => Left(value)
    case staticRegistration => Right(staticRegistration.asInstanceOf[ClientStaticRegistrationOptions].toShared)
  }

  implicit class InitializeResultConverter(v: ClientAlsInitializeResult) {
    def toShared: AlsInitializeResult =
      AlsInitializeResult(v.capabilities.toShared)
  }

  implicit class DidFocusParamsConverter(v: ClientDidFocusParams) {
    def toShared: DidFocusParams =
      DidFocusParams(v.uri, v.version)
  }

  implicit class IndexDialectParamsConverter(v: ClientIndexDialectParams) {
    def toShared: IndexDialectParams =
      IndexDialectParams(v.uri, v.content.toOption)
  }

  implicit class ClientConversionParamsConverter(v: ClientConversionParams){
    def toShared: ConversionParams = ConversionParams(v.uri, v.target, v.syntax.toOption)
  }

  implicit class ClientAlsFormattingOptionsConverter(v: ClientAlsFormattingOptions){
    def toShared: AlsFormattingOptions = AlsFormattingOptions(v.tabSize, v.insertSpaces)
  }

  implicit class ClientUpdateConfigurationConverter(v: ClientUpdateConfigurationParams){
    def toShared: UpdateConfigurationParams = UpdateConfigurationParams(
      v.clientAlsFormattingOptions.toOption.map(_.toMap.map(v => v._1 -> v._2.toShared))
    )
  }

  implicit class ClientSerializationParamsConverter(v:ClientSerializationParams){
    def toShared: SerializationParams = SerializationParams(v.documentIdentifier.toShared)
  }
  // $COVERAGE-ON
}
