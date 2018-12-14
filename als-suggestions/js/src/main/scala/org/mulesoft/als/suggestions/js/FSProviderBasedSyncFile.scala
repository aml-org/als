package org.mulesoft.als.suggestions.js

import org.mulesoft.common.io.{AsyncFile, FileSystem, Id, SyncFile}
import scala.concurrent.ExecutionContext.Implicits.global


class FSProviderBasedSyncFile(fsProvider: IFSProvider,
                              override val fileSystem: FSProviderBasedFS,
                              url: String)
  extends SyncFile {

  override def delete: Id[Unit] = ???

  override def mkdir: Id[Unit] = ???

  override def write(data: CharSequence, encoding: String): Id[Unit] = ???

  override def async: AsyncFile = new FSProviderBasedAsyncFile(this.fsProvider, this.fileSystem,
    this.url)

  override def list: Id[Array[String]] = {

    this.fsProvider.readDir(this.url).toArray
  }

  override def read(encoding: String): Id[CharSequence] = {

    this.fsProvider.content(this.url)
  }

  override def exists: Id[Boolean] = {

    this.fsProvider.exists(this.url)
  }

  override def isDirectory: Id[Boolean] = {

    this.fsProvider.isDirectory(this.url)
  }

  override def isFile: Id[Boolean] = {

    !this.fsProvider.isDirectory(this.url)
  }

  override def path: String = {

    this.url
  }

  override def parent: String = {

    this.fsProvider.dirName(this.url)
  }

  override def name: String = {

    this.fsProvider.name(this.url)
  }
}
