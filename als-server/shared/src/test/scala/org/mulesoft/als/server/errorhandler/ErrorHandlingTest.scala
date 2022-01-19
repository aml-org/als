package org.mulesoft.als.server.errorhandler

import org.mulesoft.als.common.URIImplicits.StringUriImplicits
import org.mulesoft.als.server.client.scala.LanguageServerBuilder
import org.mulesoft.als.server.modules.workspace.UnitNotFoundException
import org.mulesoft.als.server.modules.{WorkspaceManagerFactory, WorkspaceManagerFactoryBuilder}
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.protocol.configuration.AlsInitializeParams
import org.mulesoft.als.server.{LanguageServerBaseTest, MockTelemetryClientNotifier}
import org.mulesoft.lsp.feature.common.{TextDocumentIdentifier, TextDocumentItem, VersionedTextDocumentIdentifier}
import org.mulesoft.lsp.feature.documentsymbol.{DocumentSymbolParams, SymbolInformation}
import org.mulesoft.lsp.feature.{TelemeteredRequestHandler, documentsymbol}
import org.mulesoft.lsp.textsync.{DidChangeTextDocumentParams, DidCloseTextDocumentParams, DidOpenTextDocumentParams, TextDocumentContentChangeEvent}

import scala.concurrent.{ExecutionContext, Future}

class ErrorHandlingTest extends LanguageServerBaseTest {
  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  override def rootPath: String = "" // apis or golden files should not be necessary

  private val telemetryNotifier = new MockTelemetryClientNotifier(5000, false)

  def buildServer(): (LanguageServer, WorkspaceManagerFactory) = {
    val factory: WorkspaceManagerFactory =
      new WorkspaceManagerFactoryBuilder(telemetryNotifier, logger).buildWorkspaceManagerFactory()
    (new LanguageServerBuilder(factory.documentManager,
                               factory.workspaceManager,
                               factory.configurationManager,
                               factory.resolutionTaskManager).build(),
     factory)
  }

  private def undef(idx: Int): String = s"un:def-$idx"

  test("initialize with no rootUri nor rootPath") {
    val initializeParams: AlsInitializeParams = AlsInitializeParams(None, None)
    val servAndFactor                         = buildServer()
    withServer(servAndFactor._1) { server =>
      for {
        _ <- server.initialize(initializeParams)
      } yield {
        assert(servAndFactor._2.workspaceManager.getWorkspaceFolders.isEmpty)
        server.shutdown()
        succeed
      }
    }
  }

  test("initialize with wrong rootUri") {
    val undefUri: String                      = undef(0)
    val initializeParams: AlsInitializeParams = AlsInitializeParams(None, None, rootUri = Some(undefUri))
    val servAndFactor                         = buildServer()
    withServer(servAndFactor._1) { server =>
      for {
        _ <- server.initialize(initializeParams)
      } yield {
        assert(servAndFactor._2.workspaceManager.getWorkspaceFolders.isEmpty)
        logger.logList.exists(_.contains(s"Not recognized $undefUri as a valid Root URI"))
        server.shutdown()
        servAndFactor._2.documentManager.uriToEditor.remove(undefUri.toAmfUri(platform))
        succeed
      }
    }
  }

  test("did open with invalid uri") {
    val undefUri: String = undef(1)
    val servAndFactor    = buildServer()
    withServer(servAndFactor._1) { server =>
      val params = DidOpenTextDocumentParams(TextDocumentItem(undefUri, "raml", 0, "test"))
      server.textDocumentSyncConsumer.didOpen(params)
      assert(servAndFactor._2.documentManager.uriToEditor.exists(undefUri.toAmfUri(platform)))
      assert(servAndFactor._2.documentManager.uriToEditor.uris.size == 1)
      assert(logger.logList.exists(_.contains(s"Adding invalid URI file to manager: $undefUri")))
      server.shutdown()
      servAndFactor._2.documentManager.uriToEditor.remove(undefUri.toAmfUri(platform))
      succeed
    }
  }

