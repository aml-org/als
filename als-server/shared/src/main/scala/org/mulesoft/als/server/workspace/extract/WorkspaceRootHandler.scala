package org.mulesoft.als.server.workspace.extract

import org.mulesoft.als.server.logger.Logger
import org.mulesoft.amfintegration.amfconfiguration.AmfConfigurationWrapper
import org.mulesoft.als.configuration.ConfigurationStyle.{COMMAND, FILE}
import org.mulesoft.als.configuration.ProjectConfigurationStyle

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class WorkspaceRootHandler(amfConfiguration: AmfConfigurationWrapper,
                           projectConfigurationStyle: ProjectConfigurationStyle) {

  /**
    * @key: recognized config file
    * @value: mainFile extractor for this specific file
    */
  private val readers: List[ConfigReader] =
    projectConfigurationStyle.style match {
      case COMMAND => List.empty
      case FILE    => List(ExchangeConfigReader)
    }

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
