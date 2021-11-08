package org.mulesoft.als.server.workspace

case class ProjectConfiguration(mainFile: String,
                                designDependency: Set[String],
                                validationDependency: Set[String],
                                extensionDependency: Set[String],
                                metadataDependency: Set[String]) {
  def folderUri = mainFile.substring(0, mainFile.lastIndexOf("/"))
}

object ProjectConfiguration {
  def apply(main: String) = ProjectConfiguration(main, Set.empty, Set.empty, Set.empty, Set.empty)
}
