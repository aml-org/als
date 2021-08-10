package org.mulesoft.als.server.workspace

import org.mulesoft.als.server._
import org.mulesoft.als.server.client.ClientNotifier
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.protocol.configuration.AlsInitializeParams
import org.mulesoft.lsp.configuration.TraceKind
import org.mulesoft.lsp.feature.common.{TextDocumentIdentifier, TextDocumentItem}
import org.mulesoft.lsp.feature.documentsymbol.{DocumentSymbolParams, DocumentSymbolRequestType}
import org.mulesoft.lsp.feature.telemetry.MessageTypes.MessageTypes
import org.mulesoft.lsp.feature.telemetry.{MessageTypes, TelemetryMessage}
import org.mulesoft.lsp.textsync.DidOpenTextDocumentParams
import org.scalatest.Assertion

import scala.concurrent.{ExecutionContext, Future}

class WorkspaceManagerTelemetryTest extends LanguageServerBaseTest {

  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  test("Workspace Manager check parsing times (project should have 1, independent file 1)") {
    val main                                  = s"${filePath("ws1")}/api.raml"
    val independent                           = s"${filePath("ws1")}/independent.raml"
    val subdir                                = s"${filePath("ws1")}/sub/type.raml"
    val notifier: MockTelemetryClientNotifier = new MockTelemetryClientNotifier()
    withServer[Assertion](buildServer(notifier)) { server =>
      val handler = server.resolveHandler(DocumentSymbolRequestType).value

      for {
        _ <- server.initialize(AlsInitializeParams(None, Some(TraceKind.Off), rootUri = Some(s"${filePath("ws1")}")))
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
        allTelemetry <- Future.sequence {
          notifier.promises.map(p => p.future)
        }
      } yield {
        notifier.promises.clear()
        assert(allTelemetry.count(d => d.messageType == MessageTypes.BEGIN_PARSE) == 2)
      }
    }
  }

  private def waitFor(notifier: MockTelemetryClientNotifier, message: String): Future[Unit] =
    notifier.nextCall.flatMap {
      case t if t.messageType == message =>
        Future.unit
      case _ => waitFor(notifier, message)
    }

  test("Workspace Manager check parsing times when reference removed from Project") {
    val main     = s"${filePath("ws1")}/api.raml"
    val subdir   = s"${filePath("ws1")}/sub/type.raml"
    val notifier = new MockTelemetryClientNotifier(3000)
    withServer[Assertion](buildServer(notifier)) { server =>
      val handler = server.resolveHandler(DocumentSymbolRequestType).value
      for {
        _ <- server.initialize(AlsInitializeParams(None, Some(TraceKind.Off), rootUri = Some(s"${filePath("ws1")}"))) // parse main with subdir
        _ <- platform
          .resolve(main)
          .map(c => openFile(server)(main, c.stream.toString)) // open main file (should not reparse)
          .flatMap(_ => waitFor(notifier, MessageTypes.BEGIN_PARSE))
        _ <- {
          changeFile(server)(main, "#%RAML 1.0", 2)
          waitFor(notifier, MessageTypes.BEGIN_PARSE)
        } // Erase reference to subdir SHOULD reparse main
        s1 <- handler(DocumentSymbolParams(TextDocumentIdentifier(main)))
        _ <- platform
          .resolve(subdir)
          .map(c => openFile(server)(subdir, c.stream.toString)) // open subdir file (SHOULD reparse subdir)
          .flatMap(_ => waitFor(notifier, MessageTypes.BEGIN_PARSE))
        s2 <- handler(DocumentSymbolParams(TextDocumentIdentifier(subdir)))
      } yield {
        notifier.promises.clear()
        s2.right.getOrElse(Nil).length should be(1)
        s1.right.getOrElse(Nil).length should be(0)
      }
    }
  }

