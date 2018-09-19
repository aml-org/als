// $COVERAGE-OFF$
package org.mulesoft.language.server.core.platform

import org.mulesoft.common.io.{AsyncFile, FileSystem, Id, SyncFile}
import org.mulesoft.language.server.core.connections.IServerConnection

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}

class ConnectionBasedAsyncFile(connection: IServerConnection,
                               override val fileSystem: ConnectionBasedFS,
                               url: String)
  extends AsyncFile {

  override def delete: Future[Unit] = ???

  override def mkdir: Future[Unit] = ???

  override def write(data: CharSequence, encoding: String): Future[Unit] = ???

  override def sync: SyncFile = new StubSyncFile(this.connection, this.fileSystem,
    this.url)

  override def list: Future[Array[String]] = {

    this.connection.readDir(this.url).map(sequence=>sequence.toArray)
  }

  override def read(encoding: String): Future[CharSequence] = {

    val editorOption = this.fileSystem.editorManager.getEditor(this.url)

    if (editorOption.isDefined){

      Future.successful(editorOption.get.text)
    }
    else {
      this.connection.content(this.url)
    }

    this.connection.content(this.url)
  }

  override def exists: Future[Boolean] = {

    val editorOption = this.fileSystem.editorManager.getEditor(this.url)

    if (editorOption.isDefined){

      Future.successful(true)
    }
    else {
      this.connection.exists(this.url)
    }
  }

  override def isDirectory: Future[Boolean] = {

    val editorOption = this.fileSystem.editorManager.getEditor(this.url)

    if (editorOption.isDefined){

      Future.successful(false)
    }
    else {
      this.connection.isDirectory(this.url)
    }

  }

  override def isFile: Future[Boolean] = {

    this.isDirectory.map(isDir=>{
      !isDir
    })
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