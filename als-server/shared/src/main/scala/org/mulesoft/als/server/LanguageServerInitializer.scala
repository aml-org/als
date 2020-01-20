package org.mulesoft.als.server

import org.mulesoft.lsp.configuration._
import org.mulesoft.lsp.feature.codeactions.CodeActionConfigType
import org.mulesoft.lsp.feature.completion.CompletionConfigType
import org.mulesoft.lsp.feature.definition.DefinitionConfigType
import org.mulesoft.lsp.feature.diagnostic.CleanDiagnosticTreeConfigType
import org.mulesoft.lsp.feature.documentsymbol.DocumentSymbolConfigType
import org.mulesoft.lsp.feature.link.DocumentLinkConfigType
import org.mulesoft.lsp.feature.reference.ReferenceConfigType
import org.mulesoft.lsp.feature.rename.RenameConfigType
import org.mulesoft.lsp.feature.serialization.SerializationConfigType
import org.mulesoft.lsp.textsync.TextDocumentSyncConfigType
import org.mulesoft.lsp.{ConfigType, Initializable}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class LanguageServerInitializer(private val configMap: ConfigMap, private val initializables: Seq[Initializable]) {

  private def applyCapabilitiesConfig(clientCapabilities: ClientCapabilities): ServerCapabilities = {
    val textDocument = clientCapabilities.textDocument
    ServerCapabilities(
      applyConfig(TextDocumentSyncConfigType, textDocument.flatMap(_.synchronization)),
      applyConfig(CompletionConfigType, textDocument.flatMap(_.completion)),
      applyConfig(DefinitionConfigType, textDocument.flatMap(_.definition)).isDefined,
      applyConfig(ReferenceConfigType, textDocument.flatMap(_.references)).isDefined,
      applyConfig(DocumentSymbolConfigType, textDocument.flatMap(_.documentSymbol)).isDefined,
      applyConfig(RenameConfigType, textDocument.flatMap(_.rename)),
      applyConfig(CodeActionConfigType, textDocument.flatMap(_.codeActionCapabilities)),
      applyConfig(DocumentLinkConfigType, textDocument.flatMap(_.documentLink))
    )
  }

  private def applyConfig[C, O](configType: ConfigType[C, O], config: Option[C]): Option[O] = {
    configMap(configType).map(_.applyConfig(config))
  }

  def applyAlsCapabilities(clientCapabilities: AlsClientCapabilities): AlsServerCapabilities = {
    AlsServerCapabilities(
      applyConfig(SerializationConfigType, clientCapabilities.serialization),
      applyConfig(CleanDiagnosticTreeConfigType, clientCapabilities.cleanDiagnosticTree)
    )
  }

  def initialize(params: InitializeParams): Future[InitializeResult] = {
    val serverCapabilities = applyCapabilitiesConfig(params.capabilities)

    Future
      .sequence(initializables.map(_.initialize()))
      .map(_ => InitializeResult(serverCapabilities))
  }
}
