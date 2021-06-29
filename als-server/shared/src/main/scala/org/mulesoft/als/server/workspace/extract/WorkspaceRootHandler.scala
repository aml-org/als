package org.mulesoft.als.server.workspace.extract

import org.mulesoft.als.server.logger.Logger
import org.mulesoft.amfintegration.amfconfiguration.AmfConfigurationWrapper

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class WorkspaceRootHandler(amfConfiguration: AmfConfigurationWrapper) {

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
            _.readRoot(dirUri, amfConfiguration, logger)
          }
        }(_.isDefined)
    } yield {
      eConf.flatten
    }
}