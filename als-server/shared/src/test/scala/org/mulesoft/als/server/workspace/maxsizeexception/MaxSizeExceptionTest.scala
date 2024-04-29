package org.mulesoft.als.server.workspace.maxsizeexception

import amf.aml.client.scala.AMLConfiguration
import org.mulesoft.als.server.client.scala.LanguageServerBuilder
import org.mulesoft.als.server.feature.diagnostic.{CleanDiagnosticTreeParams, CleanDiagnosticTreeRequestType}
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.protocol.configuration.AlsInitializeParams
import org.mulesoft.als.server.{LanguageServerBaseTest, MockDiagnosticClientNotifier}
import org.mulesoft.lsp.configuration.TraceKind
import org.mulesoft.lsp.feature.common.{Position, TextDocumentIdentifier, TextDocumentItem}
import org.mulesoft.lsp.feature.completion.{CompletionParams, CompletionRequestType}
import org.mulesoft.lsp.feature.documentsymbol.{DocumentSymbolParams, DocumentSymbolRequestType}
import org.mulesoft.lsp.textsync.DidOpenTextDocumentParams

import scala.concurrent.ExecutionContext

class MaxSizeExceptionTest extends LanguageServerBaseTest {
  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

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
      .addRequestModule(factory.completionManager)
      .addRequestModule(factory.cleanDiagnosticManager)
      .build()
  }

  private val initParams =
    AlsInitializeParams(None, Some(TraceKind.Off), rootUri = Some(s"${filePath("")}"), maxFileSize = Some(100))

  override def rootPath: String = "workspace/maxsize"

  test("Max Size limit should return empty response for requests - Outline") {
    withServer(buildServer()) { server =>
      val url = filePath("api.raml")
      for {
        _       <- server.testInitialize(initParams)
        content <- platform.fetchContent(url, AMLConfiguration.predefined())
        _ <- server.textDocumentSyncConsumer.didOpen(
          DidOpenTextDocumentParams(TextDocumentItem(url, "RAML", 0, content.stream.toString))
        )
        s <- {
          val handler = server.resolveHandler(DocumentSymbolRequestType).value
          handler(DocumentSymbolParams(TextDocumentIdentifier(url)))
            .collect { case Right(symbols) => symbols }
        }
      } yield {
        server.shutdown()
        assert(s.isEmpty)
      }
    }
  }

  test("Max Size limit should return empty response for requests - Completion") {
    withServer(buildServer()) { server =>
      val url = filePath("api.raml")
      for {
        _       <- server.testInitialize(initParams)
        content <- platform.fetchContent(url, AMLConfiguration.predefined())
        _ <- server.textDocumentSyncConsumer.didOpen(
          DidOpenTextDocumentParams(TextDocumentItem(url, "RAML", 0, content.stream.toString))
        )
        s <- {
          val handler = server.resolveHandler(CompletionRequestType).value
          handler(CompletionParams(TextDocumentIdentifier(url), Position(2, 0)))
        }
      } yield {
        server.shutdown()
        assert(s.left.get.isEmpty)
      }
    }
  }

  test("Max Size limit should not apply for Clean Diagnostic requests") {
    withServer(buildServer()) { server =>
      val url = filePath("api.raml")
      for {
        _       <- server.testInitialize(initParams)
        content <- platform.fetchContent(url, AMLConfiguration.predefined())
        _ <- server.textDocumentSyncConsumer.didOpen(
          DidOpenTextDocumentParams(TextDocumentItem(url, "RAML", 0, content.stream.toString))
        )
        s <- {
          val handler = server.resolveHandler(CleanDiagnosticTreeRequestType).value
          handler(CleanDiagnosticTreeParams(TextDocumentIdentifier(url)))
        }
      } yield {
        server.shutdown()
        assert(s.exists(_.diagnostics.nonEmpty))
      }
    }
  }

  test("Max Size for Workspace Manager check validation Stack - Performance case - Clean Diagnostic") {
    withServer(buildServer()) { server =>
      val rootFolder = "file://als-server/shared/src/test/resources/workspace/performance-stack/"
      val url        = s"$rootFolder/references.raml"
      for {
        _ <- server.testInitialize(
          AlsInitializeParams(None, Some(TraceKind.Off), rootUri = Some(rootFolder), maxFileSize = Some(100))
        )
        _ <- setMainFile(server)(rootFolder, "references.raml")
        s <- {
          val handler = server.resolveHandler(CleanDiagnosticTreeRequestType).value
          handler(CleanDiagnosticTreeParams(TextDocumentIdentifier(url)))
        }
      } yield {
        server.shutdown()
        assert(s.nonEmpty)
      }
    }
  }

  test("Max Size limit should return empty response for requests - Completion - Max size acc by multiple files") {
    withServer(buildServer()) { server =>
      val url = filePath("api.raml")
      for {
        _ <- server.testInitialize(
          AlsInitializeParams(None, Some(TraceKind.Off), rootUri = Some(s"${filePath("")}"), maxFileSize = Some(1200))
        )
        content <- platform.fetchContent(url, AMLConfiguration.predefined())
        _ <- server.textDocumentSyncConsumer.didOpen(
          DidOpenTextDocumentParams(TextDocumentItem(url, "RAML", 0, content.stream.toString))
        )
        s <- {
          val handler = server.resolveHandler(CompletionRequestType).value
          handler(CompletionParams(TextDocumentIdentifier(url), Position(2, 0)))
        }
      } yield {
        server.shutdown()
        assert(s.left.get.isEmpty)
      }
    }
  }

  test(
    "Max Size for Workspace Manager check validation Stack - Performance case - Clean Diagnostic - Max size acc by multiple files"
  ) {
    withServer(buildServer()) { server =>
      val rootFolder = "file://als-server/shared/src/test/resources/workspace/performance-stack/"
      val url        = s"$rootFolder/references.raml"
      for {
        _ <- server.testInitialize(
          AlsInitializeParams(None, Some(TraceKind.Off), rootUri = Some(rootFolder), maxFileSize = Some(1000))
        )
        _ <- setMainFile(server)(rootFolder, "references.raml")
        s <- {
          val handler = server.resolveHandler(CleanDiagnosticTreeRequestType).value
          handler(CleanDiagnosticTreeParams(TextDocumentIdentifier(url)))
        }
      } yield {
        server.shutdown()
        assert(s.nonEmpty)
      }
    }
  }
}
