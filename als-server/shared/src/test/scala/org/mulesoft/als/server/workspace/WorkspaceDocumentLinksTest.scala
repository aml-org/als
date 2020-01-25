package org.mulesoft.als.server.workspace

import amf.internal.environment.Environment
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.modules.actions.DocumentLinksManager
import org.mulesoft.als.server.modules.ast.{CLOSE_FILE, OPEN_FILE}
import org.mulesoft.als.server.modules.telemetry.TelemetryManager
import org.mulesoft.als.server.textsync.TextDocumentContainer
import org.mulesoft.als.server.{LanguageServerBaseTest, LanguageServerBuilder, MockDiagnosticClientNotifier}
import org.mulesoft.lsp.feature.link.DocumentLink
import org.mulesoft.lsp.server.{AmfInstance, LanguageServer}

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
    val workspaceLinkHandler: WorkspaceLinkHandler = new WorkspaceLinkHandler("")
    workspaceLinkHandler
      .init()
      .flatMap(_ => {
        workspaceLinkHandler
          .openFile(nonWorkspaceFile)
          .getDocumentLinks(nonWorkspaceFile)
          .flatMap(uninitLinks => {
            workspaceLinkHandler
              .openFile(workspaceFile)
              .getDocumentLinks(workspaceFile)
              .flatMap(links => {
                assert(uninitLinks.nonEmpty)
                assert(uninitLinks.exists(_.target.equals(nonWorkspaceNonRelativeFilePath)))
                assert(!uninitLinks.exists(_.target.contains(nonWorkspaceRelativeFilePath)))
                assert(links.isEmpty)
              })
          })
      })
  }

  class WorkspaceLinkHandler(rootFolder: String) {
    val clientNotifier                     = new MockDiagnosticClientNotifier()
    val telemetryManager: TelemetryManager = new TelemetryManager(clientNotifier, logger)
    val container: TextDocumentContainer   = TextDocumentContainer(Environment(), platform, AmfInstance.default)

    val workspaceManager: WorkspaceManager =
      new WorkspaceManager(container, telemetryManager, Nil, logger)
    val documentLinksManager: DocumentLinksManager =
      new DocumentLinksManager(workspaceManager, telemetryManager, platform, logger)

    def init(): Future[Unit] = {
      workspaceManager.initializeWS(filePath(rootFolder))
    }

    def openFileAndGetLinks(path: String): Future[Seq[DocumentLink]] = {
      openFile(path).getDocumentLinks(path)
    }

    def openFile(path: String): WorkspaceLinkHandler = {
      workspaceManager.notify(filePath(path), OPEN_FILE)
      this
    }

    def closeFile(path: String): WorkspaceLinkHandler = {
      workspaceManager.notify(filePath(path), CLOSE_FILE)
      this
    }

    def getDocumentLinks(path: String): Future[Seq[DocumentLink]] = {
      documentLinksManager.documentLinks(filePath(path))
    }

  }

  override def buildServer(): LanguageServer =
    new LanguageServerBuilder(factory.documentManager, factory.workspaceManager)
      .addRequestModule(factory.structureManager)
      .build()

  override def rootPath: String = "workspace/ws-link-test"
}
