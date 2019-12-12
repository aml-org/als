package org.mulesoft.als.server.workspace.extract

import amf.core.remote.Platform

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

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