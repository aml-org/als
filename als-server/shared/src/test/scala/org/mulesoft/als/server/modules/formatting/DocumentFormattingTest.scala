package org.mulesoft.als.server.modules.formatting

import amf.core.client.scala.AMFGraphConfiguration
import org.mulesoft.als.common.diff.{FileAssertionTest, WorkspaceEditsTest}
import org.mulesoft.als.server.client.scala.LanguageServerBuilder
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.protocol.configuration.AlsInitializeParams
import org.mulesoft.als.server.{LanguageServerBaseTest, MockDiagnosticClientNotifier}
import org.mulesoft.lsp.configuration.FormattingOptions
import org.mulesoft.lsp.edit.{TextEdit, WorkspaceEdit}
import org.mulesoft.lsp.feature.RequestHandler
import org.mulesoft.lsp.feature.common.TextDocumentIdentifier
import org.mulesoft.lsp.feature.documentFormatting.{DocumentFormattingParams, DocumentFormattingRequestType}

import scala.concurrent.{ExecutionContext, Future}

class DocumentFormattingTest extends LanguageServerBaseTest with FileAssertionTest with WorkspaceEditsTest {
  override val rootPath: String = "actions/documentFormatting"

  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  def files(file: String): (String, String) =
    (filePath(platform.encodeURI(file)), filePath(platform.encodeURI("expected/" + file)))

  def buildServer(): LanguageServer = {
    val factory =
      new WorkspaceManagerFactoryBuilder(new MockDiagnosticClientNotifier, logger).buildWorkspaceManagerFactory()
    new LanguageServerBuilder(
      factory.documentManager,
      factory.workspaceManager,
      factory.configurationManager,
      factory.resolutionTaskManager
    )
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

  test("Should format whole Json document") {
    val (original, expected) = files("oas.json")
    runTest(buildServer(), original, expected).map(result => {
      assert(result.nonEmpty)
    })
  }

  test("Should format library") {
    val (original, expected) = files("library.raml")
    runTest(buildServer(), original, expected).map(result => {
      assert(result.nonEmpty)
    })
  }

  test("Should format typed fragment") {
    val (original, expected) = files("typed-fragment.raml")
    runTest(buildServer(), original, expected).map(result => {
      assert(result.nonEmpty)
    })
  }

  test("Should format big api") {
    val (original, expected) = files("api.raml")
    runTest(buildServer(), original, expected).map(result => {
      assert(result.nonEmpty)
    })
  }

  // This is ignored because AMF does not provide AST for the whole document,
  //  just starting from the first significant part
  ignore("Should format RAML header") {
    val (original, expected) = files("header.raml")
    runTest(buildServer(), original, expected).map(result => {
      assert(result.nonEmpty)
    })
  }

  // This is ignored because AMF does not provide AST for the whole document,
  //  just starting from the first significant part
  test("Should format json schema document") {
    val (original, expected) = files("json-schema.json")
    runTest(buildServer(), original, expected).map(result => {
      assert(result.nonEmpty)
    })
  }

  def runTest(server: LanguageServer, fileUri: String, expectedUri: String): Future[Seq[TextEdit]] = {
    val fileId = TextDocumentIdentifier(fileUri)
    withServer(server)(server => {
      for {
        original <- platform.fetchContent(fileUri, AMFGraphConfiguration.predefined())
        _        <- openFile(server)(fileUri, original.stream.toString)
        formattingResult <- {
          val handler: RequestHandler[DocumentFormattingParams, Seq[TextEdit]] =
            server.resolveHandler(DocumentFormattingRequestType).get
          handler(DocumentFormattingParams(fileId, FormattingOptions(2, insertSpaces = true)))
        }
        tmp <- writeTemporaryFile(expectedUri)(
          applyEdits(WorkspaceEdit(Some(Map(fileUri -> formattingResult)), None), Option(original.stream.toString))
        )
        _ <- assertDifferences(tmp, expectedUri)
      } yield {
        formattingResult
      }
    })

  }
}
