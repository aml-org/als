package org.mulesoft.als.server.workspace

import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.{LanguageServerBaseTest, LanguageServerBuilder, MockDiagnosticClientNotifier}
import org.mulesoft.lsp.server.{DefaultServerSystemConf, LanguageServer}

import scala.concurrent.ExecutionContext

class CommandsTest extends LanguageServerBaseTest {
  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  override def buildServer(): LanguageServer = {

    val factory =
      new WorkspaceManagerFactoryBuilder(new MockDiagnosticClientNotifier, logger).buildWorkspaceManagerFactory()
    new LanguageServerBuilder(factory.documentManager, factory.workspaceManager, DefaultServerSystemConf)
      .build()
  }

  override def rootPath: String = ""

  test("Parse and model errors at full validation") {
    withServer { server =>
      val content =
        """#%RAML 1.0
          |description: missing title
          |a
          |""".stripMargin

      val api = "file://api.raml"
      openFile(server)(api, content)
      compile(server)(api).map { s =>
        s.length should be(1)
        s.head.uri should be(api)
        s.head.diagnostics.length should be(2)
      }
    }
  }
}
