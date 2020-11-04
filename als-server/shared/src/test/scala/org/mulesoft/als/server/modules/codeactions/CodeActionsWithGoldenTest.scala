package org.mulesoft.als.server.modules.codeactions

import org.mulesoft.als.common.MarkerInfo
import org.mulesoft.als.common.diff.WorkspaceEditsTest
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.{LanguageServerBuilder, MockTelemetryParsingClientNotifier, ServerWithMarkerTest}
import org.mulesoft.lsp.feature.codeactions.CodeActionKind.CodeActionKind
import org.mulesoft.lsp.feature.codeactions._
import org.mulesoft.lsp.feature.common.{Range, TextDocumentIdentifier}
import org.scalatest.Assertion

import scala.concurrent.{ExecutionContext, Future}

class CodeActionsWithGoldenTest extends ServerWithMarkerTest[Seq[CodeAction]] with WorkspaceEditsTest {
  override implicit val executionContext: ExecutionContext =
    ExecutionContext.Implicits.global

  test("RAML 1.0 payload type should respond with the extract element to a declaration") {
    val path = "refactorextract/extract-element.raml"
    runTest(buildServer(), path, None)
      .flatMap { result =>
        checkExtractGolden(path, result)
      }
  }

  test("OAS 3 schema should respond with the extract element to a declaration") {
    val path = "refactorextract/extract-schema-oas3.yaml"
    runTest(buildServer(), path, None)
      .flatMap { result =>
        checkExtractGolden(path, result)
      }
  }

  test("OAS 3 example should respond with the extract element to a declaration") {
    val path = "refactorextract/extract-example-oas3.yaml"
    runTest(buildServer(), path, None)
      .flatMap { result =>
        checkExtractGolden(path, result)
      }
  }

  test("RAML 1 Extract type from property key, having `types` already declared") {
    val path = "refactorextract/extract-element-with-types-declared.raml"
    runTest(buildServer(), path, None)
      .flatMap { result =>
        checkExtractGolden(path, result)
      }
  }

  test("RAML 1 Extract type from sublevel property key") {
    val path = "refactorextract/sublevel-type.raml"
    runTest(buildServer(), path, None)
      .flatMap { result =>
        checkExtractGolden(path, result)
      }
  }

  test("RAML 1 delete type node") {
    val path = "delete/raml-type.raml"
    runTest(buildServer(), path, None)
      .flatMap { result =>
        checkRefactorGolden(path, result)
      }
  }

  test("Oas 3 delete type node (path ref") {
    val path = "delete/oas3-type.yaml"
    runTest(buildServer(), path, None)
      .flatMap { result =>
        checkRefactorGolden(path, result)
      }
  }

  private def checkExtractGolden(path: String, result: Seq[CodeAction]): Future[Assertion] = {
    checkGolden(path, result, CodeActionKind.RefactorExtract)
  }

  private def checkRefactorGolden(path: String, result: Seq[CodeAction]): Future[Assertion] = {
    checkGolden(path, result, CodeActionKind.Refactor)
  }

  private def checkGolden(path: String, result: Seq[CodeAction], kind: CodeActionKind): Future[Assertion] = {
    val containsExtract = result.exists(ca => ca.kind.contains(kind))
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
        .find(ca => ca.kind.contains(kind))
        .flatMap(_.edit)
      maybeEdit.isDefined should be(true)
      assertWorkspaceEdits(maybeEdit.get, Some(goldenContent), Some(marker.content), path)
    }
  }

  override def rootPath: String = "actions/codeactions"

  def buildServer(): LanguageServer = {
    val factory =
      new WorkspaceManagerFactoryBuilder(notifier, logger).buildWorkspaceManagerFactory()
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

  override val notifier: MockTelemetryParsingClientNotifier = new MockTelemetryParsingClientNotifier()
}
