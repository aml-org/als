package org.mulesoft.als.server.modules.workspace

import amf.core.remote.Platform
import org.mulesoft.als.server.modules.ast.{AstListener, BaseUnitListener, NotificationKind, TextListener}
import org.mulesoft.als.server.textsync.{EnvironmentProvider, TextDocumentContainer}

import scala.collection.mutable.ListBuffer
import scala.concurrent.Future

class WorkspaceManager(environmentProvider: EnvironmentProvider, dependencies: List[BaseUnitListener])
    extends TextListener {

  private val workspaces: ListBuffer[Workspace] = ListBuffer()

  def getWorkspace(uri: String): Option[Workspace] = {
    workspaces.find(ws => uri.startsWith(ws.folder))
  }

  def initializeWS(folder: String, platform: Platform, documentContainer: TextDocumentContainer): Unit = {
    val str       = MainFileReader.read(folder)
    val workspace = new Workspace(folder, Some(str), platform, documentContainer, dependencies)
    workspaces += workspace
    workspace.initialize()
  }

  def getUnit(uri: String): Future[CompilableUnit] = {
    getWorkspace(uri) match {
      case Some(ws) => ws.getOrBuildUnit(uri)
      case _        => Future.failed(new Exception("Workspace not found"))
    }
  }

  override def notify(uri: String, kind: NotificationKind): Unit = getWorkspace(uri).foreach { ws =>
    ws.changedFile(uri, kind)
  }

}
