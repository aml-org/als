package org.mulesoft.als.server.lsp4j

import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletableFuture.completedFuture

import org.mulesoft.lsp.server.{LanguageServer => lspLanguageServer}
import org.eclipse.lsp4j.services.WorkspaceService
import org.eclipse.lsp4j.{DidChangeConfigurationParams, DidChangeWatchedFilesParams, ExecuteCommandParams}
import org.mulesoft.als.server.custom.{DidFocusCommandExecutor, IndexDialectCommandExecutor}

class WorkspaceServiceImpl(private val inner: lspLanguageServer) extends WorkspaceService {
  private val textDocumentSyncConsumer = inner.textDocumentSyncConsumer

  override def didChangeConfiguration(params: DidChangeConfigurationParams): Unit = {}

  override def didChangeWatchedFiles(params: DidChangeWatchedFilesParams): Unit = {}

  override def executeCommand(params: ExecuteCommandParams): CompletableFuture[AnyRef] = {

    params.getCommand match {
      case Commands.DID_FOCUS_CHANGE_COMMAND =>
        DidFocusCommandExecutor(params.getArguments, textDocumentSyncConsumer.didFocus)
      case Commands.INDEX_DIALECT =>
        IndexDialectCommandExecutor(params.getArguments, textDocumentSyncConsumer.indexDialect)
      case _ => completedFuture("Command not recognized")
    }
  }
}

object Commands {
  val DID_FOCUS_CHANGE_COMMAND: String = "didFocusChange"
  val INDEX_DIALECT: String            = "indexDialect"
}
