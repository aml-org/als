package org.mulesoft.als.configuration

case class ProjectConfiguration(folder: String,
                                mainFile: Option[String],
                                designDependency: Set[String],
                                validationDependency: Set[String],
                                extensionDependency: Set[String],
                                metadataDependency: Set[String]) {
  def rootUri: Option[String] = mainFile.map(m => m.substring(0, m.lastIndexOf("/")))
}

object ProjectConfiguration {
  def empty(folder: String): ProjectConfiguration =
    ProjectConfiguration(folder, None, Set.empty, Set.empty, Set.empty, Set.empty)
  def apply(folder: String, main: String): ProjectConfiguration =
    ProjectConfiguration(folder, Some(main), Set.empty, Set.empty, Set.empty, Set.empty)
  def apply(folder: String, main: String, cacheUris: Set[String]): ProjectConfiguration =
    ProjectConfiguration(folder, Some(main), cacheUris, Set.empty, Set.empty, Set.empty)
}
