package org.mulesoft.als.server.lsp4j

import org.eclipse.lsp4j
import org.eclipse.lsp4j._
import org.eclipse.lsp4j.services.WorkspaceService
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.lsp.workspace.{
  DidChangeWatchedFilesParams,
  FileChangeType,
  FileEvent,
  ExecuteCommandParams => InternalCommandParams
}

import java.util.concurrent.CompletableFuture
import scala.collection.JavaConverters._
import scala.compat.java8.FutureConverters._

class WorkspaceServiceImpl(private val inner: LanguageServer) extends WorkspaceService {
  private val workspaceService = inner.workspaceService
  private val documentService  = inner.textDocumentSyncConsumer

  override def didChangeConfiguration(params: DidChangeConfigurationParams): Unit = {}

  override def executeCommand(params: ExecuteCommandParams): CompletableFuture[Object] = {
    val internal = InternalCommandParams(params.getCommand, params.getArguments.asScala.toList.map(_.toString))
    workspaceService.executeCommand(internal).toJava.toCompletableFuture
  }

  override def didCreateFiles(params: CreateFilesParams): Unit = {
    val events: List[FileEvent] = params.getFiles.asScala.map(fc => FileEvent(fc.getUri, FileChangeType.Created)).toList
    workspaceService.didChangeWatchedFiles(
      DidChangeWatchedFilesParams(events)
    )
  }
  override def didDeleteFiles(params: DeleteFilesParams): Unit = {
    val events: List[FileEvent] = params.getFiles.asScala.map { fc =>
      documentService.deleteFile(fc.getUri)
      FileEvent(fc.getUri, FileChangeType.Deleted)
    }.toList
    workspaceService.didChangeWatchedFiles(
      DidChangeWatchedFilesParams(events)
    )
  }

  override def didRenameFiles(params: RenameFilesParams): Unit = {
    val events: List[FileEvent] =
      params.getFiles.asScala.map { fc =>
        documentService.changeFile(fc.getOldUri, fc.getNewUri)
        FileEvent(fc.getNewUri, FileChangeType.Changed)
      }.toList
    workspaceService.didChangeWatchedFiles(
      DidChangeWatchedFilesParams(events)
    )
  }

  override def didChangeWatchedFiles(params: lsp4j.DidChangeWatchedFilesParams): Unit = {
    val events: List[FileEvent] =
      params.getChanges.asScala.map(fc => FileEvent(fc.getUri, FileChangeType(fc.getType.getValue))).toList
    workspaceService.didChangeWatchedFiles(
      DidChangeWatchedFilesParams(events)
    )
  }
}
