package org.mulesoft.als.suggestions.js

import org.mulesoft.common.io.{AsyncFile, FileSystem, SyncFile}

/**
  * File system based on FS provider
  */
class FSProviderBasedFS(fsProvider: IFSProvider) extends FileSystem {

  override def syncFile(path: String): SyncFile = {

    new FSProviderBasedSyncFile(this.fsProvider, this, path)
  }

  override def asyncFile(path: String): AsyncFile = {

    new FSProviderBasedAsyncFile(this.fsProvider, this, path)
  }

  override def separatorChar: Char = {
      val str = this.fsProvider.separatorChar()
      if(str.length != 1){
          throw new Error("Separator string should contain single character")
      }
      str.charAt(0)
  }
}
