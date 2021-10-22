package org.mulesoft.als.server.workspace.codeactions

import amf.core.client.common.remote.Content
import amf.core.client.scala.resource.ResourceLoader
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.protocol.configuration.AlsInitializeParams
import org.mulesoft.als.server.workspace.WorkspaceManager
import org.mulesoft.als.server.{LanguageServerBaseTest, LanguageServerBuilder, MockDiagnosticClientNotifier}
import org.mulesoft.amfintegration.amfconfiguration.AmfConfigurationWrapper
import org.mulesoft.lsp.configuration.TraceKind

import scala.concurrent.{ExecutionContext, Future}

trait CodeActionsTest extends LanguageServerBaseTest {

  override implicit val executionContext: ExecutionContext =
    ExecutionContext.Implicits.global

  override def rootPath: String = "codeActions/deleteNode"

  def buildServer(root: String, ws: Map[String, String]): Future[(LanguageServer, WorkspaceManager)] = {
    val rs = new ResourceLoader {
      override def fetch(resource: String): Future[Content] =
        ws.get(resource)
          .map(c => new Content(c, resource))
          .map(Future.successful)
          .getOrElse(Future.failed(new Exception("File not found on custom ResourceLoader")))
      override def accepts(resource: String): Boolean = ws.keySet.contains(resource)
    }

    AmfConfigurationWrapper(Seq(rs)).flatMap(amfConfiguration => {
      val factory =
        new WorkspaceManagerFactoryBuilder(new MockDiagnosticClientNotifier, logger)
          .withAmfConfiguration(amfConfiguration)
          .buildWorkspaceManagerFactory()
      val workspaceManager: WorkspaceManager = factory.workspaceManager
      val server =
        new LanguageServerBuilder(factory.documentManager,
                                  workspaceManager,
                                  factory.configurationManager,
                                  factory.resolutionTaskManager)
          .addRequestModule(factory.codeActionManager)
          .build()

      server
        .initialize(AlsInitializeParams(None, Some(TraceKind.Off), rootUri = Some(root)))
        .andThen { case _ => server.initialized() }
        .map(_ => (server, workspaceManager))
    })

  }

}
