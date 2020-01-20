package org.mulesoft.als.server.workspace

import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.{LanguageServerBaseTest, LanguageServerBuilder, MockDiagnosticClientNotifier}
import org.mulesoft.lsp.common.TextDocumentIdentifier
import org.mulesoft.lsp.configuration.{InitializeParams, TraceKind}
import org.mulesoft.lsp.feature.documentsymbol.{DocumentSymbolParams, DocumentSymbolRequestType}
import org.mulesoft.lsp.server.{DefaultServerSystemConf, LanguageServer}
import org.scalatest.Assertion

import scala.concurrent.{ExecutionContext, Future}

class WorkspaceManagerWithoutDiagnosticsTest extends LanguageServerBaseTest {

  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  val diagnosticClientNotifier = new MockDiagnosticClientNotifier

  private val factory =
    new WorkspaceManagerFactoryBuilder(diagnosticClientNotifier, logger).buildWorkspaceManagerFactory()

  private val editorFiles = factory.container

  test("on close notification") {
    val changedFragment =
      """#%RAML 1.0 DataType
        |
        |properties:
        |  a: string
        |  b: string
      """.stripMargin
    val fragmentUri = s"${filePath("ws2/fragment.raml")}"
    withServer[Assertion] { server =>
      for {
        _               <- server.initialize(InitializeParams(None, Some(TraceKind.Off), rootUri = Some(s"${filePath("ws2")}")))
        apiContent      <- platform.resolve(s"${filePath("ws2/api.raml")}")
        fragmentContent <- platform.resolve(fragmentUri)
        _               <- Future { openFile(server)(s"${filePath("ws2/api.raml")}", apiContent.stream.toString) }
        _               <- Future { openFile(server)(fragmentUri, fragmentContent.stream.toString) }
        _               <- Future { changeFile(server)(fragmentUri, changedFragment, 1) }
        r1 <- {
          val handler = server.resolveHandler(DocumentSymbolRequestType).value

          handler(DocumentSymbolParams(TextDocumentIdentifier(fragmentUri)))
            .collect { case Right(symbols) => symbols }
            .map(symbols =>
              symbols.headOption match {
                case Some(o) => o.children.size should be(2)
                case _       => fail("Missing first symbol")
            })
        }
        r2 <- {
          if (r1 == succeed) {
            closeFile(server)(fragmentUri)
            val handler = server.resolveHandler(DocumentSymbolRequestType).value

            handler(DocumentSymbolParams(TextDocumentIdentifier(fragmentUri)))
              .collect { case Right(symbols) => symbols }
              .map(symbols =>
                symbols.headOption match {
                  case Some(o) => o.children.size should be(1)
                  case _       => fail("Missing first symbol")
              })
          } else Future.successful(r1)
        }
      } yield {
        r2
      }
    }
  }

  override def buildServer(): LanguageServer =
    new LanguageServerBuilder(factory.documentManager, factory.workspaceManager, DefaultServerSystemConf)
      .addRequestModule(factory.structureManager)
      .build()

  override def rootPath: String = "workspace"

}
