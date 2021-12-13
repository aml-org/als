package org.mulesoft.als.configuration

case class ProjectConfiguration(folder: String,
                                mainFile: Option[String],
                                designDependency: Set[String],
                                validationDependency: Set[String],
                                extensionDependency: Set[String],
                                metadataDependency: Set[String]) {
  def rootUri: Option[String] = mainFile.map(m => m.substring(0, m.lastIndexOf("/")))

  def containsInDependencies(uri: String): Boolean =
    (validationDependency ++ extensionDependency ++ metadataDependency).contains(uri)
}

object ProjectConfiguration {

  def empty(folder: String): ProjectConfiguration = apply(folder)

  def apply(folder: String,
            mainFile: Option[String] = None,
            designDependency: Set[String] = Set.empty,
            validationDependency: Set[String] = Set.empty,
            extensionDependency: Set[String] = Set.empty,
            metadataDependency: Set[String] = Set.empty): ProjectConfiguration =
    new ProjectConfiguration(folder,
                             mainFile,
                             designDependency,
                             validationDependency,
                             extensionDependency,
                             metadataDependency)

  def apply(folder: String, main: String): ProjectConfiguration =
    ProjectConfiguration(folder, Some(main), Set.empty, Set.empty, Set.empty, Set.empty)
  def apply(folder: String, main: String, cacheUris: Set[String]): ProjectConfiguration =
    ProjectConfiguration(folder, Some(main), cacheUris, Set.empty, Set.empty, Set.empty)
}
