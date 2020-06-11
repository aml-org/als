package org.mulesoft.als.server.workspace

import org.mulesoft.als.server.modules.{WorkspaceManagerFactory, WorkspaceManagerFactoryBuilder}
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.protocol.configuration.AlsInitializeParams
import org.mulesoft.als.server.{LanguageServerBaseTest, LanguageServerBuilder, MockDiagnosticClientNotifier}
import org.mulesoft.lsp.configuration.TraceKind
import org.mulesoft.lsp.feature.common.TextDocumentIdentifier
import org.mulesoft.lsp.feature.documentsymbol.{DocumentSymbolParams, DocumentSymbolRequestType}
import org.scalatest.Assertion

import scala.concurrent.{ExecutionContext, Future}

class WorkspaceManagerWithoutDiagnosticsTest extends LanguageServerBaseTest {

  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  test("on close notification") {
    val diagnosticClientNotifier = new MockDiagnosticClientNotifier

    val factory: WorkspaceManagerFactory =
      new WorkspaceManagerFactoryBuilder(diagnosticClientNotifier, logger).buildWorkspaceManagerFactory()

    val changedFragment =
      """#%RAML 1.0 DataType
        |
        |properties:
        |  a: string
        |  b: string
      """.stripMargin
    val fragmentUri = s"${filePath("ws2/fragment.raml")}"
    withServer[Assertion](buildServer(factory)) { server =>
      for {
        _               <- server.initialize(AlsInitializeParams(None, Some(TraceKind.Off), rootUri = Some(s"${filePath("ws2")}")))
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
                case Some(o) =>
                  o.name should be("properties")
                  o.children.size should be(2)
                case _ => fail("Missing first symbol")
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

  def buildServer(factory: WorkspaceManagerFactory): LanguageServer =
    new LanguageServerBuilder(factory.documentManager, factory.workspaceManager, factory.configurationManager, factory.resolutionTaskManager)
      .addRequestModule(factory.structureManager)
      .build()

  override def rootPath: String = "workspace"

}
