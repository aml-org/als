package org.mulesoft.als.server.workspace.codeactions

import amf.core.client.common.remote.Content
import amf.core.client.scala.resource.ResourceLoader
import org.mulesoft.als.server.client.scala.LanguageServerBuilder
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.protocol.configuration.AlsInitializeParams
import org.mulesoft.als.server.workspace.WorkspaceManager
import org.mulesoft.als.server.workspace.command.Commands
import org.mulesoft.als.server.{LanguageServerBaseTest, MockDiagnosticClientNotifier}
import org.mulesoft.amfintegration.amfconfiguration.EditorConfiguration
import org.mulesoft.lsp.configuration.TraceKind
import org.mulesoft.lsp.workspace.ExecuteCommandParams

import scala.concurrent.{ExecutionContext, Future}

trait CodeActionsTest extends LanguageServerBaseTest {

  override implicit val executionContext: ExecutionContext =
    ExecutionContext.Implicits.global

  override def rootPath: String = "codeActions/deleteNode"

  case class WorkspaceEntry(resources: Map[String, String], mainFile: Option[String])

  def buildServer(root: String, workspace: WorkspaceEntry): Future[(LanguageServer, WorkspaceManager)] = {
    val rs = new ResourceLoader {
      override def fetch(resource: String): Future[Content] =
        workspace.resources
          .get(resource)
          .map(c => new Content(c, resource))
          .map(Future.successful)
          .getOrElse(Future.failed(new Exception("File not found on custom ResourceLoader")))
      override def accepts(resource: String): Boolean = workspace.resources.keySet.contains(resource)
    }

    val factory =
      new WorkspaceManagerFactoryBuilder(new MockDiagnosticClientNotifier,
                                         logger,
                                         EditorConfiguration.withPlatformLoaders(Seq(rs)))
        .buildWorkspaceManagerFactory()
    val workspaceManager: WorkspaceManager = factory.workspaceManager
    val server =
      new LanguageServerBuilder(factory.documentManager,
                                workspaceManager,
                                factory.configurationManager,
                                factory.resolutionTaskManager)
        .addRequestModule(factory.codeActionManager)
        .addRequestModule(factory.renameManager)
        .build()

    server
      .testInitialize(AlsInitializeParams(None, Some(TraceKind.Off), rootUri = Some(root)))
      .andThen { case _ => server.initialized() }
      .flatMap(_ => {
        val initialArgs = changeConfigArgs(workspace.mainFile, root)
        server.workspaceService.executeCommand(
          ExecuteCommandParams(Commands.DID_CHANGE_CONFIGURATION, List(initialArgs)))
      })
      .map(_ => (server, workspaceManager))

  }

}
