package org.mulesoft.als.server.workspace.extract

import org.mulesoft.als.configuration.WorkspaceConfiguration

case class WorkspaceConfig(rootFolder: String,
                           mainFile: String,
                           cachables: Set[String],
                           configReader: Option[ConfigReader])
    extends WorkspaceConfiguration
