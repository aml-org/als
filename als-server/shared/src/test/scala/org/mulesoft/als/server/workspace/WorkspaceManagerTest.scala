package org.mulesoft.als.server.workspace

import org.mulesoft.als.server.modules.ManagersFactory
import org.mulesoft.als.server.{LanguageServerBaseTest, LanguageServerBuilder}
import org.mulesoft.lsp.configuration.{InitializeParams, TraceKind}
import org.mulesoft.lsp.server.LanguageServer
import org.scalatest.Assertion

import scala.concurrent.ExecutionContext

class WorkspaceManagerTest extends LanguageServerBaseTest {

  override implicit val executionContext = ExecutionContext.Implicits.global

  private val factory = ManagersFactory(MockDiagnosticClientNotifier, platform, logger, withDiagnostics = true)

  private val editorFiles = factory.container

  test("Workspace Manager check validations (initializing a tree should validate instantly)") {
    withServer[Assertion] { server =>
      for {
        _ <- server.initialize(InitializeParams(None, Some(TraceKind.Off), rootUri = Some(s"${filePath("ws1")}")))
        a <- MockDiagnosticClientNotifier.nextCall
        b <- MockDiagnosticClientNotifier.nextCall
      } yield {
        assert(a.uri != b.uri)
      }
    }
  }

  override def buildServer(): LanguageServer =
    new LanguageServerBuilder(factory.documentManager, factory.workspaceManager, platform).build()

  override def rootPath: String = "workspace"

}
