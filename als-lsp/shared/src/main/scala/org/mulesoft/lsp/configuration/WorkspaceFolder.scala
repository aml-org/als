package org.mulesoft.lsp.configuration

/** @param uri
  *   The associated URI for this workspace folder.
  * @param name
  *   The name of the workspace folder. Used to refer to this workspace folder in the user interface.
  */
case class WorkspaceFolder(uri: Option[String], name: Option[String])

object WorkspaceFolder {
  def apply(uri: String) = new WorkspaceFolder(Some(uri), None)
}
