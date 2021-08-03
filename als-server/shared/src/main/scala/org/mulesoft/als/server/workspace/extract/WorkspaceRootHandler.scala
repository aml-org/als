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

  def extractConfiguration(dirUri: String, logger: Logger): Future[Option[WorkspaceConfig]] =
    for {
      eConf: Option[Option[WorkspaceConfig]] <- Future
        .find {
          readers.map {
            _.readRoot(dirUri, platform, environment, logger)
          }
        }(_.isDefined)
    } yield {
      eConf.flatten
    }
}