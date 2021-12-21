package org.mulesoft.als.server.modules.codeactions

import org.mulesoft.als.actions.codeactions.plugins.AllCodeActions
import org.mulesoft.als.actions.codeactions.plugins.testaction.TestCodeAction
import org.mulesoft.als.common.MarkerInfo
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.als.server.client.scala.LanguageServerBuilder
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.modules.actions.CodeActionManager
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.{MockTelemetryParsingClientNotifier, ServerWithMarkerTest}
import org.mulesoft.lsp.feature.codeactions._
import org.mulesoft.lsp.feature.common.{Range, TextDocumentIdentifier}

import scala.concurrent.{ExecutionContext, Future}

class CodeActionsWithPositionMarkerTest extends ServerWithMarkerTest[Seq[CodeAction]] {
  override implicit val executionContext: ExecutionContext =
    ExecutionContext.Implicits.global

  override def rootPath: String = "actions/codeactions"

  override val notifier: MockTelemetryParsingClientNotifier = new MockTelemetryParsingClientNotifier()

  def buildServer(): LanguageServer = {
    val factory =
      new WorkspaceManagerFactoryBuilder(notifier, logger).buildWorkspaceManagerFactory()
    val codeActionManager =
      new CodeActionManager(
        AllCodeActions.all :+ TestCodeAction,
        factory.workspaceManager,
        factory.configurationManager.getConfiguration,
        factory.telemetryManager,
        logger,
        factory.directoryResolver
      )
    new LanguageServerBuilder(factory.documentManager,
                              factory.workspaceManager,
                              factory.configurationManager,
                              factory.resolutionTaskManager)
      .addRequestModule(codeActionManager)
      .build()
  }

  test("Should respond with the test code action") {
    runTest(buildServer(), "emptyfile/empty1.raml", None)
      .map { result =>
        val containsTest = result.exists(ca => ca.kind.contains(CodeActionKind.Test))
        containsTest should be(true)
      }
  }

  test("Should NOT respond with the test code action") {
    runTest(buildServer(), "emptyfile/empty2.raml", None)
      .map { result =>
        val containsTest = result.exists(ca => ca.kind.contains(CodeActionKind.Test))
        containsTest should be(false)
      }
  }

  test("Should NOT respond with the extract element to an example declaration") {
    runTest(buildServer(), "refactorextract/extract-example.raml", None)
      .map { result =>
        val containsExtract = result.exists(ca => ca.kind.contains(CodeActionKind.RefactorExtract))
        containsExtract should be(false)
      }
  }

  override def getAction(path: String, server: LanguageServer, markerInfo: MarkerInfo): Future[Seq[CodeAction]] = {
    val handler  = server.resolveHandler(CodeActionRequestType).value
    val position = LspRangeConverter.toLspPosition(markerInfo.position)

    handler(CodeActionParams(TextDocumentIdentifier(path), Range(position, position), CodeActionContext(Nil, None)))
      .andThen({
        case _ => closeFile(server)(path)
      })
  }
}
