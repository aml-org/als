package org.mulesoft.als.server.lsp4j

import org.eclipse.lsp4j
import org.eclipse.lsp4j.jsonrpc.messages.{Either => JEither}
import org.mulesoft.als.configuration.{AlsConfiguration, TemplateTypes}
import org.mulesoft.als.server.feature.configuration.UpdateConfigurationParams
import org.mulesoft.als.server.feature.configuration.workspace.{
  GetWorkspaceConfigurationParams,
  WorkspaceConfigurationClientCapabilities
}
import org.mulesoft.als.server.feature.diagnostic.{
  CleanDiagnosticTreeClientCapabilities,
  CleanDiagnosticTreeParams,
  CustomValidationClientCapabilities
}
import org.mulesoft.als.server.feature.fileusage.FileUsageClientCapabilities
import org.mulesoft.als.server.feature.renamefile.{RenameFileActionClientCapabilities, RenameFileActionParams}
import org.mulesoft.als.server.feature.serialization._
import org.mulesoft.als.server.protocol.configuration.{
  AlsClientCapabilities,
  AlsInitializeParams,
  AlsInitializeResult,
  AlsServerCapabilities
}
import org.mulesoft.lsp.LspConversions.{
  completionOptions,
  documentLinkOptions,
  eitherCodeActionProviderOptions,
  eitherRenameOptions,
  textDocumentClientCapabilities,
  textDocumentIdentifier,
  textDocumentSyncKind,
  textDocumentSyncOptions,
  traceKind,
  workDoneProgressOptions,
  workspaceClientCapabilities,
  workspaceFolder,
  workspaceServerCapabilities
}
import org.mulesoft.lsp.configuration.FormattingOptions

import java.util.{List => JList}
import scala.collection.JavaConverters._
import scala.language.implicitConversions

object LspConversions {

  implicit def either[A, B, C, D](either: JEither[A, B], leftTo: A => C, rightTo: B => D): Either[C, D] =
    if (either.isLeft) Left(leftTo(either.getLeft))
    else Right(rightTo(either.getRight))

  implicit def seq[A, B](list: JList[A], mapper: A => B): Seq[B] =
    list.asScala.map(mapper)

  def booleanOrFalse(value: java.lang.Boolean): Boolean =
    !(value == null) && value

  implicit def clientCapabilities(capabilities: extension.AlsClientCapabilities): AlsClientCapabilities = {
    AlsClientCapabilities(
      Option(capabilities.getWorkspace).map(workspaceClientCapabilities),
      Option(capabilities.getTextDocument).map(textDocumentClientCapabilities),
      Option(capabilities.getExperimental),
      Option(capabilities.getSerialization).map(s => SerializationClientCapabilities(s.getAcceptsNotification)),
      Option(capabilities.getCleanDiagnosticTree).map(s =>
        CleanDiagnosticTreeClientCapabilities(s.getEnabledCleanDiagnostic)
      ),
      Option(capabilities.getFileUsage).map(s => FileUsageClientCapabilities(s.getEnabledFileUsage)),
      Option(capabilities.getConversion).map(c => conversionClientCapabilities(c)),
      Option(capabilities.getRenameFileAction).map(r => RenameFileActionClientCapabilities(r.getEnabled)),
      Option(capabilities.getWorkspaceConfiguration).map(r => WorkspaceConfigurationClientCapabilities(r.canGet)),
      Option(capabilities.getCustomValidations).map(r => CustomValidationClientCapabilities(r.isEnabled))
    )
  }
  implicit def formattingOptions(formattingOptions: extension.AlsFormattingOptions): FormattingOptions = {
    FormattingOptions(
      formattingOptions.getTabSize,
      formattingOptions.preferSpaces()
    )
  }

  implicit def alsConfiguration(alsConfiguration: extension.AlsConfiguration): AlsConfiguration = {
    if (alsConfiguration == null) AlsConfiguration()
    else
      AlsConfiguration(
        alsConfiguration.getFormattingOptions.asScala.toMap.map(a => a._1 -> formattingOptions(a._2)),
        templateTypeFromString(alsConfiguration.getTemplateType)
      )
  }

  private def templateTypeFromString(templateType: String) =
    if (templateType == null) TemplateTypes.FULL
    else
      templateType.toUpperCase match {
        case v if v == TemplateTypes.NONE || v == TemplateTypes.SIMPLE || v == TemplateTypes.FULL => v
        case _                                                                                    => TemplateTypes.BOTH
      }

