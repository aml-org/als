package org.mulesoft.als.suggestions.js

import org.mulesoft.common.io.{AsyncFile, FileSystem, Id, SyncFile}
import scala.concurrent.ExecutionContext.Implicits.global


import scala.concurrent.Future

class FSProviderBasedAsyncFile(fsProvider: IFSProvider,
                               override val fileSystem: FSProviderBasedFS,
                               url: String)
  extends AsyncFile {

  override def delete: Future[Unit] = ???

  override def mkdir: Future[Unit] = ???

  override def write(data: CharSequence, encoding: String): Future[Unit] = ???

  override def sync: SyncFile = new FSProviderBasedSyncFile(this.fsProvider, this.fileSystem,
    this.url)

  override def list: Future[Array[String]] = {

    this.fsProvider.readDirAsync(this.url).toFuture.map(sequence=>sequence.toArray)
  }

  override def read(encoding: String): Future[CharSequence] = {

    this.fsProvider.contentAsync(this.url).toFuture
  }

  override def exists: Future[Boolean] = {

    this.fsProvider.existsAsync(this.url).toFuture
  }

  override def isDirectory: Future[Boolean] = {

    this.fsProvider.isDirectoryAsync(this.url).toFuture
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

    this.fsProvider.dirName(this.url)
  }

  override def name: String = {

    this.fsProvider.name(this.url)
  }
}
