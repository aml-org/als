package org.mulesoft.als.server.lsp4j

import amf.core.client.common.validation.ProfileNames
import amf.core.internal.unsafe.PlatformSecrets
import org.eclipse.lsp4j.ExecuteCommandParams
import org.mulesoft.als.server.client.scala.LanguageServerBuilder
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.{LanguageServerBaseTest, MockDiagnosticClientNotifier}

import java.util
import scala.concurrent.Future

class Lsp4jLanguageServerDiagnosticImplTest extends LanguageServerBaseTest with PlatformSecrets {

  // TODO: check if the caché on WorkspaceContentManager is used (if it can be used)
  // TODO: check if a new validation should be sent from WorkspaceContentCollection when "onFocus" (when the BU is already parsed)
  test("Lsp4j LanguageServerImpl Command - Did Focus: Command should notify DidFocus") {
    def wrapJson(uri: String, version: String): String =
      s"""{"uri": "$uri", "version": "$version"}"""

    def executeCommandFocus(server: LanguageServerImpl)(file: String, version: Int): Future[Unit] = {
      val args: java.util.List[AnyRef] = new util.ArrayList[AnyRef]()
      args.add(wrapJson(file, version.toString))
      server.getWorkspaceService.executeCommand(new ExecuteCommandParams("didFocusChange", args))
      Future.successful(Unit)
    }

    val diagnosticsClient: MockDiagnosticClientNotifier = new MockDiagnosticClientNotifier(7000)
    withServer(buildServer(diagnosticsClient)) { s =>
      val server       = new LanguageServerImpl(s)
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
        _       <- openFileNotification(s)(libFilePath, libFileContent)
        a       <- diagnosticsClient.nextCall
        _       <- openFileNotification(s)(mainFilePath, mainContent)
        b       <- diagnosticsClient.nextCall
        c       <- diagnosticsClient.nextCall
        _       <- executeCommandFocus(server)(libFilePath, 0)
        focus11 <- diagnosticsClient.nextCall
        _       <- changeNotification(s)(libFilePath, libFileContent.replace("b: string", "a: string"), 1)
        d1      <- diagnosticsClient.nextCall
        _       <- executeCommandFocus(server)(mainFilePath, 0)
        focus21 <- diagnosticsClient.nextCall
        focus22 <- diagnosticsClient.nextCall
      } yield {
        server.shutdown()
        val firstMain   = Seq(b, c)
        val secondFocus = Seq(focus21, focus22)
        assert(a.diagnostics.isEmpty && a.uri == libFilePath)
        assert(firstMain.find(_.uri == libFilePath).contains(focus11))
        assert(firstMain.exists(i => i.uri == mainFilePath && i.diagnostics.length == 1))
        assert(secondFocus.exists(i => i.uri == mainFilePath && i.diagnostics.isEmpty))
      }
    }
  }

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
