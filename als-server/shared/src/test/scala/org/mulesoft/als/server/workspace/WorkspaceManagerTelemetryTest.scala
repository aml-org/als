package org.mulesoft.als.server.workspace

import org.mulesoft.als.server.modules.ManagersFactory
import org.mulesoft.als.server.modules.structure.StructureManager
import org.mulesoft.als.server.{LanguageServerBaseTest, LanguageServerBuilder}
import org.mulesoft.lsp.common.{TextDocumentIdentifier, TextDocumentItem}
import org.mulesoft.lsp.configuration.{InitializeParams, TraceKind}
import org.mulesoft.lsp.feature.documentsymbol.{DocumentSymbolParams, DocumentSymbolRequestType}
import org.mulesoft.lsp.feature.telemetry.MessageTypes
import org.mulesoft.lsp.server.LanguageServer
import org.mulesoft.lsp.textsync.DidOpenTextDocumentParams
import org.scalatest.Assertion

import scala.concurrent.{ExecutionContext, Future}

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
          platform
            .resolve(independent)
            .map(c => {
              val content = c.stream.toString
              server.textDocumentSyncConsumer.didOpen(
                DidOpenTextDocumentParams(TextDocumentItem(independent, "RAML", 0, content)))
            })
        }
        _ <- handler(DocumentSymbolParams(TextDocumentIdentifier(independent)))
        _ <- handler(DocumentSymbolParams(TextDocumentIdentifier(subdir)))
        _ <- handler(DocumentSymbolParams(TextDocumentIdentifier(main)))
        allTelemetry <- Future.sequence { // TODO: Check for consistency
          notifier.promises.map(p => p.future)
        }
      } yield {
        notifier.promises.clear()
        assert(allTelemetry.count(d => d.messageType == MessageTypes.BEGIN_PARSE.id) == 2)
      }
    }
  }

  test("Workspace Manager check parsing times when reference removed from Project") {
    val main        = s"${filePath("ws1")}/api.raml"
    val independent = s"${filePath("ws1")}/independent.raml"
    val subdir      = s"${filePath("ws1")}/sub/type.raml"

    withServer[Assertion] { server =>
      val handler = server.resolveHandler(DocumentSymbolRequestType).value

      for {
        _ <- server.initialize(InitializeParams(None, Some(TraceKind.Off), rootUri = Some(s"${filePath("ws1")}"))) // parse main with subdir
        _ <- platform
          .resolve(main)
          .map(c => openFile(server)(main, c.stream.toString)) // open main file (should not reparse)
        _ <- Future {
          changeFile(server)(main, "#%RAML 1.0", 2)
        } // Erase reference to subdir SHOULD reparse main
        s1 <- handler(DocumentSymbolParams(TextDocumentIdentifier(main)))
        _ <- platform
          .resolve(subdir)
          .map(c => openFile(server)(subdir, c.stream.toString)) // open subdir file (SHOULD reparse subdir)
        s2 <- handler(DocumentSymbolParams(TextDocumentIdentifier(subdir)))
        allTelemetry <- Future.sequence { // TODO: Check for consistency
          notifier.promises.map(p => p.future)
        }
      } yield {
        s2.right.getOrElse(Nil).length should be(1)
        s1.right.getOrElse(Nil).length should be(0)
        notifier.promises.clear()
        assert(allTelemetry.count(d => d.messageType == MessageTypes.BEGIN_PARSE.id) == 3)
      }
    }
  }

  override def buildServer(): LanguageServer =
    new LanguageServerBuilder(factory.documentManager, factory.workspaceManager, platform)
      .addRequestModule(factory.structureManager)
      .build()

  override def rootPath: String = "workspace"

}
