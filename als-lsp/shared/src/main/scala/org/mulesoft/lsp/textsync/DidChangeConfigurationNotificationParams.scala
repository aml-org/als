package org.mulesoft.lsp.textsync

/**
  * @param mainUri
  * @param folder
  * @param dependencies if just a String, then the file will be treated as a normal lazy dependency
  */
case class DidChangeConfigurationNotificationParams(mainUri: String,
                                                    folder: Option[String],
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
