package org.mulesoft.als.server.modules.diagnostic

import org.mulesoft.als.server.modules.ManagersFactory
import org.mulesoft.als.server.workspace.extract.WorkspaceRootHandler
import org.mulesoft.als.server.{LanguageServerBaseTest, LanguageServerBuilder}
import org.mulesoft.lsp.server.LanguageServer

import scala.concurrent.ExecutionContext

class ServerDiagnosticTest extends LanguageServerBaseTest {

  override implicit val executionContext = ExecutionContext.Implicits.global

  override def rootPath: String = ""

  override def buildServer(): LanguageServer = {

    val factory = ManagersFactory(MockDiagnosticClientNotifier, new WorkspaceRootHandler(platform), platform, logger)
    new LanguageServerBuilder(factory.documentManager, platform)
      .addInitializable(factory.astManager)
      .addInitializableModule(factory.diagnosticManager)
      .build()
  }

  test("diagnostics test 001 - onFocus") {
    withServer { server =>
      val mainFilePath = s"file://api.raml"
      val libFilePath  = s"file://lib1.raml"

      val mainContent =
        """#%RAML 1.0
          |
          |title: test API
          |uses:
          |  lib1: lib1.raml
          |
          |/resource:
          |  post:
          |    responses:
          |      200:
          |        body:
          |          application/json:
          |            type: lib1.TestType
          |            example:
          |              {"a":"1"}
        """.stripMargin

      val libFileContent =
        """#%RAML 1.0 Library
          |
          |types:
          |  TestType:
          |    properties:
          |      b: string
        """.stripMargin

      /*
        open lib -> open main -> focus lib -> fix lib -> focus main
       */
      for {
        a  <- openFileNotification(server)(libFilePath, libFileContent)
        b  <- openFileNotification(server)(mainFilePath, mainContent)
        b2 <- MockDiagnosticClientNotifier.nextCall
        c  <- focusNotification(server)(libFilePath, 0)
        d  <- changeNotification(server)(libFilePath, libFileContent.replace("b: string", "a: string"), 1)
        e  <- focusNotification(server)(mainFilePath, 0)
        e2 <- MockDiagnosticClientNotifier.nextCall
      } yield {
        server.shutdown()
        assert(a.diagnostics.isEmpty && a.uri == libFilePath)
        assert(b.diagnostics.length == 1 && b.uri == mainFilePath)
        assert(b2.diagnostics.isEmpty && b2.uri == libFilePath)
        assert(c.diagnostics.isEmpty && c.uri == libFilePath)
        assert(d.diagnostics.isEmpty && d.uri == libFilePath)
        assert(e2.diagnostics.isEmpty && e2.uri == libFilePath)
        assert(e.diagnostics.isEmpty && e.uri == mainFilePath)
        assert(MockDiagnosticClientNotifier.promises.isEmpty)
      }
    }
  }
}
