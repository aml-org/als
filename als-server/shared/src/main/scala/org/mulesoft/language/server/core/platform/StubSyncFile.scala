// $COVERAGE-OFF$
package org.mulesoft.language.server.core.platform

import org.mulesoft.common.io.{AsyncFile, Id, SyncFile}
import org.mulesoft.language.server.core.connections.ServerConnection

/**
  * Stub for sync file based on connection.
  * Does not really work because connection does not support synchronous file access
  *
  * @param fileSystem
  * @param url
  */
class StubSyncFile(connection: ServerConnection, override val fileSystem: ConnectionBasedFS, url: String)
  extends SyncFile {

  override def delete: Id[Unit] = ???

  override def mkdir: Id[Unit] = ???

  override def write(data: CharSequence, encoding: String): Id[Unit] = ???

  override def async: AsyncFile = new ConnectionBasedAsyncFile(this.connection, this.fileSystem, this.url)

  override def list: Id[Array[String]] = {

    Array.empty[String]
  }

  override def read(encoding: String): Id[CharSequence] = {

    ""
  }

  override def exists: Id[Boolean] = {

    false
  }

  override def isDirectory: Id[Boolean] = {

    false
  }

  override def isFile: Id[Boolean] = {

    true
  }

  override def path: String = {

    this.url
  }

  override def parent: String = {

    val lastSeparatorIndex = this.url.lastIndexOf(this.fileSystem.separatorChar)

    if (lastSeparatorIndex == -1 || lastSeparatorIndex == 0) {
      ""
    } else {
      this.url.substring(0, lastSeparatorIndex)
    }
  }

  override def name: String = {

    val lastSeparatorIndex = this.url.lastIndexOf(this.fileSystem.separatorChar)

    if (lastSeparatorIndex == -1 || lastSeparatorIndex == 0 ||
      lastSeparatorIndex >= this.url.length - 1) {
      ""
    } else {
      this.url.substring(lastSeparatorIndex + 1)
    }
  }
}

// $COVERAGE-ON$
