package org.mulesoft.als.server.errorhandler

import org.mulesoft.als.common.URIImplicits.StringUriImplicits
import org.mulesoft.als.server.modules.workspace.UnitNotFoundException
import org.mulesoft.als.server.modules.{WorkspaceManagerFactory, WorkspaceManagerFactoryBuilder}
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.protocol.configuration.AlsInitializeParams
import org.mulesoft.als.server.{LanguageServerBaseTest, LanguageServerBuilder, MockTelemetryClientNotifier}
import org.mulesoft.lsp.feature.common.{TextDocumentIdentifier, TextDocumentItem, VersionedTextDocumentIdentifier}
import org.mulesoft.lsp.feature.documentsymbol.{DocumentSymbolParams, SymbolInformation}
import org.mulesoft.lsp.feature.{TelemeteredRequestHandler, documentsymbol}
import org.mulesoft.lsp.textsync.{
  DidChangeTextDocumentParams,
  DidCloseTextDocumentParams,
  DidOpenTextDocumentParams,
  TextDocumentContentChangeEvent
}

import scala.concurrent.{ExecutionContext, Future}

class ErrorHandlingTest extends LanguageServerBaseTest {
  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  override def rootPath: String = "" // apis or golden files should not be necessary

  private val telemetryNotifier = new MockTelemetryClientNotifier(5000, false)
  protected val factory: WorkspaceManagerFactory =
    new WorkspaceManagerFactoryBuilder(telemetryNotifier, logger).buildWorkspaceManagerFactory()

  def buildServer(): LanguageServer =
    new LanguageServerBuilder(factory.documentManager,
                              factory.workspaceManager,
                              factory.configurationManager,
                              factory.resolutionTaskManager).build()

  private def undef(idx: Int): String = s"un:def-$idx"

  test("initialize with no rootUri nor rootPath") {
    val initializeParams: AlsInitializeParams = AlsInitializeParams(None, None)
    withServer(buildServer()) { server =>
      for {
        _ <- server.initialize(initializeParams)
      } yield {
        assert(factory.workspaceManager.getWorkspaceFolders.isEmpty)
        server.shutdown()
        succeed
      }
    }
  }

  test("initialize with wrong rootUri") {
    val undefUri: String                      = undef(0)
    val initializeParams: AlsInitializeParams = AlsInitializeParams(None, None, rootUri = Some(undefUri))
    withServer(buildServer()) { server =>
      for {
        _ <- server.initialize(initializeParams)
      } yield {
        assert(factory.workspaceManager.getWorkspaceFolders.isEmpty)
        logger.logList.exists(_.contains(s"Not recognized $undefUri as a valid Root URI"))
        server.shutdown()
        factory.documentManager.uriToEditor.remove(undefUri.toAmfUri(platform))
        succeed
      }
    }
  }

  test("did open with invalid uri") {
    val undefUri: String = undef(1)
    withServer(buildServer()) { server =>
      val params = DidOpenTextDocumentParams(TextDocumentItem(undefUri, "raml", 0, "test"))
      server.textDocumentSyncConsumer.didOpen(params)
      assert(factory.documentManager.uriToEditor.exists(undefUri.toAmfUri(platform)))
      assert(factory.documentManager.uriToEditor.uris.size == 1)
      assert(logger.logList.exists(_.contains(s"Adding invalid URI file to manager: $undefUri")))
      server.shutdown()
      factory.documentManager.uriToEditor.remove(undefUri.toAmfUri(platform))
      succeed
    }
  }

  test("did change with invalid uri") {
    val undefUri: String = undef(2)
    withServer(buildServer()) { server =>
      val params = DidChangeTextDocumentParams(VersionedTextDocumentIdentifier(undefUri, None),
                                               Seq(TextDocumentContentChangeEvent("other test")))
      server.textDocumentSyncConsumer.didChange(params)
      assert(factory.documentManager.uriToEditor.exists(undefUri.toAmfUri(platform)))
      assert(factory.documentManager.uriToEditor.uris.size == 1)
      assert(logger.logList.exists(_.contains(s"Editing invalid URI file to manager: $undefUri")))
      server.shutdown()
      factory.documentManager.uriToEditor.remove(undefUri.toAmfUri(platform))
      succeed
    }
  }

  test("did close with invalid uri") {
    val undefUri: String = undef(3)
    withServer(buildServer()) { server =>
      val openParams = DidOpenTextDocumentParams(TextDocumentItem(undefUri, "raml", 0, "test"))
      server.textDocumentSyncConsumer.didOpen(openParams)
      assert(factory.documentManager.uriToEditor.exists(undefUri.toAmfUri(platform)))
      val closeParams = DidCloseTextDocumentParams(TextDocumentIdentifier(undefUri))
      server.textDocumentSyncConsumer.didClose(closeParams)
      assert(!factory.documentManager.uriToEditor.exists(undefUri.toAmfUri(platform)))
      assert(factory.documentManager.uriToEditor.uris.isEmpty)
      assert(logger.logList.exists(_.contains(s"Removing invalid URI file to manager: $undefUri")))
      server.shutdown()
      succeed
    }
  }

  test("getLast for invalid uri") {
    val undefUri: String = undef(4)
    withServer(buildServer()) { server =>
      for {
        lu <- try {
          factory.workspaceManager
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
    withServer(buildServer()) { server =>
      for {
        c <- factory.structureManager.getRequestHandlers
          .collectFirst {
            case ch: TelemeteredRequestHandler[DocumentSymbolParams,
                                               Either[Seq[SymbolInformation], Seq[documentsymbol.DocumentSymbol]]] =>
              ch.apply(params)
          }
          .getOrElse(fail("structure request handler not found"))
        _      <- telemetryNotifier.nextCall // begin structure
        tError <- telemetryNotifier.nextCall // error
      } yield {
        c match {
          case Left(value)  => assert(value.isEmpty)
          case Right(value) => assert(value.isEmpty)
        }
        assert(tError.message == s"Unit not found at repository for uri: ${undefUri.toAmfUri(platform)}")
        server.shutdown()
        succeed
      }
    }
  }
}
