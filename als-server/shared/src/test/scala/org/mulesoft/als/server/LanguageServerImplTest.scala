package org.mulesoft.als.server

import amf.core.unsafe.PlatformSecrets
import org.mulesoft.als.server.logger.EmptyLogger
import org.mulesoft.als.server.textsync.TextDocumentManager
import org.mulesoft.lsp.common.TextDocumentItem
import org.mulesoft.lsp.configuration.{ClientCapabilities, InitializeParams, TraceKind}
import org.mulesoft.lsp.textsync.DidOpenTextDocumentParams
import org.scalatest.{Matchers, _}

import scala.concurrent.ExecutionContext

class LanguageServerImplTest extends AsyncFlatSpec with Matchers with PlatformSecrets with OptionValues {

  override implicit val executionContext = ExecutionContext.Implicits.global

  behavior of "LanguageServerImpl"
  it should "open file" in {
    val documentManager = new TextDocumentManager(platform, EmptyLogger)

    val server = LanguageServerBuilder()
      .withTextDocumentSyncConsumer(documentManager)
      .build()

    server
      .initialize(InitializeParams.default)
      .map(_ => {
        server.initialized()

        server.textDocumentSyncConsumer.didOpen(
          DidOpenTextDocumentParams(
            TextDocumentItem("file://api.raml", "raml", 0, "#%RAML 1.0")
          ))

        val document = documentManager.getTextDocument("file://api.raml").value

        document.text should be("#%RAML 1.0")
        document.version should be(0)
      })
  }

}
