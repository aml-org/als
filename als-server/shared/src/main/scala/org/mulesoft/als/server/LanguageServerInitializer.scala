package org.mulesoft.als.server

import org.mulesoft.als.server.feature.diagnostic.CleanDiagnosticTreeConfigType
import org.mulesoft.als.server.feature.serialization.{SerializationConfigType, ConversionConfigType}
import org.mulesoft.als.server.protocol.configuration.{
  AlsClientCapabilities,
  AlsInitializeParams,
  AlsInitializeResult,
  AlsServerCapabilities
}
import org.mulesoft.lsp.feature.codeactions.CodeActionConfigType
import org.mulesoft.lsp.feature.completion.CompletionConfigType
import org.mulesoft.lsp.feature.definition.DefinitionConfigType
import org.mulesoft.lsp.feature.documentsymbol.DocumentSymbolConfigType
import org.mulesoft.lsp.feature.link.DocumentLinkConfigType
import org.mulesoft.lsp.feature.reference.ReferenceConfigType
import org.mulesoft.lsp.feature.rename.RenameConfigType
import org.mulesoft.lsp.textsync.TextDocumentSyncConfigType
import org.mulesoft.lsp.{ConfigType, Initializable}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class LanguageServerInitializer(private val configMap: ConfigMap, private val initializables: Seq[Initializable]) {

  private def applyCapabilitiesConfig(clientCapabilities: AlsClientCapabilities): AlsServerCapabilities = {
    val textDocument = clientCapabilities.textDocument
    AlsServerCapabilities(
      applyConfig(TextDocumentSyncConfigType, textDocument.flatMap(_.synchronization)),
      applyConfig(CompletionConfigType, textDocument.flatMap(_.completion)),
      applyConfig(DefinitionConfigType, textDocument.flatMap(_.definition)).isDefined,
      applyConfig(ReferenceConfigType, textDocument.flatMap(_.references)).isDefined,
      applyConfig(DocumentSymbolConfigType, textDocument.flatMap(_.documentSymbol)).isDefined,
      applyConfig(RenameConfigType, textDocument.flatMap(_.rename)),
      applyConfig(CodeActionConfigType, textDocument.flatMap(_.codeActionCapabilities)),
      applyConfig(DocumentLinkConfigType, textDocument.flatMap(_.documentLink)),
      None,
      None,
      applyConfig(SerializationConfigType, clientCapabilities.serialization),
      applyConfig(CleanDiagnosticTreeConfigType, clientCapabilities.cleanDiagnosticTree),
      applyConfig(ConversionConfigType, clientCapabilities.conversion)
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
