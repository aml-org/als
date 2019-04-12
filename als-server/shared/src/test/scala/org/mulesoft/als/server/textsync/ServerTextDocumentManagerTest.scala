package org.mulesoft.als.server.textsync

import org.mulesoft.als.server.modules.ast.AstManager
import org.mulesoft.als.server.modules.hlast.HlAstManager
import org.mulesoft.als.server.modules.structure.StructureManager
import org.mulesoft.als.server.platform.ServerPlatform
import org.mulesoft.als.server.{LanguageServerBaseTest, LanguageServerBuilder}
import org.mulesoft.lsp.common.TextDocumentIdentifier
import org.mulesoft.lsp.feature.documentsymbol.{DocumentSymbolParams, DocumentSymbolRequestType}

import scala.concurrent.ExecutionContext

class ServerTextDocumentManagerTest extends LanguageServerBaseTest {

  override implicit val executionContext = ExecutionContext.Implicits.global
  override def rootPath: String          = ""

  override def addModules(documentManager: TextDocumentManager,
                          serverPlatform: ServerPlatform,
                          builder: LanguageServerBuilder): LanguageServerBuilder = {

    val astManager   = new AstManager(documentManager, serverPlatform, logger)
    val hlAstManager = new HlAstManager(documentManager, astManager, serverPlatform, logger)
    val module       = new StructureManager(documentManager, hlAstManager, serverPlatform, logger)

    builder
      .addInitializable(astManager)
      .addInitializable(hlAstManager)
      .addRequestModule(module)
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
            .collectFirst { case o if o.name == "MyType" => succeed }
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
}
