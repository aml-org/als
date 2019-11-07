package org.mulesoft.als.server.textsync

import org.mulesoft.als.server.modules.ManagersFactory
import org.mulesoft.als.server.workspace.WorkspaceRootHandler
import org.mulesoft.als.server.{LanguageServerBaseTest, LanguageServerBuilder}
import org.mulesoft.lsp.common.TextDocumentIdentifier
import org.mulesoft.lsp.feature.documentsymbol.{DocumentSymbolParams, DocumentSymbolRequestType}
import org.mulesoft.lsp.server.LanguageServer

import scala.concurrent.ExecutionContext

class ServerTextDocumentManagerTest extends LanguageServerBaseTest {

  override implicit val executionContext = ExecutionContext.Implicits.global

  override def rootPath: String = ""

  override def buildServer(): LanguageServer = {

    val factory = ManagersFactory(MockDiagnosticClientNotifier, new WorkspaceRootHandler(platform), platform, logger)
    new LanguageServerBuilder(factory.documentManager, platform)
      .addRequestModule(factory.structureManager)
      .build()
  }

  test("change document test 001") {
    withServer { server =>
      val content1 = "#%RAML 1.0\ntitle: test\n"
      val content2 = "#%RAML 1.0\ntitle: test\ntypes:\n  MyType: number\n"

      val url = "file:///changeDocumentTest001.raml"

      openFile(server)(url, content1)
      changeFile(server)(url, content2, 1)

      val handler = server.resolveHandler(DocumentSymbolRequestType).value

      handler(DocumentSymbolParams(TextDocumentIdentifier(url)))
        .collect { case Right(symbols) => symbols }
        .map(symbols =>
          symbols
            .collectFirst { case o if o.name == "title" => succeed }
            .getOrElse(fail("Invalid outline")))
    }
  }

  test("change document test 002") {
    withServer { server =>
      val content1 = "#%RAML 1.0\ntitle: test\n"
      val content2 = "#%RAML 1.0\ntitle: test\n"
      val content3 = "#%RAML 1.0\ntitle: test\nsome invalid string\ntypes:\n  MyType: number\n"

      val url = "file:///changeDocumentTest002.raml"

      openFile(server)(url, content1)
      changeFile(server)(url, content2, 1)
      changeFile(server)(url, content3, 2)

      val handler = server.resolveHandler(DocumentSymbolRequestType).value

      handler(DocumentSymbolParams(TextDocumentIdentifier(url)))
        .collect { case Right(symbols) => symbols }
        .map(symbols =>
          symbols
            .collectFirst { case o if o.name == "MyType" => fail("Should fail") }
            .getOrElse(succeed))
    }
  }

  test("change document with uri spaces test 003") {
    withServer { server =>
      val content1 = "#%RAML 1.0\ntitle: test\n"
      val content2 = "#%RAML 1.0\ntitle: test\nsome invalid string\ntypes:\n  MyType: number\n"

      val url = platform.encodeURI("file:///uri with spaces.raml")

      openFile(server)(url, content1)
      changeFile(server)(url, content2, 1)

      val handler = server.resolveHandler(DocumentSymbolRequestType).value

      handler(DocumentSymbolParams(TextDocumentIdentifier(url)))
        .collect { case Right(symbols) => symbols }
        .map(symbols =>
          symbols.headOption match {
            case Some(o) => o.name should be("title")
            case _       => fail("Missing first symbol")
        })
    }
  }
}
