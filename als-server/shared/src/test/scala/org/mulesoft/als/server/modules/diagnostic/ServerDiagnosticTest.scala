package org.mulesoft.als.server.modules.diagnostic

import amf.core.remote.Platform
import amf.internal.environment.Environment
import org.mulesoft.als.common.DirectoryResolver
import org.mulesoft.als.server.modules.ast.AstManager
import org.mulesoft.als.server.modules.telemetry.TelemetryManager
import org.mulesoft.als.server.textsync.TextDocumentManager
import org.mulesoft.als.server.{LanguageServerBaseTest, LanguageServerBuilder}

import scala.concurrent.ExecutionContext

class ServerDiagnosticTest extends LanguageServerBaseTest {

  override implicit val executionContext = ExecutionContext.Implicits.global

  override def rootPath: String = ""

  override def addModules(documentManager: TextDocumentManager,
                          platform: Platform,
                          directoryResolver: DirectoryResolver,
                          baseEnvironment: Environment,
                          builder: LanguageServerBuilder): LanguageServerBuilder = {

    val telemetryManager = new TelemetryManager(MockDiagnosticClientNotifier, logger)
    val astManager       = new AstManager(documentManager, baseEnvironment, telemetryManager, platform, logger)
    val diagnosticManager =
      new DiagnosticManager(documentManager,
                            astManager,
                            telemetryManager,
                            MockDiagnosticClientNotifier,
                            platform,
                            logger)

    builder
      .addInitializable(astManager)
      .addInitializableModule(diagnosticManager)
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
        a <- openFileNotification(server)(libFilePath, libFileContent)
        b <- openFileNotification(server)(mainFilePath, mainContent)
        c <- focusNotification(server)(libFilePath, 0)
        d <- changeNotification(server)(libFilePath, libFileContent.replace("b: string", "a: string"), 1)
        e <- focusNotification(server)(mainFilePath, 0)
      } yield {
        server.shutdown()
        assert(
          a.diagnostics.isEmpty && a.uri == libFilePath &&
            b.diagnostics.length == 1 && b.uri == mainFilePath && // todo: search coinciding message between JS and JVM
            c.diagnostics.isEmpty && c.uri == libFilePath &&
            d.diagnostics.isEmpty && d.uri == libFilePath &&
            e.diagnostics.isEmpty && e.uri == mainFilePath)
      }
    }
  }
}
