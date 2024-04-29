package org.mulesoft.als.server.workspace

import amf.aml.client.scala.AMLConfiguration
import org.mulesoft.als.server.client.scala.LanguageServerBuilder
import org.mulesoft.als.server.modules.{WorkspaceManagerFactory, WorkspaceManagerFactoryBuilder}
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.protocol.configuration.AlsInitializeParams
import org.mulesoft.als.server.{LanguageServerBaseTest, MockDiagnosticClientNotifier}
import org.mulesoft.lsp.configuration.TraceKind
import org.mulesoft.lsp.feature.common.{TextDocumentIdentifier, TextDocumentItem}
import org.mulesoft.lsp.feature.documentsymbol.{DocumentSymbolParams, DocumentSymbolRequestType}
import org.mulesoft.lsp.textsync.DidOpenTextDocumentParams
import org.scalatest.compatible.Assertion

import scala.concurrent.ExecutionContext

class WorkspaceManagerSymbolTest extends LanguageServerBaseTest {

  override implicit val executionContext: ExecutionContext =
    ExecutionContext.Implicits.global

  private def testStructureForFile(server: LanguageServer, url: String) = {
    for {
      _ <- server.testInitialize(AlsInitializeParams(None, Some(TraceKind.Off), rootUri = Some(s"${filePath("ws1")}")))
      _ <- changeWorkspaceConfiguration(server)(changeConfigArgs(Some("api.raml"), filePath("ws1")))
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
      assert(s.nonEmpty)
    }
  }

  test("Workspace Manager check Symbol - main") {

    val diagnosticClientNotifier = new MockDiagnosticClientNotifier

    val factory: WorkspaceManagerFactory =
      new WorkspaceManagerFactoryBuilder(diagnosticClientNotifier)
        .buildWorkspaceManagerFactory()
    withServer[Assertion](buildServer(factory)) { server =>
      testStructureForFile(server, s"${filePath("ws1")}/api.raml")
    }
  }

  // todo: there is no structure for dependency files of a project
  ignore("Workspace Manager check Symbol - dependency") {

    val diagnosticClientNotifier = new MockDiagnosticClientNotifier

    val factory: WorkspaceManagerFactory =
      new WorkspaceManagerFactoryBuilder(diagnosticClientNotifier)
        .buildWorkspaceManagerFactory()
    withServer[Assertion](buildServer(factory)) { server =>
      testStructureForFile(server, s"${filePath("ws1")}/sub/type.raml")
    }
  }

  test("Workspace Manager check Symbol - independent") {

    val diagnosticClientNotifier = new MockDiagnosticClientNotifier

    val factory: WorkspaceManagerFactory =
      new WorkspaceManagerFactoryBuilder(diagnosticClientNotifier)
        .buildWorkspaceManagerFactory()
    withServer[Assertion](buildServer(factory)) { server =>
      testStructureForFile(server, s"${filePath("ws1")}/independent.raml")
    }
  }

  def buildServer(factory: WorkspaceManagerFactory): LanguageServer =
    new LanguageServerBuilder(
      factory.documentManager,
      factory.workspaceManager,
      factory.configurationManager,
      factory.resolutionTaskManager
    )
      .addRequestModule(factory.structureManager)
      .build()

  override def rootPath: String = "workspace"

}
