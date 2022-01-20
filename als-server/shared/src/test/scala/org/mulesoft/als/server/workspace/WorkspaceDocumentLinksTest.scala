package org.mulesoft.als.server.workspace

import org.mulesoft.als.server.client.scala.LanguageServerBuilder
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.modules.actions.DocumentLinksManager
import org.mulesoft.als.server.modules.ast.{CLOSE_FILE, OPEN_FILE}
import org.mulesoft.als.server.modules.telemetry.TelemetryManager
import org.mulesoft.als.server.modules.workspace.DefaultProjectConfigurationProvider
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.textsync.TextDocumentContainer
import org.mulesoft.als.server.workspace.command.Commands
import org.mulesoft.als.server.{LanguageServerBaseTest, MockDiagnosticClientNotifier}
import org.mulesoft.amfintegration.amfconfiguration.EditorConfiguration
import org.mulesoft.lsp.configuration.WorkspaceFolder
import org.mulesoft.lsp.feature.link.DocumentLink
import org.mulesoft.lsp.workspace.ExecuteCommandParams

import scala.concurrent.{ExecutionContext, Future}

class WorkspaceDocumentLinksTest extends LanguageServerBaseTest {

  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  private val factory =
    new WorkspaceManagerFactoryBuilder(new MockDiagnosticClientNotifier(), logger, EditorConfiguration())
      .buildWorkspaceManagerFactory()
  private val workspaceFile                   = "ws/sub/type.raml"
  private val workspaceIncludedFilePath       = filePath("ws/includes/type.json")
  private val nonWorkspaceFile                = "uninitialized-ws/sub/non-main.raml"
  private val nonWorkspaceNonRelativeFilePath = filePath("uninitialized-ws/sub/non-relative.raml")
  private val nonWorkspaceRelativeFilePath    = filePath("fragment.raml")

  test("Initialized workspace links relative to main file should work") {
    val workspaceLinkHandler: WorkspaceLinkHandler = new WorkspaceLinkHandler("ws", Some("api.raml"))

    for {
      _   <- workspaceLinkHandler.init()
      seq <- workspaceLinkHandler.getDocumentLinks(workspaceFile)
    } yield assert(seq.exists(_.target.equals(workspaceIncludedFilePath)))
  }

  test("Uninitialized workspace links relative to main file shouldn't work") {
    val workspaceLinkHandler: WorkspaceLinkHandler = new WorkspaceLinkHandler("ws", Some("api.raml"))
    for {
      _         <- workspaceLinkHandler.init()
      links     <- workspaceLinkHandler.openFileAndGetLinks(nonWorkspaceFile)
      initLinks <- workspaceLinkHandler.openFileAndGetLinks(workspaceFile)
    } yield {
      assert(!links.exists(_.target.equals(nonWorkspaceRelativeFilePath)))
      assert(links.exists(_.target.equals(nonWorkspaceNonRelativeFilePath)))
      assert(initLinks.exists(_.target.equals(workspaceIncludedFilePath)))
    }
  }

  test("Returning to initialized workspace from an uninitialized and relative links still work") {
    val workspaceLinkHandler: WorkspaceLinkHandler = new WorkspaceLinkHandler("ws", Some("api.raml"))
    for {
      _         <- workspaceLinkHandler.init()
      links     <- workspaceLinkHandler.openFileAndGetLinks(nonWorkspaceFile)
      initLinks <- workspaceLinkHandler.openFileAndGetLinks(workspaceFile)
    } yield {
      assert(links.nonEmpty)
      assert(links.exists(_.target.equals(nonWorkspaceNonRelativeFilePath)))
      assert(!links.exists(_.target.equals(nonWorkspaceRelativeFilePath)))
      assert(initLinks.exists(_.target.equals(workspaceIncludedFilePath)))
    }

  }

  test("Starting a global workspace non-relative links should work") {
    val workspaceLinkHandler: WorkspaceLinkHandler = new WorkspaceLinkHandler("file://", None)
    workspaceLinkHandler
      .init()
      .flatMap(_ => {
        workspaceLinkHandler
          .openFile(nonWorkspaceFile)
          .flatMap(
            _ =>
              workspaceLinkHandler
                .getDocumentLinks(nonWorkspaceFile)
                .flatMap(uninitLinks => {
                  workspaceLinkHandler
                    .openFile(workspaceFile)
                    .flatMap(_ =>
                      workspaceLinkHandler
                        .getDocumentLinks(workspaceFile)
                        .flatMap(links => {
                          assert(uninitLinks.nonEmpty)
                          assert(uninitLinks.exists(_.target.equals(nonWorkspaceNonRelativeFilePath)))
                          assert(!uninitLinks.exists(_.target.contains(nonWorkspaceRelativeFilePath)))
                          assert(links.isEmpty)
                        }))
                }))
      })
  }

  class WorkspaceLinkHandler(rootFolder: String, mainFile: Option[String]) {
    val clientNotifier                     = new MockDiagnosticClientNotifier()
    val telemetryManager: TelemetryManager = new TelemetryManager(clientNotifier, logger)

    val workspaceManager: WorkspaceManager = {
      val editorConfiguration = EditorConfiguration()
      val container           = TextDocumentContainer()
      val defaultProjectConfigurationProvider =
        new DefaultProjectConfigurationProvider(container, editorConfiguration, logger)
      WorkspaceManager(container,
                       telemetryManager,
                       editorConfiguration,
                       defaultProjectConfigurationProvider,
                       Nil,
                       Nil,
                       logger,
                       factory.configurationManager)
    }

    val documentLinksManager: DocumentLinksManager =
      new DocumentLinksManager(workspaceManager, telemetryManager, logger)

    def init(): Future[Unit] =
      workspaceManager
        .initialize(List(WorkspaceFolder(filePath(rootFolder))))
        .flatMap(_ => {
          val initialArgs = changeConfigArgs(mainFile, filePath(rootFolder))
          workspaceManager
            .executeCommand(ExecuteCommandParams(Commands.DID_CHANGE_CONFIGURATION, List(initialArgs)))
            .map(_ => {})
        })

    def openFileAndGetLinks(path: String): Future[Seq[DocumentLink]] =
      openFile(path) flatMap (_ => getDocumentLinks(path))

    def openFile(path: String): Future[Unit] =
      workspaceManager
        .notify(filePath(path), OPEN_FILE)

    def closeFile(path: String): Future[Unit] =
      workspaceManager
        .notify(filePath(path), CLOSE_FILE)

    def getDocumentLinks(path: String): Future[Seq[DocumentLink]] =
      documentLinksManager.documentLinks(filePath(path), "")
  }

  def buildServer(): LanguageServer =
    new LanguageServerBuilder(factory.documentManager,
                              factory.workspaceManager,
                              factory.configurationManager,
                              factory.resolutionTaskManager)
      .addRequestModule(factory.structureManager)
      .build()

  override def rootPath: String = "workspace/ws-link-test"
}
