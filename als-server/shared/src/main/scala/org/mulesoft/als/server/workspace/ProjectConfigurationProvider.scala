package org.mulesoft.als.server.workspace

import org.mulesoft.als.configuration.ProjectConfiguration
import org.mulesoft.als.server.modules.workspace.MainFileTree
import org.mulesoft.amfintegration.ValidationProfile
import org.mulesoft.amfintegration.amfconfiguration.ProjectConfigurationState

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait ProjectConfigurationProvider {
  def newProjectConfiguration(folder: String,
                              projectConfiguration: ProjectConfiguration): Future[ProjectConfigurationState]
  def afterNewTree(folder: String, tree: MainFileTree): Future[Unit]
  def getProjectInfo(folder: String): Option[Future[ProjectConfigurationState]]
  def getProfiles(folder: String): Future[Seq[ValidationProfile]]
  def getMainFile(folder: String): Option[Future[String]]
  def getProjectRoot(folder: String): Option[Future[String]] =
    getMainFile(folder).map(_.map(m => m.substring(0, m.lastIndexOf("/"))))
  // TODO: analyze
  // def getValidator:
}
