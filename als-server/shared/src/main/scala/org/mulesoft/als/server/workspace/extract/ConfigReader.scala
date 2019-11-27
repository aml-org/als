package org.mulesoft.als.server.workspace.extract

import amf.core.remote.Platform
import org.mulesoft.als.common.FileUtils
import org.mulesoft.common.io.SyncFile

trait ConfigReader {
  val configFileName: String

  def readRoot(rootPath: String, platform: Platform): Option[WorkspaceConf] = {
    val path = FileUtils.getPath(s"$rootPath/$configFileName", platform)
    val file = platform.fs.syncFile(path)
    if (file.exists) buildConfig(file, FileUtils.getPath(rootPath, platform), platform)
    else None
  }

  protected def buildConfig(file: SyncFile, path: String, platform: Platform): Option[WorkspaceConf]
}
