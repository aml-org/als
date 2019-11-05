package org.mulesoft.als.server.workspace

import org.mulesoft.common.io.SyncFile
import org.mulesoft.lsp.workspace.WorkspaceService

import scala.collection.mutable.ListBuffer

class WorkspaceManager() extends WorkspaceService {

  private val folders: ListBuffer[SyncFile] = ListBuffer()

}
