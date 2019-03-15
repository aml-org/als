// $COVERAGE-OFF$
package org.mulesoft.language.server.core.platform

import org.mulesoft.common.io.{AsyncFile, FileSystem, SyncFile}
import org.mulesoft.language.server.core.connections.ServerConnection
import org.mulesoft.language.server.modules.editorManager.EditorManagerModule

/**
  * File system based on FS provider
  */
class ConnectionBasedFS(connection: ServerConnection, val editorManager: EditorManagerModule) extends FileSystem {

  override def syncFile(path: String): SyncFile = {

    new StubSyncFile(this.connection, this, path)
  }

  override def asyncFile(path: String): AsyncFile = {

    new ConnectionBasedAsyncFile(this.connection, this, path)
  }

  override def separatorChar: Char = '/'
}

// $COVERAGE-ON$
