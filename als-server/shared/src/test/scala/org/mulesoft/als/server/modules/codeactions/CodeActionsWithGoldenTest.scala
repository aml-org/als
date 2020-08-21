package org.mulesoft.als.server.modules.codeactions

import org.mulesoft.als.common.diff.WorkspaceEditsTest
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.modules.reference.MarkerInfo
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.{LanguageServerBuilder, MockDiagnosticClientNotifier, ServerWithMarkerTest}
import org.mulesoft.lsp.feature.codeactions._
import org.mulesoft.lsp.feature.common.{Range, TextDocumentIdentifier}
import org.scalatest.Assertion

import scala.concurrent.{ExecutionContext, Future}

class CodeActionsWithGoldenTest extends ServerWithMarkerTest[Seq[CodeAction]] with WorkspaceEditsTest {
  override implicit val executionContext: ExecutionContext =
    ExecutionContext.Implicits.global

  test("RAML 1.0 payload type hould respond with the extract element to a declaration") {
    val path = "refactorextract/extract-element.raml"
    runTest(buildServer(), path, None)
      .flatMap { result =>
        checkGolden(path, result)
      }
  }

  test("OAS 3 schema should respond with the extract element to a declaration") {
    val path = "refactorextract/extract-schema-oas3.yaml"
    runTest(buildServer(), path, None)
      .flatMap { result =>
        checkGolden(path, result)
      }
  }

  test("OAS 3 example should respond with the extract element to a declaration") {
    val path = "refactorextract/extract-example-oas3.yaml"
    runTest(buildServer(), path, None)
      .flatMap { result =>
        checkGolden(path, result)
      }
  }

  private def checkGolden(path: String, result: Seq[CodeAction]): Future[Assertion] = {
    val containsExtract = result.exists(ca => ca.kind.contains(CodeActionKind.RefactorExtract))
    containsExtract should be(true)
    val goldenPath     = path.replace(".", "-golden.")
    val resolved       = filePath(platform.encodeURI(path))
    val goldenResolved = filePath(platform.encodeURI(goldenPath))

    for {
      goldenContent <- this.platform.resolve(goldenResolved).map(_.stream.toString)
      content       <- this.platform.resolve(resolved).map(_.stream.toString)
    } yield {
      val marker = findMarker(content)
      val maybeEdit = result
        .find(ca => ca.kind.contains(CodeActionKind.RefactorExtract))
        .flatMap(_.edit)
      maybeEdit.isDefined should be(true)
      assertWorkspaceEdits(maybeEdit.get, Some(goldenContent), Some(marker.patchedContent.content), path)
    }
  }

  override def rootPath: String = "actions/codeactions"

  def buildServer(): LanguageServer = {
    val factory =
      new WorkspaceManagerFactoryBuilder(new MockDiagnosticClientNotifier, logger).buildWorkspaceManagerFactory()
    new LanguageServerBuilder(factory.documentManager,
                              factory.workspaceManager,
                              factory.configurationManager,
                              factory.resolutionTaskManager)
      .addRequestModule(factory.codeActionManager)
      .build()
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