  test("did change with invalid uri") {
    val undefUri: String = undef(2)
    val servAndFactor    = buildServer()
    withServer(servAndFactor._1) { server =>
      val params = DidChangeTextDocumentParams(VersionedTextDocumentIdentifier(undefUri, None),
                                               Seq(TextDocumentContentChangeEvent("other test")))
      server.textDocumentSyncConsumer.didChange(params)
      assert(servAndFactor._2.documentManager.uriToEditor.exists(undefUri.toAmfUri(platform)))
      assert(servAndFactor._2.documentManager.uriToEditor.uris.size == 1)
      assert(logger.logList.exists(_.contains(s"Editing invalid URI file to manager: $undefUri")))
      server.shutdown()
      servAndFactor._2.documentManager.uriToEditor.remove(undefUri.toAmfUri(platform))
      succeed
    }
  }

  test("did close with invalid uri") {
    val undefUri: String = undef(3)
    val servAndFactor    = buildServer()
    withServer(servAndFactor._1) { server =>
      val openParams = DidOpenTextDocumentParams(TextDocumentItem(undefUri, "raml", 0, "test"))
      server.textDocumentSyncConsumer.didOpen(openParams)
      assert(servAndFactor._2.documentManager.uriToEditor.exists(undefUri.toAmfUri(platform)))
      val closeParams = DidCloseTextDocumentParams(TextDocumentIdentifier(undefUri))
      server.textDocumentSyncConsumer.didClose(closeParams)
      assert(!servAndFactor._2.documentManager.uriToEditor.exists(undefUri.toAmfUri(platform)))
      assert(servAndFactor._2.documentManager.uriToEditor.uris.isEmpty)
      assert(logger.logList.exists(_.contains(s"Removing invalid URI file to manager: $undefUri")))
      server.shutdown()
      succeed
    }
  }

  test("getLast for invalid uri") {
    val undefUri: String = undef(4)
    val servAndFactor    = buildServer()
    withServer(servAndFactor._1) { server =>
      for {
        lu <- try {
          servAndFactor._2.workspaceManager
            .getLastUnit(undefUri, "")
            .map(cu => {
              println(cu.uri)
              fail("should have thrown UnitNotFoundException")
            })
            .recoverWith {
              case _: UnitNotFoundException =>
                Future.successful(assert(
                  logger.logList.exists(_.contains(s"UnitNotFoundException for: ${undefUri.toAmfUri(platform)}"))))
            }
        } catch {
          case _: UnitNotFoundException =>
            Future.successful(
              assert(logger.logList.exists(_.contains(s"UnitNotFoundException for: ${undefUri.toAmfUri(platform)}"))))
        }
      } yield {
        server.shutdown()
        lu
      }
    }
  }

  test("request structure with invalid uri") {
    val undefUri: String             = undef(5)
    val params: DocumentSymbolParams = DocumentSymbolParams(TextDocumentIdentifier(undefUri))
    telemetryNotifier.promises.clear()
    val servAndFactor = buildServer()
    withServer(servAndFactor._1) { server =>
      for {
        c <- servAndFactor._2.structureManager.getRequestHandlers
          .collectFirst {
            case ch: TelemeteredRequestHandler[DocumentSymbolParams,
                                               Either[Seq[SymbolInformation], Seq[documentsymbol.DocumentSymbol]]] =>
              ch.apply(params)
          }
          .getOrElse(fail("structure request handler not found"))
        calls      <-
          Future.sequence(Seq( // begin structure/parse, error, end parse/structure
            telemetryNotifier.nextCall,
            telemetryNotifier.nextCall,
            telemetryNotifier.nextCall,
          ))
      } yield {
        c match {
          case Left(value) => assert(value.isEmpty)
          case Right(value) =>
            assert(value.isEmpty)
            calls.exists(_.message == s"Unit not found at repository for uri: ${undefUri.toAmfUri(platform)}")
            server.shutdown()
            succeed
        }
      }
    }
  }
}
