package org.mulesoft.als.server.workspace

import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.modules.actions.DocumentLinksManager
import org.mulesoft.als.server.modules.ast.{CLOSE_FILE, OPEN_FILE}
import org.mulesoft.als.server.modules.telemetry.TelemetryManager
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.textsync.TextDocumentContainer
import org.mulesoft.als.server.{LanguageServerBaseTest, LanguageServerBuilder, MockDiagnosticClientNotifier}
import org.mulesoft.amfintegration.amfconfiguration.AmfConfigurationWrapper
import org.mulesoft.lsp.configuration.WorkspaceFolder
import org.mulesoft.lsp.feature.link.DocumentLink
import org.mulesoft.als.configuration.DefaultProjectConfigurationStyle

import scala.concurrent.{ExecutionContext, Future}

class WorkspaceDocumentLinksTest extends LanguageServerBaseTest {

  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  private val factory =
    new WorkspaceManagerFactoryBuilder(new MockDiagnosticClientNotifier(), logger).buildWorkspaceManagerFactory()
  private val workspaceFile                   = "ws/sub/type.raml"
  private val workspaceIncludedFilePath       = filePath("ws/includes/type.json")
  private val nonWorkspaceFile                = "uninitialized-ws/sub/non-main.raml"
  private val nonWorkspaceNonRelativeFilePath = filePath("uninitialized-ws/sub/non-relative.raml")
  private val nonWorkspaceRelativeFilePath    = filePath("fragment.raml")

  test("Initialized workspace links relative to main file should work") {
    val workspaceLinkHandler: WorkspaceLinkHandler = new WorkspaceLinkHandler("ws")

    for {
      _   <- workspaceLinkHandler.init()
      seq <- workspaceLinkHandler.getDocumentLinks(workspaceFile)
    } yield assert(seq.exists(_.target.equals(workspaceIncludedFilePath)))
  }

  test("Uninitialized workspace links relative to main file shouldn't work") {
    val workspaceLinkHandler: WorkspaceLinkHandler = new WorkspaceLinkHandler("ws")
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
    val workspaceLinkHandler: WorkspaceLinkHandler = new WorkspaceLinkHandler("ws")
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
    val workspaceLinkHandler: WorkspaceLinkHandler = new WorkspaceLinkHandler("file://")
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

  class WorkspaceLinkHandler(rootFolder: String) {
    val amfConfiguration                   = AmfConfigurationWrapper(Seq.empty)
    val clientNotifier                     = new MockDiagnosticClientNotifier()
    val telemetryManager: TelemetryManager = new TelemetryManager(clientNotifier, logger)
    val container: TextDocumentContainer   = TextDocumentContainer(amfConfiguration)

    val workspaceManager: WorkspaceManager =
      WorkspaceManager(container, telemetryManager, Nil, Nil, logger)
    val documentLinksManager: DocumentLinksManager =
      new DocumentLinksManager(workspaceManager, telemetryManager, logger)

    def init(): Future[Unit] =
      for {
        _ <- amfConfiguration.init()
        _ <- workspaceManager.initialize(List(WorkspaceFolder(filePath(rootFolder))), DefaultProjectConfigurationStyle)
      } yield {}

    def openFileAndGetLinks(path: String): Future[Seq[DocumentLink]] =
      openFile(path) flatMap (_.getDocumentLinks(path))

    def openFile(path: String): Future[WorkspaceLinkHandler] = {
      workspaceManager
        .notify(filePath(path), OPEN_FILE)
        .map(_ => this)
    }

    def closeFile(path: String): Future[WorkspaceLinkHandler] = {
      workspaceManager
        .notify(filePath(path), CLOSE_FILE)
        .map(_ => this)
    }

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
