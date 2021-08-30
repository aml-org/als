package org.mulesoft.als.server.workspace.extract

import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.modules.workspace.WorkspaceContentManager
import org.mulesoft.amfintegration.amfconfiguration.AmfConfigurationWrapper

import scala.concurrent.Future

trait WorkspaceConfigurationProvider {
  def obtainConfiguration(amfConfiguration: AmfConfigurationWrapper, logger: Logger): Future[Option[WorkspaceConfig]]
}

case class DefaultWorkspaceConfigurationProvider(manager: WorkspaceContentManager,
                                                 mainUri: String,
                                                 dependencies: Set[String],
                                                 profiles: Set[String],
                                                 semanticExtensions: Set[String],
                                                 reader: Option[ConfigReader])
    extends WorkspaceConfigurationProvider {
  override def obtainConfiguration(amfConfiguration: AmfConfigurationWrapper,
                                   logger: Logger): Future[Option[WorkspaceConfig]] =
    Future.successful(
      Some(
        WorkspaceConfig(
          manager.folderUri,
          mainUri.stripPrefix(manager.folderUri).stripPrefix("/"), // just the file name, not the full path
          dependencies,
          profiles,
          semanticExtensions,
          reader
        )))
}

case class ReaderWorkspaceConfigurationProvider(manager: WorkspaceContentManager)
    extends WorkspaceConfigurationProvider {
  override def obtainConfiguration(amfConfiguration: AmfConfigurationWrapper,
                                   logger: Logger): Future[Option[WorkspaceConfig]] = {
    manager.getConfigReader match {
      case Some(configReader) =>
        configReader.readRoot(manager.folderUri, amfConfiguration, logger)
      case _ => Future.successful(None)
    }
  }
}
