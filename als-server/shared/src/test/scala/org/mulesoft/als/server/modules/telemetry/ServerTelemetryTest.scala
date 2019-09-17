package org.mulesoft.als.server.modules.telemetry

import amf.core.remote.Platform
import amf.internal.environment.Environment
import org.mulesoft.als.common.DirectoryResolver
import org.mulesoft.als.server.modules.ast.AstManager
import org.mulesoft.als.server.modules.diagnostic.DiagnosticManager
import org.mulesoft.als.server.textsync.TextDocumentManager
import org.mulesoft.als.server.{LanguageServerBaseTest, LanguageServerBuilder}
import org.mulesoft.lsp.feature.telemetry.{MessageTypes, TelemetryMessage}

import scala.concurrent.{ExecutionContext, Future}

class ServerTelemetryTest extends LanguageServerBaseTest {
  override implicit val executionContext = ExecutionContext.Implicits.global

  override def rootPath: String = ""

  private val mockTelemetryClientNotifier = new MockTelemetryClientNotifier

  override def addModules(documentManager: TextDocumentManager,
                          platform: Platform,
                          directoryResolver: DirectoryResolver,
                          baseEnvironment: Environment,
                          builder: LanguageServerBuilder): LanguageServerBuilder = {

    val telemetryManager = new TelemetryManager(mockTelemetryClientNotifier, logger)
    val astManager       = new AstManager(documentManager, baseEnvironment, telemetryManager, platform, logger)
    val diagnosticManager =
      new DiagnosticManager(documentManager,
                            astManager,
                            telemetryManager,
                            mockTelemetryClientNotifier,
                            platform,
                            logger)

    builder
      .addInitializable(astManager)
      .addInitializableModule(diagnosticManager)
      .addInitializableModule(telemetryManager)
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
      val mockWithTelemetry: (Int, () => Unit) => Future[Seq[TelemetryMessage]] =
        withTelemetry(mockTelemetryClientNotifier)

      for {
        a <- mockWithTelemetry(openWithErrorTelemetryMessages(1).size,
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
          checkMessages(openWithErrorTelemetryMessages(1), a) &&
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
    ) ++ (0 until reportsQty map (_ => MessageTypes.GOT_DIAGNOSTICS))
  }

  private val cleanOpenTelemetryMessages = Seq(
    MessageTypes.CHANGE_DOCUMENT,
    MessageTypes.BEGIN_PARSE,
    MessageTypes.END_PARSE
  )

  private def openWithErrorTelemetryMessages(reportsQty: Int): Seq[MessageTypes.Value] =
    cleanOpenTelemetryMessages ++ diagnosticTelemetryMessages(reportsQty)

}
