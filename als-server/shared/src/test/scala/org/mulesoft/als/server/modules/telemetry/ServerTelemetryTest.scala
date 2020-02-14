package org.mulesoft.als.server.modules.telemetry

import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.{LanguageServerBaseTest, LanguageServerBuilder, MockTelemetryClientNotifier}
import org.mulesoft.lsp.feature.telemetry.{MessageTypes, TelemetryMessage}

import scala.concurrent.{ExecutionContext, Future}
// TODO: keep each manager inside each test, instantiated with separated ClientNotifiers, which receive
//    the expected messages and compare inside.
class ServerTelemetryTest extends LanguageServerBaseTest {
  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  override def rootPath: String = ""

  private val mockTelemetryClientNotifier = new MockTelemetryClientNotifier

  override def buildServer(): LanguageServer = {
    val builder = new WorkspaceManagerFactoryBuilder(mockTelemetryClientNotifier,logger)
    val dm = builder.diagnosticManager()
    val factory = builder.buildWorkspaceManagerFactory()

    new LanguageServerBuilder(factory.documentManager, factory.workspaceManager)
        .addInitializableModule(dm)
        .build()
  }

  def checkMessages(telemetryMessages: Seq[MessageTypes.Value], msgs: Seq[TelemetryMessage]): Boolean =
    if (msgs.size == telemetryMessages.size &&
        (msgs.map(_.messageType).toSet diff telemetryMessages.map(_.id).toSet).isEmpty)
      true
    else {
      println(s"got: ${msgs.size}\texpected: ${telemetryMessages.size}")
      println(msgs.map(_.messageType).toSet diff telemetryMessages.map(_.id).toSet)
      false
    }


  //TODO: test again once Telemetry messaging is stable
  ignore("diagnostics test 001 - onFocus") {
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
      val mockWithTelemetry: (Int, () => Unit) => Future[Seq[TelemetryMessage]] =
        withTelemetry(mockTelemetryClientNotifier)

      for {
        a <- mockWithTelemetry((initMessages ++ openWithErrorTelemetryMessages(1)).size,
                               () => openFile(server)(libFilePath, libFileContent))
        b <- mockWithTelemetry(openWithErrorTelemetryMessages(2).size,
                               () => openFile(server)(mainFilePath, mainContent))
        c <- mockWithTelemetry(openWithErrorTelemetryMessages(1).size, () => onFocus(server)(libFilePath, 0))
        d <- mockWithTelemetry(
          openWithErrorTelemetryMessages(1).size,
          () => changeFile(server)(libFilePath, libFileContent.replace("b: string", "a: string"), 1))
        e <- mockWithTelemetry(openWithErrorTelemetryMessages(2).size, () => onFocus(server)(mainFilePath, 0))
      } yield {
        server.shutdown()
        assert(
          checkMessages(initMessages ++ openWithErrorTelemetryMessages(1), a) &&
            checkMessages(openWithErrorTelemetryMessages(2), b) &&
            checkMessages(openWithErrorTelemetryMessages(1), c) &&
            checkMessages(openWithErrorTelemetryMessages(1), d) &&
            checkMessages(openWithErrorTelemetryMessages(2), e)
        )
      }
    }
  }

  private def diagnosticTelemetryMessages(reportsQty: Int): Seq[MessageTypes.Value] = {
    Seq(
      MessageTypes.BEGIN_DIAGNOSTIC,
      MessageTypes.BEGIN_REPORT,
      MessageTypes.END_REPORT,
      MessageTypes.END_DIAGNOSTIC
    )
  }

  private val cleanOpenTelemetryMessages = Seq(
    MessageTypes.CHANGE_DOCUMENT,
    MessageTypes.BEGIN_PARSE,
    MessageTypes.END_PARSE
  )

  private val initMessages = Seq(
    MessageTypes.BEGIN_AMF_INIT,
    MessageTypes.END_AMF_INIT,
  )

  private def openWithErrorTelemetryMessages(reportsQty: Int): Seq[MessageTypes.Value] =
    cleanOpenTelemetryMessages ++ diagnosticTelemetryMessages(reportsQty)

}
