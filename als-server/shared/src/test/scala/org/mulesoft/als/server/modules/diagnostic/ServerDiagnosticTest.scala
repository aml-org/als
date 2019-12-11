package org.mulesoft.als.server.modules.diagnostic

import org.mulesoft.als.server.modules.ManagersFactory
import org.mulesoft.als.server.{LanguageServerBaseTest, LanguageServerBuilder}
import org.mulesoft.lsp.server.{DefaultServerSystemConf, LanguageServer}

import scala.concurrent.ExecutionContext

class ServerDiagnosticTest extends LanguageServerBaseTest {

  override implicit val executionContext = ExecutionContext.Implicits.global

  override def rootPath: String = ""

  override def buildServer(): LanguageServer = {

    val factory = ManagersFactory(MockDiagnosticClientNotifier, logger, withDiagnostics = true)
    new LanguageServerBuilder(factory.documentManager, factory.workspaceManager, DefaultServerSystemConf)
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
        _       <- openFileNotification(server)(libFilePath, libFileContent)
        oLib1   <- MockDiagnosticClientNotifier.nextCall
        _       <- openFileNotification(server)(mainFilePath, mainContent)
        oMain11 <- MockDiagnosticClientNotifier.nextCall
        oMain12 <- MockDiagnosticClientNotifier.nextCall
        _       <- focusNotification(server)(libFilePath, 0)
        oLib2   <- MockDiagnosticClientNotifier.nextCall
        _       <- changeNotification(server)(libFilePath, libFileContent.replace("b: string", "a: string"), 1)
        oLib3   <- MockDiagnosticClientNotifier.nextCall
        _       <- focusNotification(server)(mainFilePath, 0)
        oMain21 <- MockDiagnosticClientNotifier.nextCall
        oMain22 <- MockDiagnosticClientNotifier.nextCall
      } yield {
        server.shutdown()
        assert(oLib1.diagnostics.isEmpty && oLib1.uri == libFilePath)
        assert(oMain11.diagnostics.length == 1 && oMain11.uri == mainFilePath)
        assert(oLib1 == oMain12)
        assert(oLib2 == oLib1)
        assert(oLib3 == oLib2)
        assert(oMain22 == oLib1)
        assert(oMain21.diagnostics.isEmpty && oMain21.uri == mainFilePath)
        assert(MockDiagnosticClientNotifier.promises.isEmpty)
      }
    }
  }
}
