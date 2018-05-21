package org.mulesoft.language.server.core.platform

import org.mulesoft.common.io.{AsyncFile, FileSystem, SyncFile}
import org.mulesoft.language.server.core.connections.IServerConnection
import org.mulesoft.language.server.server.modules.editorManager.IEditorManagerModule

/**
  * File system based on FS provider
  */
class ConnectionBasedFS(connection: IServerConnection,
                        val editorManager: IEditorManagerModule) extends FileSystem {

  override def syncFile(path: String): SyncFile = {

    new StubSyncFile(this.connection, this, path)
  }

  override def asyncFile(path: String): AsyncFile = {

    new ConnectionBasedAsyncFile(this.connection, this, path)
  }

  override def separatorChar: Char = '/'
}

