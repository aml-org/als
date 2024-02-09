package org.mulesoft.als.server.modules.workspace
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DummyWorkspaceFolderManager(override val folderUri: String) extends WorkspaceFolderManager {

  override def initialized: Future[Unit] = Future.successful(true)

  override def shutdown(): Future[Unit] = Future.successful()

  override def containsFile(uri: String): Future[Boolean] = Future(uri.startsWith(folderUri))
}
