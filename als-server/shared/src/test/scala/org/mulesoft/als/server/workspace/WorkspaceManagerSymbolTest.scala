package org.mulesoft.als.server.workspace

import org.mulesoft.als.server.modules.{WorkspaceManagerFactory, WorkspaceManagerFactoryBuilder}
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.protocol.configuration.AlsInitializeParams
import org.mulesoft.als.server.{LanguageServerBaseTest, LanguageServerBuilder, MockDiagnosticClientNotifier}
import org.mulesoft.amfintegration.amfconfiguration.AmfConfigurationWrapper
import org.mulesoft.lsp.configuration.TraceKind
import org.mulesoft.lsp.feature.common.{TextDocumentIdentifier, TextDocumentItem}
import org.mulesoft.lsp.feature.documentsymbol.{DocumentSymbolParams, DocumentSymbolRequestType}
import org.mulesoft.lsp.textsync.DidOpenTextDocumentParams
import org.scalatest.Assertion

import scala.concurrent.ExecutionContext

class WorkspaceManagerSymbolTest extends LanguageServerBaseTest {

  override implicit val executionContext: ExecutionContext =
    ExecutionContext.Implicits.global

  private def testStructureForFile(server: LanguageServer, url: String) = {
    val amfConfiguration = AmfConfigurationWrapper()

    for {
      _ <- server.initialize(AlsInitializeParams(None, Some(TraceKind.Off), rootUri = Some(s"${filePath("ws1")}")))
      _ <- {
        amfConfiguration.fetchContent(url).map { c =>
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

    val diagnosticClientNotifier = new MockDiagnosticClientNotifier

    val factory: WorkspaceManagerFactory =
      new WorkspaceManagerFactoryBuilder(diagnosticClientNotifier, logger)
        .buildWorkspaceManagerFactory()
    withServer[Assertion](buildServer(factory)) { server =>
      testStructureForFile(server, s"${filePath("ws1")}/api.raml")
    }
  }

  // TODO: Why is this not returning structure??
  ignore("Workspace Manager check Symbol - dependency") {

    val diagnosticClientNotifier = new MockDiagnosticClientNotifier

    val factory: WorkspaceManagerFactory =
      new WorkspaceManagerFactoryBuilder(diagnosticClientNotifier, logger)
        .buildWorkspaceManagerFactory()
    withServer[Assertion](buildServer(factory)) { server =>
      testStructureForFile(server, s"${filePath("ws1")}/sub/type.raml")
    }
  }

  test("Workspace Manager check Symbol - independent") {

    val diagnosticClientNotifier = new MockDiagnosticClientNotifier

    val factory: WorkspaceManagerFactory =
      new WorkspaceManagerFactoryBuilder(diagnosticClientNotifier, logger)
        .buildWorkspaceManagerFactory()
    withServer[Assertion](buildServer(factory)) { server =>
      testStructureForFile(server, s"${filePath("ws1")}/independent.raml")
    }
  }

  def buildServer(factory: WorkspaceManagerFactory): LanguageServer =
    new LanguageServerBuilder(factory.documentManager,
                              factory.workspaceManager,
                              factory.configurationManager,
                              factory.resolutionTaskManager)
      .addRequestModule(factory.structureManager)
      .build()

  override def rootPath: String = "workspace"

}
