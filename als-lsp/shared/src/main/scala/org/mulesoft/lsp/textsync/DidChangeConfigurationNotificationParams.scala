package org.mulesoft.lsp.textsync

/**
  * @param mainUri optional if project is present. The root of the tree, starting point. Path ALWAYS relative to folder
  * @param folder workspace folder that contains the project
  * @param dependencies if just a String, then the file will be treated as a normal lazy dependency
  */
case class DidChangeConfigurationNotificationParams(mainUri: Option[String],
                                                    folder: String,
                                                    dependencies: Set[Either[String, DependencyConfiguration]])

/**
  * @param file path relative to the root
  * @param scope type of relationship (normal dependency, custom validation, semantic extensions, etc), defaults to normal
  */
case class DependencyConfiguration(file: String, scope: String)

object KnownDependencyScopes {
  val CUSTOM_VALIDATION  = "custom-validation"
  val SEMANTIC_EXTENSION = "semantic-extension"
  val DIALECT            = "dialect"
  val DEPENDENCY         = "dependency" // not really necessary, is default value
}
