package org.mulesoft.als.server.modules.formatting

import amf.core.client.scala.AMFGraphConfiguration
import org.mulesoft.als.common.MarkerFinderTest
import org.mulesoft.als.common.diff.{FileAssertionTest, WorkspaceEditsTest}
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.als.server.client.scala.LanguageServerBuilder
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.{LanguageServerBaseTest, MockDiagnosticClientNotifier}
import org.mulesoft.lsp.configuration.FormattingOptions
import org.mulesoft.lsp.edit.{TextEdit, WorkspaceEdit}
import org.mulesoft.lsp.feature.RequestHandler
import org.mulesoft.lsp.feature.common.{Range, TextDocumentIdentifier}
import org.mulesoft.lsp.feature.documentRangeFormatting.{
  DocumentRangeFormattingParams,
  DocumentRangeFormattingRequestType
}

import scala.concurrent.{ExecutionContext, Future}

class DocumentRangeFormattingTest
    extends LanguageServerBaseTest
    with FileAssertionTest
    with WorkspaceEditsTest
    with MarkerFinderTest {
  override val rootPath: String = "actions/documentRangeFormatting"

  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  def files(file: String): (String, String) =
    (filePath(platform.encodeURI(file)), filePath(platform.encodeURI("expected/" + file)))

  def buildServer(): LanguageServer = {
    val factory =
      new WorkspaceManagerFactoryBuilder(new MockDiagnosticClientNotifier, logger).buildWorkspaceManagerFactory()
    new LanguageServerBuilder(factory.documentManager,
                              factory.workspaceManager,
                              factory.configurationManager,
                              factory.resolutionTaskManager)
      .addRequestModule(factory.documentFormattingManager)
      .addRequestModule(factory.documentRangeFormattingManager)
      .build()
  }

  test("Should format whole document - Yaml") {
    val (original, expected) = files("test.yaml")
    runTest(buildServer(), original, expected).map(result => {
      assert(result.nonEmpty)
    })
  }

  test("Should format whole RAML document - Yaml") {
    val (original, expected) = files("raml.raml")
    runTest(buildServer(), original, expected).map(result => {
      assert(result.nonEmpty)
    })
  }

  test("Correct formatting when parent and son selected - Yaml") {
    val (original, expected) = files("parent-and-son.yaml")
    runTest(buildServer(), original, expected).map(result => {
      assert(result.nonEmpty)
    })
  }

  test("Test on sequence - Yaml") {
    val (original, expected) = files("seq.yaml")
    runTest(buildServer(), original, expected).map(result => {
      assert(result.nonEmpty)
    })
  }

  test("Should format whole Json document") {
    val (original, expected) = files("oas.json")
    runTest(buildServer(), original, expected).map(result => {
      assert(result.nonEmpty)
    })
  }

  def runTest(server: LanguageServer, fileUri: String, expectedUri: String): Future[Seq[TextEdit]] = {
    val fileId = TextDocumentIdentifier(fileUri)
    withServer(server)(server => {
      for {
        originalContent <- platform.fetchContent(fileUri, AMFGraphConfiguration.predefined()).map(_.stream.toString)
        markers         <- Future(findMarkers(originalContent))
        _               <- openFile(server)(fileUri, markers.head.content)
        formattingResult <- {
          assert(markers.length == 2)
          val start = markers.head
          val end   = markers.tail.head
          val range: Range =
            Range(LspRangeConverter.toLspPosition(start.position), LspRangeConverter.toLspPosition(end.position))
          val handler: RequestHandler[DocumentRangeFormattingParams, Seq[TextEdit]] =
            server.resolveHandler(DocumentRangeFormattingRequestType).get
          handler(DocumentRangeFormattingParams(fileId, range, FormattingOptions(2, insertSpaces = true)))
        }
        tmp <- writeTemporaryFile(expectedUri)(
          applyEdits(WorkspaceEdit(Some(Map(fileUri -> formattingResult)), None), Option(markers.head.content)))
        _ <- assertDifferences(tmp, expectedUri)
      } yield {
        formattingResult
      }
    })
  }

}
