package org.mulesoft.als.server.modules.workspace

class UnavailableWorkspaceException extends Exception {
  override def getMessage: String = "Workspace has already been closed."
}
