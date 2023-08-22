package org.mulesoft.als.server.textsync

import org.mulesoft.als.server.client.scala.LanguageServerBuilder
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.{LanguageServerBaseTest, MockDiagnosticClientNotifier}
import org.mulesoft.lsp.feature.common.TextDocumentIdentifier
import org.mulesoft.lsp.feature.documentsymbol.{DocumentSymbolParams, DocumentSymbolRequestType}

import scala.concurrent.ExecutionContext

class ServerTextDocumentManagerTest extends LanguageServerBaseTest {

  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  override def rootPath: String = ""

  def buildServer(): LanguageServer = {

    val factory =
      new WorkspaceManagerFactoryBuilder(new MockDiagnosticClientNotifier).buildWorkspaceManagerFactory()
    new LanguageServerBuilder(
      factory.documentManager,
      factory.workspaceManager,
      factory.configurationManager,
      factory.resolutionTaskManager
    )
      .addRequestModule(factory.structureManager)
      .build()
  }

  test("change document test 001") {
    withServer(buildServer()) { server =>
      val content1 = "#%RAML 1.0\ntitle: test\n"
      val content2 = "#%RAML 1.0\ntitle: test\ntypes:\n  MyType: number\n"

      val url = "file:///changeDocumentTest001.raml"

      val handler = server.resolveHandler(DocumentSymbolRequestType).value
      for {
        _ <- openFile(server)(url, content1)
        _ <- changeFile(server)(url, content2, 1)
        _ <- handler(DocumentSymbolParams(TextDocumentIdentifier(url)))
          .collect { case Right(symbols) => symbols }
          .map(symbols =>
            symbols
              .collectFirst { case o if o.name == "title" => succeed }
              .getOrElse(fail("Invalid outline"))
          )
      } yield {
        succeed
      }
    }
  }

  test("change document test 002") {
    withServer(buildServer()) { server =>
      val content1 = "#%RAML 1.0\ntitle: test\n"
      val content2 = "#%RAML 1.0\ntitle: test\n"
      val content3 = "#%RAML 1.0\ntitle: test\nsome invalid string\ntypes:\n  MyType: number\n"

      val url = "file:///changeDocumentTest002.raml"

      val handler = server.resolveHandler(DocumentSymbolRequestType).value
      for {
        _ <- openFile(server)(url, content1)
        _ <- changeFile(server)(url, content2, 1)
        _ <- changeFile(server)(url, content3, 2)
        _ <- handler(DocumentSymbolParams(TextDocumentIdentifier(url)))
          .collect { case Right(symbols) => symbols }
          .map(symbols =>
            symbols
              .collectFirst { case o if o.name == "MyType" => fail("Should fail") }
              .getOrElse(succeed)
          )
      } yield succeed
    }
  }

  // todo: encoded URIs not working correctly
  test("change document with uri spaces test 003") {
    withServer(buildServer()) { server =>
      val content1 = "#%RAML 1.0\ntitle: test\n"
      val content2 = "#%RAML 1.0\ntitle: test\nsome invalid string\ntypes:\n  MyType: number\n"

      val url = platform.encodeURI("file:///uri with spaces.raml")

      val handler = server.resolveHandler(DocumentSymbolRequestType).value

      for {
        _ <- openFile(server)(url, content1)
        _ <- changeFile(server)(url, content2, 1)
        _ <- handler(DocumentSymbolParams(TextDocumentIdentifier(url)))
          .collect { case Right(symbols) => symbols }
          .map(symbols =>
            symbols.headOption match {
              case Some(o) => o.name should be("title")
              case _       => fail("Missing first symbol")
            }
          )
      } yield {
        succeed
      }
    }
  }

}
