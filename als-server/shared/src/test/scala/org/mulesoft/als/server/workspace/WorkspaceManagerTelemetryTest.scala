package org.mulesoft.als.server.workspace

import org.mulesoft.als.common.FileUtils
import org.mulesoft.als.server.modules.ManagersFactory
import org.mulesoft.als.server.{LanguageServerBaseTest, LanguageServerBuilder}
import org.mulesoft.lsp.common.{TextDocumentIdentifier, TextDocumentItem}
import org.mulesoft.lsp.configuration.{InitializeParams, TraceKind}
import org.mulesoft.lsp.feature.documentsymbol.{DocumentSymbolParams, DocumentSymbolRequestType}
import org.mulesoft.lsp.feature.telemetry.{MessageTypes, TelemetryMessage}
import org.mulesoft.lsp.server.LanguageServer
import org.mulesoft.lsp.textsync.DidOpenTextDocumentParams
import org.scalatest.Assertion

import scala.concurrent.{ExecutionContext, Future}
import scala.io.Source

class WorkspaceManagerTelemetryTest extends LanguageServerBaseTest {

  override implicit val executionContext = ExecutionContext.Implicits.global

  private val notifier = new MockTelemetryClientNotifier()
  private val factory  = ManagersFactory(notifier, platform, logger, withDiagnostics = false)

  private val editorFiles = factory.container

  test("Workspace Manager check parsing times (project should have 1, independent file 1)") {
    val main        = s"${filePath("ws1")}/api.raml"
    val independent = s"${filePath("ws1")}/independent.raml"
    val subdir      = s"${filePath("ws1")}/sub/type.raml"

    withServer[Assertion] { server =>
      val handler = server.resolveHandler(DocumentSymbolRequestType).value

      for {
        _ <- server.initialize(InitializeParams(None, Some(TraceKind.Off), rootUri = Some(s"${filePath("ws1")}")))
        _ <- handler(DocumentSymbolParams(TextDocumentIdentifier(main)))
        _ <- handler(DocumentSymbolParams(TextDocumentIdentifier(subdir)))
        _ <- handler(DocumentSymbolParams(TextDocumentIdentifier(main)))
        _ <- handler(DocumentSymbolParams(TextDocumentIdentifier(subdir)))
        _ <- {
          Future.successful {
            val source  = Source.fromFile(FileUtils.getPath(independent, platform))
            val content = source.getLines().mkString("\n")
            source.close()
            server.textDocumentSyncConsumer.didOpen(
              DidOpenTextDocumentParams(TextDocumentItem(independent, "RAML", 0, content)))
          }
        }
        _            <- handler(DocumentSymbolParams(TextDocumentIdentifier(independent)))
        _            <- handler(DocumentSymbolParams(TextDocumentIdentifier(subdir)))
        _            <- handler(DocumentSymbolParams(TextDocumentIdentifier(main)))
        allTelemetry <- Future.sequence { notifier.promises.map(p => p.future) }
      } yield {
        assert(allTelemetry.count(d => d.messageType == MessageTypes.BEGIN_PARSE.id) == 2)
      }
    }
  }

  override def buildServer(): LanguageServer =
    new LanguageServerBuilder(factory.documentManager, factory.workspaceManager, platform)
      .addRequestModule(factory.structureManager)
      .build()

  override def rootPath: String = "workspace"

}
