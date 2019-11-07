package org.mulesoft.als.server

import amf.core.unsafe.PlatformSecrets
import org.mulesoft.als.server.logger.EmptyLogger
import org.mulesoft.als.server.textsync.{TextDocument, TextDocumentContainer, TextDocumentManager}
import org.mulesoft.lsp.common.TextDocumentItem
import org.mulesoft.lsp.configuration.InitializeParams
import org.mulesoft.lsp.textsync.DidOpenTextDocumentParams
import org.scalatest.{Matchers, _}

import scala.concurrent.ExecutionContext

class LanguageServerImplTest extends AsyncFlatSpec with Matchers with PlatformSecrets with OptionValues {

  override implicit val executionContext = ExecutionContext.Implicits.global

  behavior of "LanguageServerImpl"
  it should "open file" in {
    val editorFiles     = TextDocumentContainer(platform)
    val documentManager = new TextDocumentManager(editorFiles, List.empty, EmptyLogger)

    val server = new LanguageServerBuilder(documentManager, platform).build()

    server
      .initialize(InitializeParams.default)
      .map(_ => {
        server.initialized()

        server.textDocumentSyncConsumer.didOpen(
          DidOpenTextDocumentParams(
            TextDocumentItem("file://api.raml", "raml", 0, "#%RAML 1.0")
          ))

        val documentOption: Option[TextDocument] = editorFiles.get("file://api.raml")
        documentOption.isDefined should be(true)
        val document = documentOption.get
        document.text should be("#%RAML 1.0")
        document.version should be(0)
      })
  }
}
