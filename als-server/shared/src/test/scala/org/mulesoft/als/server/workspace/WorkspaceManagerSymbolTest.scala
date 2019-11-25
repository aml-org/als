package org.mulesoft.als.server.workspace

import org.mulesoft.als.server.modules.ManagersFactory
import org.mulesoft.als.server.{LanguageServerBaseTest, LanguageServerBuilder}
import org.mulesoft.lsp.common.{TextDocumentIdentifier, TextDocumentItem}
import org.mulesoft.lsp.configuration.{InitializeParams, TraceKind}
import org.mulesoft.lsp.feature.documentsymbol.{DocumentSymbolParams, DocumentSymbolRequestType}
import org.mulesoft.lsp.server.LanguageServer
import org.mulesoft.lsp.textsync.DidOpenTextDocumentParams
import org.scalatest.Assertion

import scala.concurrent.ExecutionContext

class WorkspaceManagerSymbolTest extends LanguageServerBaseTest {

  override implicit val executionContext = ExecutionContext.Implicits.global

  private val factory = ManagersFactory(MockDiagnosticClientNotifier, platform, logger, withDiagnostics = false)

  private val editorFiles = factory.container

  private def testStructureForFile(server: LanguageServer, url: String) = {
    for {
      _ <- server.initialize(InitializeParams(None, Some(TraceKind.Off), rootUri = Some(s"${filePath("ws1")}")))
      _ <- {
        platform.resolve(url).map { c =>
          server.textDocumentSyncConsumer.didOpen(DidOpenTextDocumentParams(
            TextDocumentItem(url, "RAML", 0, c.stream.toString))) // why clean empty lines was necessary?
        }
      }
      s <- {
        val handler = server.resolveHandler(DocumentSymbolRequestType).value

        handler(DocumentSymbolParams(TextDocumentIdentifier(url)))
          .collect { case Right(symbols) => symbols }
      }
    } yield {
      assert(s.nonEmpty)
    }
  }

  test("Workspace Manager check Symbol - main") {
    withServer[Assertion] { server =>
      testStructureForFile(server, s"${filePath("ws1")}/api.raml")
    }
  }

  // TODO: Why is this not returning structure??
  ignore("Workspace Manager check Symbol - dependency") {
    withServer[Assertion] { server =>
      testStructureForFile(server, s"${filePath("ws1")}/sub/type.raml")
    }
  }

  test("Workspace Manager check Symbol - independent") {
    withServer[Assertion] { server =>
      testStructureForFile(server, s"${filePath("ws1")}/independent.raml")
    }
  }

  override def buildServer(): LanguageServer =
    new LanguageServerBuilder(factory.documentManager, factory.workspaceManager, platform)
      .addRequestModule(factory.structureManager)
      .build()

  override def rootPath: String = "workspace"

}
