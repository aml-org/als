package org.mulesoft.als.server.lsp4j

import org.eclipse.lsp4j.services.WorkspaceService
import org.eclipse.lsp4j.{DidChangeConfigurationParams, DidChangeWatchedFilesParams, ExecuteCommandParams}
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.lsp.workspace.{ExecuteCommandParams => InternalCommandParams}

import java.util.concurrent.CompletableFuture
import scala.collection.JavaConverters._
import scala.compat.java8.FutureConverters._

class WorkspaceServiceImpl(private val inner: LanguageServer) extends WorkspaceService {
  private val workspaceService = inner.workspaceService

  override def didChangeConfiguration(params: DidChangeConfigurationParams): Unit = {}

  override def didChangeWatchedFiles(params: DidChangeWatchedFilesParams): Unit = {}

  override def executeCommand(params: ExecuteCommandParams): CompletableFuture[Object] = {
    val internal = InternalCommandParams(params.getCommand, params.getArguments.asScala.toList.map(_.toString))
    workspaceService.executeCommand(internal).toJava.toCompletableFuture
  }
}