  implicit def alsInitializeParams(params: extension.AlsInitializeParams): AlsInitializeParams =
    Option(params).map { p =>
      AlsInitializeParams(
        Option(p.getCapabilities).map(clientCapabilities),
        Option(p.getTrace).map(traceKind),
        locale = Option(p.getLocale),
        rootUri = Option(p.getRootUri),
        processId = Option(p.getProcessId),
        workspaceFolders = Option(p.getWorkspaceFolders).map(wf => seq(wf, workspaceFolder)),
        rootPath = Option(p.getRootPath),
        initializationOptions = Option(p.getInitializationOptions),
        configuration = Option(p.getConfiguration),
        hotReload = Option(p.getHotReload),
        newCachingLogic = Option(p.getNewCachingLogic)
      )
    } getOrElse AlsInitializeParams.default

  implicit def serverCapabilities(result: lsp4j.ServerCapabilities): AlsServerCapabilities =
    if (result == null) AlsServerCapabilities.empty
    else
      AlsServerCapabilities(
        Option(result.getTextDocumentSync)
          .map(either(_, textDocumentSyncKind, textDocumentSyncOptions)),
        Option(result.getCompletionProvider).map(completionOptions),
        Option(result.getDefinitionProvider)
          .map(either(_, booleanOrFalse, workDoneProgressOptions)),
        Option(result.getImplementationProvider)
          .map(either(_, booleanOrFalse, workDoneProgressOptions)),
        Option(result.getTypeDefinitionProvider)
          .map(either(_, booleanOrFalse, workDoneProgressOptions)),
        Option(result.getReferencesProvider)
          .map(either(_, booleanOrFalse, workDoneProgressOptions)),
        Option(result.getDocumentSymbolProvider)
          .map(either(_, booleanOrFalse, workDoneProgressOptions)),
        Option(result.getRenameProvider).flatMap(eitherRenameOptions),
        Option(result.getCodeActionProvider)
          .flatMap(eitherCodeActionProviderOptions),
        Option(result.getDocumentLinkProvider),
        Option(result.getWorkspace),
        Option(result.getExperimental),
        foldingRangeProvider = Option(result.getFoldingRangeProvider).map(_.getLeft),
        documentFormattingProvider = Option(result.getDocumentFormattingProvider)
          .map(either(_, booleanOrFalse, workDoneProgressOptions)),
        documentRangeFormattingProvider = Option(result.getDocumentRangeFormattingProvider)
          .map(either(_, booleanOrFalse, workDoneProgressOptions))
      )

  private def conversionClientCapabilities(
      result: extension.ConversionClientCapabilities
  ): ConversionClientCapabilities = {
    ConversionClientCapabilities(result.getSupported)
  }

  private def conversionConfig(result: extension.ConversionConf): ConversionConfig =
    ConversionConfig(result.getFrom, result.getTo)

  implicit def initializeResult(result: lsp4j.InitializeResult): AlsInitializeResult =
    Option(result)
      .map(r => AlsInitializeResult(serverCapabilities(r.getCapabilities)))
      .getOrElse(AlsInitializeResult.empty)

  implicit def jvmConversionParams(result: extension.ConversionParams): ConversionParams = {
    ConversionParams(
      Option(result.getUri).getOrElse(""),
      Option(result.getTarget).getOrElse(""),
      Option(result.getSyntax)
    )
  }

  implicit def jvmUpdateFormatOptionsParams(v: extension.UpdateConfigurationParams): UpdateConfigurationParams = {
    UpdateConfigurationParams(
      Option(stringFormatMapToMimeFormatMap(v.getUpdateFormatOptionsParams.asScala.toMap)),
      Option(v.getGenericOptions).map(_.asScala.toMap).getOrElse(Map.empty),
      templateTypeFromString(v.getTemplateType),
      v.shouldPrettyPrintSerialization()
    )
  }

  implicit def stringFormatMapToMimeFormatMap(
      v: Map[String, extension.AlsFormattingOptions]
  ): Map[String, FormattingOptions] = {
    v.map(v => (v._1 -> formattingOptions(v._2)))
  }

  implicit def jvmCleanDiagnosticTreeParams(result: extension.CleanDiagnosticTreeParams): CleanDiagnosticTreeParams = {
    CleanDiagnosticTreeParams(textDocumentIdentifier(result.getTextDocument))
  }

  implicit def jvmSerializationParams(params: extension.SerializationParams): SerializationParams = {
    SerializationParams(params.getDocumentIdentifier)
  }

  implicit def jvmRenameFileActionParams(params: extension.RenameFileActionParams): RenameFileActionParams =
    RenameFileActionParams(params.getOldDocument, params.getNewDocument)

  implicit def jvmGetWorkspaceConfigurationParams(
      params: extension.GetWorkspaceConfigurationParams
  ): GetWorkspaceConfigurationParams =
    GetWorkspaceConfigurationParams(params.getTextDocument)
}
