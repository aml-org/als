package org.mulesoft.language.server.lsp4j

import org.eclipse.lsp4j.{DidChangeConfigurationParams, DidChangeWatchedFilesParams}
import org.eclipse.lsp4j.services.WorkspaceService

class WorkspaceServiceImpl extends WorkspaceService {
  override def didChangeConfiguration(params: DidChangeConfigurationParams): Unit = {}

  override def didChangeWatchedFiles(params: DidChangeWatchedFilesParams): Unit = {}
}
