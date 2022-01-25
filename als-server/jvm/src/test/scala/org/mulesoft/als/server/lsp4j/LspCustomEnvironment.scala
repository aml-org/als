package org.mulesoft.als.server.lsp4j

import java.util.concurrent.CompletableFuture

import amf.client.convert.CoreClientConverters._
import amf.client.environment.DefaultEnvironment
import amf.client.remote.Content
import amf.client.resource.ClientResourceLoader
import amf.core.unsafe.PlatformSecrets
import org.eclipse.lsp4j.{DidOpenTextDocumentParams, TextDocumentItem}
import org.mulesoft.als.configuration.ResourceLoaderConverter
import org.mulesoft.als.server.MockDiagnosticClientNotifier
import org.mulesoft.als.server.logger.EmptyLogger
import org.mulesoft.als.server.modules.diagnostic.ALL_TOGETHER
import org.scalatest.{AsyncFunSuite, Matchers}
class LspCustomEnvironment extends AsyncFunSuite with Matchers with PlatformSecrets {

  test("test custom environment") {

    var calledRL = false
    val cl = new ClientResourceLoader {
      override def fetch(resource: String): CompletableFuture[Content] = {
        calledRL = true
        CompletableFuture.completedFuture(new Content("#%RAML 1.0 DataType\ntype: string", "jar:/api.raml"))
      }

      override def accepts(resource: String): Boolean = true
    }

    val notifier = new MockDiagnosticClientNotifier()
    val env      = DefaultEnvironment().addClientLoader(cl)
    val server = new LanguageServerImpl(
      new LanguageServerFactory(notifier)
        .withNotificationKind(ALL_TOGETHER)
        .withLogger(EmptyLogger)
        .withEnvironment(env)
        .build())
    val api =
      """#%RAML 1.0
        |title: test
        |types:
        | A: !include jar:/api.raml
        |""".stripMargin
    server.getTextDocumentService.didOpen(
      new DidOpenTextDocumentParams(new TextDocumentItem("file://api.raml", "raml1.0", 1, api)))
    for {
      r1 <- notifier.nextCall
      r2 <- notifier.nextCall
    } yield {
      r2.uri should be("jar:/api.raml")
      calledRL should be(true)
    }
  }

  test("Test client resource loader conversion") {
    val cl = new ClientResourceLoader {
      override def fetch(resource: String): CompletableFuture[Content] = {
        CompletableFuture.completedFuture(new Content("#%RAML 1.0 DataType\ntype: string", "jar:/api.raml"))
      }

      override def accepts(resource: String): Boolean = true
    }

    val int = ResourceLoaderConverter.internalResourceLoader(cl)

    ResourceLoaderMatcher.asClient(int)

    succeed
  }
}
