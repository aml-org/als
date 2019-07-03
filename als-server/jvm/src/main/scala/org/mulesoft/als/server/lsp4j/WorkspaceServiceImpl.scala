package org.mulesoft.als.server.lsp4j

import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletableFuture.completedFuture
import org.mulesoft.lsp.server.{LanguageServer => lspLanguageServer}

import org.eclipse.lsp4j.services.WorkspaceService
import org.eclipse.lsp4j.{DidChangeConfigurationParams, DidChangeWatchedFilesParams, ExecuteCommandParams}
import org.mulesoft.als.server.custom.DidFocusCommand

class WorkspaceServiceImpl(private val inner: lspLanguageServer) extends WorkspaceService with DidFocusCommand {
  private val textDocumentSyncConsumer = inner.textDocumentSyncConsumer

  override def didChangeConfiguration(params: DidChangeConfigurationParams): Unit = {}

  override def didChangeWatchedFiles(params: DidChangeWatchedFilesParams): Unit = {}

  override def executeCommand(params: ExecuteCommandParams): CompletableFuture[AnyRef] = {

    params.getCommand match {
      case DID_FOCUS_CHANGE_COMMAND => onDidFocus(params.getArguments, textDocumentSyncConsumer.didFocus)
      case _                        => completedFuture("Command not recognized")
    }
  }
}
