package org.mulesoft.als.server

import org.mulesoft.als.server.feature.configuration.workspace.WorkspaceConfigurationConfigType
import org.mulesoft.als.server.feature.configuration.{
  UpdateConfigurationClientCapabilities,
  UpdateConfigurationConfigType
}
import org.mulesoft.als.server.feature.diagnostic.{CleanDiagnosticTreeConfigType, CustomValidationConfigType}
import org.mulesoft.als.server.feature.fileusage.FileUsageConfigType
import org.mulesoft.als.server.feature.renamefile.RenameFileConfigType
import org.mulesoft.als.server.feature.serialization.{ConversionConfigType, SerializationConfigType}
import org.mulesoft.als.server.protocol.configuration.{
  AlsClientCapabilities,
  AlsInitializeParams,
  AlsInitializeResult,
  AlsServerCapabilities
}
import org.mulesoft.lsp.configuration.DefaultWorkspaceServerCapabilities
import org.mulesoft.lsp.feature.codeactions.CodeActionConfigType
import org.mulesoft.lsp.feature.completion.CompletionConfigType
import org.mulesoft.lsp.feature.definition.DefinitionConfigType
import org.mulesoft.lsp.feature.documentFormatting.DocumentFormattingConfigType
import org.mulesoft.lsp.feature.documentRangeFormatting.DocumentRangeFormattingConfigType
import org.mulesoft.lsp.feature.documentsymbol.DocumentSymbolConfigType
import org.mulesoft.lsp.feature.folding.FoldingRangeConfigType
import org.mulesoft.lsp.feature.highlight.DocumentHighlightConfigType
import org.mulesoft.lsp.feature.hover.HoverConfigType
import org.mulesoft.lsp.feature.implementation.ImplementationConfigType
import org.mulesoft.lsp.feature.link.DocumentLinkConfigType
import org.mulesoft.lsp.feature.reference.ReferenceConfigType
import org.mulesoft.lsp.feature.rename.RenameConfigType
import org.mulesoft.lsp.feature.selectionRange.SelectionRangeConfigType
import org.mulesoft.lsp.feature.typedefinition.TypeDefinitionConfigType
import org.mulesoft.lsp.textsync.TextDocumentSyncConfigType
import org.mulesoft.lsp.{ConfigType, Initializable}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class LanguageServerInitializer(private val configMap: ConfigMap, private val initializables: Seq[Initializable]) {

  private def applyCapabilitiesConfig(clientCapabilities: AlsClientCapabilities): AlsServerCapabilities = {
    val textDocument = clientCapabilities.textDocument
    val workspace    = clientCapabilities.workspace
    val configOptions = applyConfig(
      UpdateConfigurationConfigType,
      Some(
        UpdateConfigurationClientCapabilities(
          enableUpdateFormatOptions = true,
          supportsDocumentChanges = workspace.flatMap(_.workspaceEdit).flatMap(_.documentChanges).contains(true)))
    )
    AlsServerCapabilities(
      applyConfig(TextDocumentSyncConfigType, textDocument.flatMap(_.synchronization)),
      applyConfig(CompletionConfigType, textDocument.flatMap(_.completion)),
      applyConfig(DefinitionConfigType, textDocument.flatMap(_.definition)),
      applyConfig(ImplementationConfigType, textDocument.flatMap(_.implementation)),
      applyConfig(TypeDefinitionConfigType, textDocument.flatMap(_.typeDefinition)),
      applyConfig(ReferenceConfigType, textDocument.flatMap(_.references)),
      applyConfig(DocumentSymbolConfigType, textDocument.flatMap(_.documentSymbol)),
      applyConfig(RenameConfigType, textDocument.flatMap(_.rename)),
      applyConfig(CodeActionConfigType, textDocument.flatMap(_.codeActionCapabilities)),
      applyConfig(DocumentLinkConfigType, textDocument.flatMap(_.documentLink)),
      Some(DefaultWorkspaceServerCapabilities), // Not dependant on client capabilities
      None,
      applyConfig(SerializationConfigType, clientCapabilities.serialization),
      applyConfig(CleanDiagnosticTreeConfigType, clientCapabilities.cleanDiagnosticTree),
      applyConfig(FileUsageConfigType, clientCapabilities.fileUsage),
      applyConfig(ConversionConfigType, clientCapabilities.conversion),
      applyConfig(DocumentHighlightConfigType, textDocument.flatMap(_.documentHighlight)),
      applyConfig(HoverConfigType, textDocument.flatMap(_.hover)),
      applyConfig(FoldingRangeConfigType, textDocument.flatMap(_.foldingRange)),
      applyConfig(SelectionRangeConfigType, textDocument.flatMap(_.selectionRange)),
      applyConfig(RenameFileConfigType, clientCapabilities.renameFileAction),
      configOptions,
      applyConfig(DocumentFormattingConfigType, textDocument.flatMap(_.documentFormatting)),
      applyConfig(DocumentRangeFormattingConfigType, textDocument.flatMap(_.documentRangeFormatting)),
      applyConfig(WorkspaceConfigurationConfigType, clientCapabilities.workspaceConfiguration),
      applyConfig(CustomValidationConfigType, clientCapabilities.customValidations)
    )
  }

  private def applyConfig[C, O](configType: ConfigType[C, O], config: Option[C]): Option[O] = {
    configMap(configType).map(_.applyConfig(config))
  }

  def initialize(params: AlsInitializeParams): Future[AlsInitializeResult] = {
    val serverCapabilities = applyCapabilitiesConfig(params.capabilities)

    Future
      .sequence(initializables.map(_.initialize()))
      .map(_ => AlsInitializeResult(serverCapabilities))
  }
}
