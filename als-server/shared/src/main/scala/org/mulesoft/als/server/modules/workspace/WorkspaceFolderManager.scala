package org.mulesoft.als.server.modules.workspace

import scala.concurrent.Future

trait WorkspaceFolderManager {
  def initialized: Future[Unit]
  val folderUri: String
  def shutdown(): Future[Unit]
  def containsFile(uri: String): Future[Boolean]
}
