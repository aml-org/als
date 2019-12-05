package org.mulesoft.als.server.workspace.extract

import amf.core.remote.Platform
import amf.internal.environment.Environment

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class WorkspaceRootHandler(platform: Platform) {

  /**
    * @key: recognized config file
    * @value: mainFile extractor for this specific file
    */
  private val readers: List[ConfigReader] =
    List(ExchangeConfigReader)

  def extractConfiguration(dir: String): Future[Option[WorkspaceConf]] = {
    Future
      .find {
        readers.map {
          _.readRoot(dir, platform)
        }
      }(cf => cf.isDefined)
      .map(_.flatten)
  }
}