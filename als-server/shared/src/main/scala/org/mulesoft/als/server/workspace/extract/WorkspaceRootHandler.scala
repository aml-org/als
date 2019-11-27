package org.mulesoft.als.server.workspace.extract

import amf.core.remote.Platform

class WorkspaceRootHandler(platform: Platform) {

  /**
    * @key: recognized config file
    * @value: mainFile extractor for this specific file
    */
  private val readers: List[ConfigReader] =
    List(ExchangeConfigReader)

  def extractMainFile(dir: String): Option[WorkspaceConf] = {
    val mains = readers.flatMap { r =>
      r.readRoot(dir, platform)
    }
    mains.headOption
  }
}
