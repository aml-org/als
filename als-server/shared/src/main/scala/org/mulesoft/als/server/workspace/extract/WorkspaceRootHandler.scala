package org.mulesoft.als.server.workspace.extract

import amf.core.remote.Platform
import amf.internal.environment.Environment
import org.mulesoft.als.server.logger.Logger

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class WorkspaceRootHandler(platform: Platform, environment: Environment) {

  /**
    * @key: recognized config file
    * @value: mainFile extractor for this specific file
    */
  private val readers: List[ConfigReader] =
    List(ExchangeConfigReader)

  def extractConfiguration(dir: String, logger: Logger): Future[Option[WorkspaceConf]] =
    for {
      eConf: Option[Option[WorkspaceConf]] <- Future
        .find {
          readers.map {
            _.readRoot(dir, platform, environment, logger)
          }
        }(_.isDefined)
    } yield {
      eConf.flatten
    }
}