  test("Workspace Manager check parsing times (parse instance after modifying dialect)") {
    val dialect  = s"${filePath("aml-workspace")}/dialect.yaml"
    val instance = s"${filePath("aml-workspace")}/instance.yaml"

    val notifier: MockCompleteClientNotifier = new MockCompleteClientNotifier(3000)
    withServer[Assertion](buildServer(notifier)) { server =>
      // open dialect -> open invalid instance -> change dialect -> focus now valid instance
      for {
        _ <- server.initialize(
          AlsInitializeParams(None, Some(TraceKind.Off), rootUri = Some(filePath("aml-workspace"))))
        dialectContent      <- this.platform.resolve(dialect).map(_.stream.toString)
        _                   <- openFileNotification(server)(dialect, dialectContent)
        dialectDiagnostic1  <- notifier.nextCallD
        instanceContent     <- this.platform.resolve(instance).map(_.stream.toString)
        _                   <- openFileNotification(server)(instance, instanceContent)
        instanceDiagnostic1 <- notifier.nextCallD
        _                   <- focusNotification(server)(dialect, 0)
        _                   <- changeNotification(server)(dialect, dialectContent.replace("range: number", "range: string"), 1)
        dialectDiagnostic2  <- notifier.nextCallD
        _                   <- focusNotification(server)(instance, 0)
        instanceDiagnostic2 <- notifier.nextCallD
        allTelemetry <- Future.sequence {
          notifier.promisesT.map(p => p.future)
        }
      } yield {
        dialectDiagnostic1.uri should be(dialect)
        dialectDiagnostic2.uri should be(dialect)
        dialectDiagnostic1.diagnostics.size should be
        dialectDiagnostic2.diagnostics.size should be(0)

        instanceDiagnostic1.uri should be(instance)
        instanceDiagnostic1.diagnostics.size should be(1)

        instanceDiagnostic2.uri should be(instance)
        instanceDiagnostic2.diagnostics.size should be(0)
        val filtered = allTelemetry.filter(_.messageType == MessageTypes.BEGIN_PARSE)
        assert(filtered.length == 4)
      }
    }
  }

  test("Workspace Manager check parsing times (will parse instance even if no change has been done)") {
    val dialect  = s"${filePath("aml-instance-is-mf")}/dialect.yaml"
    val instance = s"${filePath("aml-instance-is-mf")}/instance.yaml"

    val notifier: MockCompleteClientNotifier = new MockCompleteClientNotifier(3000)
    withServer[Assertion](buildServer(notifier)) { server =>
      // open workspace (parse instance) -> open dialect (parse) -> focus dialect (parse) -> focus instance (parse)
      for {
        _ <- server.initialize(
          AlsInitializeParams(None, Some(TraceKind.Off), rootUri = Some(filePath("aml-instance-is-mf"))))
        rootDiagnostic      <- notifier.nextCallD
        dialectContent      <- this.platform.resolve(dialect).map(_.stream.toString)
        _                   <- openFileNotification(server)(dialect, dialectContent)
        dialectDiagnostic1  <- notifier.nextCallD
        instanceContent     <- this.platform.resolve(instance).map(_.stream.toString)
        _                   <- openFileNotification(server)(instance, instanceContent)
        _                   <- focusNotification(server)(dialect, 0)
        dialectDiagnostic2  <- notifier.nextCallD
        _                   <- focusNotification(server)(instance, 0)
        instanceDiagnostic2 <- notifier.nextCallD
        allTelemetry <- Future.sequence {
          notifier.promisesT.map(p => p.future)
        }
      } yield {
        dialectDiagnostic1.uri should be(dialect)
        dialectDiagnostic2.uri should be(dialect)
        dialectDiagnostic1.diagnostics.size should be
        dialectDiagnostic2.diagnostics.size should be(0)

        rootDiagnostic.uri should be(instance)
        instanceDiagnostic2.uri should be(instance)
        rootDiagnostic.diagnostics.size should be(0)
        instanceDiagnostic2.diagnostics.size should be(0)

        assert(allTelemetry.count(d => d.messageType == MessageTypes.BEGIN_PARSE) == 4)
      }
    }
  }

  def buildServer(notifier: ClientNotifier): LanguageServer = {
    val builder = new WorkspaceManagerFactoryBuilder(notifier, logger)
    val dm      = builder.diagnosticManager()
    val factory = builder.buildWorkspaceManagerFactory()
    val b = new LanguageServerBuilder(factory.documentManager,
                                      factory.workspaceManager,
                                      factory.configurationManager,
                                      factory.resolutionTaskManager)
      .addRequestModule(factory.structureManager)
    dm.foreach(b.addInitializableModule)
    b.build()
  }

  override def rootPath: String = "workspace"

}
