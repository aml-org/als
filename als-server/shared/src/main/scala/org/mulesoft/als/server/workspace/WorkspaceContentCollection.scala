package org.mulesoft.als.server.workspace

import amf.core.remote.Platform
import amf.core.unsafe.PlatformSecrets
import amf.internal.environment.Environment
import org.mulesoft.als.server.modules.ast.{BaseUnitListener, NotificationKind, TextListener}
import org.mulesoft.als.server.modules.workspace.{CompilableUnit, WorkspaceContentManager}
import org.mulesoft.als.server.textsync.{EnvironmentProvider, TextDocumentContainer}
import org.mulesoft.als.server.workspace.extract.WorkspaceRootHandler
import org.mulesoft.lsp.workspace.WorkspaceService

import scala.collection.mutable.ListBuffer
import scala.concurrent.Future

class WorkspaceContentCollection(environmentProvider: EnvironmentProvider, dependencies: List[BaseUnitListener])
    extends TextListener
    with WorkspaceService {

  private val rootHandler                                     = new WorkspaceRootHandler(environmentProvider.platform)
  private val workspaces: ListBuffer[WorkspaceContentManager] = ListBuffer()
//  private var documentContainer:TextDocumentContainer = DefaultEnvironmentProvider

  def getWorkspace(uri: String): Option[WorkspaceContentManager] = {
    workspaces.find(ws => uri.startsWith(ws.folder))
  }

  def initializeWS(folder: String): Unit = {
    val mainOption = rootHandler.extractMainFile(folder)
    val workspace  = new WorkspaceContentManager(folder, mainOption, environmentProvider, dependencies)
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

//  override def withTextDocumentContainer(textDocumentContainer: TextDocumentContainer): Unit = {
//    documentContainer = Some(textDocumentContainer)
//  }
}

object DefaultEnvironmentProvider extends EnvironmentProvider with PlatformSecrets {

  private val environment                         = Environment()
  override def environmentSnapshot(): Environment = environment

  override val platform: Platform = platform
}
