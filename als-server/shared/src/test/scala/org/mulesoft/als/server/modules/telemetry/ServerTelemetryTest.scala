package org.mulesoft.als.server.modules.telemetry

import org.mulesoft.als.server.client.scala.LanguageServerBuilder
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.{LanguageServerBaseTest, MockTelemetryClientNotifier}
import org.mulesoft.lsp.feature.telemetry.{MessageTypes, TelemetryMessage}

import scala.concurrent.{ExecutionContext, Future}

class ServerTelemetryTest extends LanguageServerBaseTest {
  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  override def rootPath: String = ""

  private val mockTelemetryClientNotifier = new MockTelemetryClientNotifier

  def buildServer(): LanguageServer = {
    val builder = new WorkspaceManagerFactoryBuilder(mockTelemetryClientNotifier, logger)
    val dm      = builder.buildDiagnosticManagers()
    val factory = builder.buildWorkspaceManagerFactory()

    val b = new LanguageServerBuilder(
      factory.documentManager,
      factory.workspaceManager,
      factory.configurationManager,
      factory.resolutionTaskManager
    )
    dm.foreach(m => b.addInitializableModule(m))
    b.build()
  }

  def checkMessages(telemetryMessages: Seq[String], msgs: Seq[TelemetryMessage]): Boolean =
    if (
      msgs.size == telemetryMessages.size &&
      (msgs.map(_.messageType).toSet diff telemetryMessages.toSet).isEmpty
    )
      true
    else {
      println(s"got: ${msgs.size}\texpected: ${telemetryMessages.size}")
      println(msgs.map(_.messageType).toSet diff telemetryMessages.toSet)
      false
    }

  // TODO: test again once Telemetry messaging is stable
  ignore("diagnostics test 001 - onFocus") {
    withServer(buildServer()) { server =>
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
      val mockWithTelemetry: (Int, () => Unit) => Future[Seq[TelemetryMessage]] =
        withTelemetry(mockTelemetryClientNotifier)

      for {
        a <- mockWithTelemetry(
          (openWithErrorTelemetryMessages(1)).size,
          () => openFile(server)(libFilePath, libFileContent)
        )
        b <- mockWithTelemetry(
          openWithErrorTelemetryMessages(2).size,
          () => openFile(server)(mainFilePath, mainContent)
        )
        c <- mockWithTelemetry(openWithErrorTelemetryMessages(1).size, () => onFocus(server)(libFilePath, 0))
        d <- mockWithTelemetry(
          openWithErrorTelemetryMessages(1).size,
          () => changeFile(server)(libFilePath, libFileContent.replace("b: string", "a: string"), 1)
        )
        e <- mockWithTelemetry(openWithErrorTelemetryMessages(2).size, () => onFocus(server)(mainFilePath, 0))
      } yield {
        server.shutdown()
        assert(
          checkMessages(openWithErrorTelemetryMessages(1), a) &&
            checkMessages(openWithErrorTelemetryMessages(2), b) &&
            checkMessages(openWithErrorTelemetryMessages(1), c) &&
            checkMessages(openWithErrorTelemetryMessages(1), d) &&
            checkMessages(openWithErrorTelemetryMessages(2), e)
        )
      }
    }
  }

  private def diagnosticTelemetryMessages(reportsQty: Int) = {
    Seq(
      MessageTypes.BEGIN_DIAGNOSTIC_PARSE,
      MessageTypes.BEGIN_REPORT,
      MessageTypes.END_REPORT,
      MessageTypes.END_DIAGNOSTIC_PARSE
    )
  }

  private val cleanOpenTelemetryMessages = Seq(
    MessageTypes.BEGIN_PARSE,
    MessageTypes.END_PARSE
  )

  private def openWithErrorTelemetryMessages(reportsQty: Int) =
    cleanOpenTelemetryMessages ++ diagnosticTelemetryMessages(reportsQty)
}
