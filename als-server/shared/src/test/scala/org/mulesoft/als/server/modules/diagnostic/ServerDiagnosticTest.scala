package org.mulesoft.als.server.modules.diagnostic

import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.{LanguageServerBaseTest, LanguageServerBuilder, MockDiagnosticClientNotifier}

import scala.concurrent.ExecutionContext

class ServerDiagnosticTest extends LanguageServerBaseTest {

  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  override def rootPath: String = ""

  val diagnosticNotifier = new MockDiagnosticClientNotifier
  override def buildServer(): LanguageServer = {
    val builder = new WorkspaceManagerFactoryBuilder(diagnosticNotifier, logger)
    val dm      = builder.diagnosticManager()
    val factory = builder.buildWorkspaceManagerFactory()
    new LanguageServerBuilder(factory.documentManager, factory.workspaceManager)
      .addInitializableModule(dm)
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
        _       <- openFileNotification(server)(libFilePath, libFileContent)
        oLib1   <- diagnosticNotifier.nextCall
        _       <- openFileNotification(server)(mainFilePath, mainContent)
        oMain11 <- diagnosticNotifier.nextCall
        oMain12 <- diagnosticNotifier.nextCall
        _       <- focusNotification(server)(libFilePath, 0)
        oLib2   <- diagnosticNotifier.nextCall
        _       <- changeNotification(server)(libFilePath, libFileContent.replace("b: string", "a: string"), 1)
        oLib3   <- diagnosticNotifier.nextCall
        _       <- focusNotification(server)(mainFilePath, 0)
        oMain21 <- diagnosticNotifier.nextCall
        oMain22 <- diagnosticNotifier.nextCall
      } yield {
        server.shutdown()
        assert(oLib1.diagnostics.isEmpty && oLib1.uri == libFilePath)
        assert(oMain11.diagnostics.length == 1 && oMain11.uri == mainFilePath)
        assert(oLib1 == oMain12)
        assert(oLib2 == oLib1)
        assert(oLib3 == oLib2)
        assert(oMain22 == oLib1)
        assert(oMain21.diagnostics.isEmpty && oMain21.uri == mainFilePath)
        assert(diagnosticNotifier.promises.isEmpty)
      }
    }
  }
}
