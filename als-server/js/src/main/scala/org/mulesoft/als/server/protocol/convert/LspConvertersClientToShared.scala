package org.mulesoft.als.server.protocol.convert

import org.mulesoft.als.configuration.{AlsConfiguration, ConfigurationStyle, ProjectConfigurationStyle, TemplateTypes}
import org.mulesoft.als.server.feature.configuration.UpdateConfigurationParams
import org.mulesoft.als.server.feature.diagnostic.{
  CleanDiagnosticTreeClientCapabilities,
  CleanDiagnosticTreeOptions,
  CleanDiagnosticTreeParams
}
import org.mulesoft.als.server.feature.fileusage.{FileUsageClientCapabilities, FileUsageOptions}
import org.mulesoft.als.server.feature.renamefile.{RenameFileActionClientCapabilities, RenameFileActionParams}
import org.mulesoft.als.server.feature.serialization._
import org.mulesoft.als.server.protocol.actions.{
  ClientRenameFileActionClientCapabilities,
  ClientRenameFileActionParams
}
import org.mulesoft.als.server.protocol.configuration._
import org.mulesoft.als.server.protocol.diagnostic.ClientCleanDiagnosticTreeParams
import org.mulesoft.als.server.protocol.serialization.{ClientConversionParams, ClientSerializationParams}
import org.mulesoft.als.server.protocol.textsync.{
  ClientDidFocusParams,
  ClientIndexDialectParams,
  DidFocusParams,
  IndexDialectParams
}
import org.mulesoft.lsp.configuration._
import org.mulesoft.lsp.convert.LspConvertersClientToShared.{
  TextDocumentClientCapabilitiesConverter,
  WorkspaceClientCapabilitiesConverter,
  WorkspaceFolderConverter,
  _
}

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

  implicit class AlsClientCapabilitiesConverter(v: ClientAlsClientCapabilities) {
    def toShared: AlsClientCapabilities = AlsClientCapabilities(
      v.workspace.map(_.toShared).toOption,
      v.textDocument.map(_.toShared).toOption,
      v.experimental.toOption,
      serialization = v.serialization.map(_.toShared).toOption,
      cleanDiagnosticTree = v.cleanDiagnosticTree.map(_.toShared).toOption,
      fileUsage = v.fileUsage.map(_.toShared).toOption,
      conversion = v.conversion.map(_.toShared).toOption,
      renameFileAction = v.renameFileAction.map(_.toShared).toOption
    )
  }

  implicit class ClientAlsConfigurationConverter(v: ClientAlsConfiguration) {
    def toShared: AlsConfiguration =
      AlsConfiguration(
        v.formattingOptions.toMap.map({
          case (k, value) => (k -> FormattingOptionsConverter(value).toShared)
        }),
        v.templateType match {
          case TemplateTypes.FULL   => TemplateTypes.FULL
          case TemplateTypes.SIMPLE => TemplateTypes.SIMPLE
          case TemplateTypes.NONE   => TemplateTypes.NONE
          case _                    => TemplateTypes.FULL
        },
        v.prettyPrintSerialization.toOption.getOrElse(false)
      )
  }

  implicit class ClientProjectConfigurationConverter(v: ClientProjectConfigurationStyle) {
    def toShared: ProjectConfigurationStyle =
      ProjectConfigurationStyle(ConfigurationStyle(v.style))
  }

  implicit class InitializeParamsConverter(v: ClientAlsInitializeParams) {
    def toShared: AlsInitializeParams =
      AlsInitializeParams(
        Option(v.capabilities).map(_.toShared),
        v.trace.toOption.map(TraceKind.withName),
        locale = v.locale.toOption.flatMap(Option(_)),
        rootUri = v.rootUri.toOption.flatMap(Option(_)), // (it may come as `Some(null)`)
        processId = Option(v.processId),
        workspaceFolders = Option(v.workspaceFolders).map(_.map(_.toShared).toSeq),
        rootPath = v.rootPath.toOption.flatMap(Option(_)), // (it may come as `Some(null)`)
        initializationOptions = v.initializationOptions.toOption,
        configuration = v.configuration.toOption.map(_.toShared),
        projectConfigurationStyle = v.projectConfigurationStyle.map(_.toShared)
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

  private def staticRegistrationToShared: Any => Either[Boolean, StaticRegistrationOptions] = {
    case value: Boolean     => Left(value)
    case staticRegistration => Right(staticRegistration.asInstanceOf[ClientStaticRegistrationOptions].toShared)
  }

  implicit class DidFocusParamsConverter(v: ClientDidFocusParams) {
    def toShared: DidFocusParams =
      DidFocusParams(v.uri, v.version)
  }

  implicit class IndexDialectParamsConverter(v: ClientIndexDialectParams) {
    def toShared: IndexDialectParams =
      IndexDialectParams(v.uri, v.content.toOption)
  }

  implicit class ClientConversionParamsConverter(v: ClientConversionParams) {
    def toShared: ConversionParams = ConversionParams(v.uri, v.target, v.syntax.toOption)
  }

  implicit class ClientFormattingOptionsConverter(v: ClientFormattingOptions) {
    def toShared: FormattingOptions =
      FormattingOptions(v.tabSize, v.insertSpaces.getOrElse(false))
  }

  implicit class ClientUpdateConfigurationConverter(v: ClientUpdateConfigurationParams) {
    def toShared: UpdateConfigurationParams = UpdateConfigurationParams(
      v.formattingOptions.toOption
        .map(_.toMap.map(v => v._1 -> ClientFormattingOptionsConverter(v._2).toShared)),
      v.genericOptions.toOption
        .map(_.toMap.map(v => v._1 -> v._2))
        .getOrElse(Map.empty),
      v.templateType.getOrElse("").toUpperCase match {
        case v if v == TemplateTypes.NONE || v == TemplateTypes.SIMPLE => v
        case _                                                         => TemplateTypes.FULL
      },
      v.prettyPrintSerialization.toOption.getOrElse(false)
    )
  }
  implicit class ClientSerializationParamsConverter(v: ClientSerializationParams) {
    def toShared: SerializationParams = SerializationParams(v.documentIdentifier.toShared)
  }

  implicit class ClientRenameFileActionClientCapabilitiesConverter(i: ClientRenameFileActionClientCapabilities) {
    def toShared: RenameFileActionClientCapabilities = RenameFileActionClientCapabilities(i.enabled)
  }

  implicit class ClientRenameFileActionParamsConverter(i: ClientRenameFileActionParams) {
    def toShared: RenameFileActionParams = RenameFileActionParams(i.oldDocument.toShared, i.newDocument.toShared)
  }
  // $COVERAGE-ON
}
