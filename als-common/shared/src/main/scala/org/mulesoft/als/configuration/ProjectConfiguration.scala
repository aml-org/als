package org.mulesoft.als.configuration

import org.mulesoft.common.io.Fs
import org.mulesoft.exceptions.PathTweaks

/** @param folder
  *   workspace folder that contains the project
  * @param mainFile
  *   optional if project is present. The root of the tree, starting point. Path ALWAYS relative to folder
  * @param designDependency
  * @param validationDependency
  * @param extensionDependency
  * @param metadataDependency
  */
case class ProjectConfiguration(
    folder: String,
    mainFile: Option[String],
    designDependency: Set[String],
    validationDependency: Set[String],
    extensionDependency: Set[String],
    metadataDependency: Set[String]
) {

  /** @return
    *   main file computed uri. Folder plus main file.
    */
  def rootUri: Option[String] =
    mainFile.map(m => m.substring(0, m.lastIndexOf(Fs.separatorChar))) // todo: not sure what this is supposed to do

  def containsInDependencies(uri: String): Boolean = {
    val dependencies = designDependency ++ validationDependency ++ extensionDependency ++ metadataDependency
    dependencies.contains(uri) || dependencies.contains(PathTweaks.unapply(uri))
  }
}

object ProjectConfiguration {

  def empty(folder: String): ProjectConfiguration = apply(folder)

  def apply(
      folder: String,
      mainFile: Option[String] = None,
      designDependency: Set[String] = Set.empty,
      validationDependency: Set[String] = Set.empty,
      extensionDependency: Set[String] = Set.empty,
      metadataDependency: Set[String] = Set.empty
  ): ProjectConfiguration =
    new ProjectConfiguration(
      folder,
      mainFile,
      designDependency,
      validationDependency,
      extensionDependency,
      metadataDependency
    )

  def apply(folder: String, main: String): ProjectConfiguration =
    ProjectConfiguration(folder, Some(main), Set.empty, Set.empty, Set.empty, Set.empty)
  def apply(folder: String, main: String, cacheUris: Set[String]): ProjectConfiguration =
    ProjectConfiguration(folder, Some(main), cacheUris, Set.empty, Set.empty, Set.empty)
}
