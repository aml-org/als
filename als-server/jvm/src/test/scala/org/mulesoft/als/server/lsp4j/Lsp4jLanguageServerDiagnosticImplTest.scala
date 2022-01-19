package org.mulesoft.als.server.lsp4j

import amf.core.client.common.validation.ProfileNames
import amf.core.internal.unsafe.PlatformSecrets
import org.mulesoft.als.server.client.scala.LanguageServerBuilder
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.{LanguageServerBaseTest, MockDiagnosticClientNotifier}

class Lsp4jLanguageServerDiagnosticImplTest extends LanguageServerBaseTest with PlatformSecrets {

  test("diagnostics test - FullValidation") {

    val diagnosticsClient: MockDiagnosticClientNotifier = new MockDiagnosticClientNotifier(7000)
    withServer(buildServer(diagnosticsClient)) { s =>
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

      for {
        _  <- openFileNotification(s)(libFilePath, libFileContent)
        _  <- diagnosticsClient.nextCall
        _  <- openFileNotification(s)(mainFilePath, mainContent)
        _  <- diagnosticsClient.nextCall
        _  <- diagnosticsClient.nextCall
        v1 <- requestCleanDiagnostic(s)(mainFilePath)
        _  <- changeNotification(s)(libFilePath, libFileContent.replace("b: string", "a: string"), 1)
        _  <- diagnosticsClient.nextCall
        v2 <- requestCleanDiagnostic(s)(mainFilePath)

      } yield {
        s.shutdown()

        diagnosticsClient.promises.clear()
        v1.size should be(2)
        v1.head.diagnostics.size should be(1)
        v1.head.profile should be(ProfileNames.RAML10)
        v1.last.diagnostics.size should be(0)
        v2.size should be(2) // fixed error
        v2.head.diagnostics.size should be(0)
      }
    }
  }

  def buildServer(diagnosticsClient: MockDiagnosticClientNotifier): LanguageServer = {
    val builder  = new WorkspaceManagerFactoryBuilder(diagnosticsClient, logger)
    val dm       = builder.buildDiagnosticManagers()
    val managers = builder.buildWorkspaceManagerFactory()

    val b =
      new LanguageServerBuilder(managers.documentManager,
                                managers.workspaceManager,
                                managers.configurationManager,
                                managers.resolutionTaskManager)
        .addRequestModule(managers.cleanDiagnosticManager)
    dm.foreach(m => b.addInitializableModule(m))
    b.build()
  }

  override def rootPath: String = ""
}
