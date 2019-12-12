package org.mulesoft.als.server

import org.mulesoft.als.server.modules.ManagersFactory
import org.mulesoft.als.server.textsync.TextDocument
import org.mulesoft.lsp.common.TextDocumentItem
import org.mulesoft.lsp.server.LanguageServer
import org.mulesoft.lsp.textsync.DidOpenTextDocumentParams
import org.scalatest.Assertion

import scala.concurrent.ExecutionContext

class LanguageServerImplTest extends LanguageServerBaseTest {

  override implicit val executionContext = ExecutionContext.Implicits.global

  private val factory = ManagersFactory(MockDiagnosticClientNotifier, platform, logger, withDiagnostics = false)

  private val editorFiles = factory.container

  test("LanguageServerImpl -> open file") {
    withServer[Assertion] { server =>
      server.textDocumentSyncConsumer.didOpen(
        DidOpenTextDocumentParams(
          TextDocumentItem("file://api.raml", "raml", 0, "#%RAML 1.0")
        ))

      val documentOption: Option[TextDocument] = editorFiles.get("file://api.raml")
      documentOption.isDefined should be(true)
      val document = documentOption.get
      assert(document.text.equals("#%RAML 1.0"))
    }
  }

  override def buildServer(): LanguageServer =
    new LanguageServerBuilder(factory.documentManager, factory.workspaceManager, platform).build()

  override def rootPath: String = ""
}
