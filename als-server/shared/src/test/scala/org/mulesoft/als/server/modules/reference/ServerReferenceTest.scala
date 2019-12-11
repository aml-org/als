package org.mulesoft.als.server.modules.reference

import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.als.server.modules.ManagersFactory
import org.mulesoft.als.server.{LanguageServerBaseTest, LanguageServerBuilder}
import org.mulesoft.lsp.common.TextDocumentIdentifier
import org.mulesoft.lsp.configuration.{InitializeParams, TraceKind}
import org.mulesoft.lsp.convert.LspRangeConverter
import org.mulesoft.lsp.feature.reference.{ReferenceContext, ReferenceParams, ReferenceRequestType}
import org.mulesoft.lsp.server.{DefaultServerSystemConf, LanguageServer}
import org.scalatest.Assertion

import scala.concurrent.ExecutionContext

class ServerReferenceTest extends LanguageServerBaseTest {

  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  override def buildServer(): LanguageServer = {

    val factory = ManagersFactory(MockDiagnosticClientNotifier, logger, withDiagnostics = false)

    new LanguageServerBuilder(factory.documentManager, factory.workspaceManager, DefaultServerSystemConf)
      .addRequestModule(factory.referenceManager)
      .build()
  }

  test("Find references test 001") {
    withServer[Assertion] { server =>
      val uri = s"${filePath("ws2")}"
      for {
        _ <- server.initialize(InitializeParams(None, Some(TraceKind.Off), rootUri = Some(uri)))
        references <- {
          val handler = server.resolveHandler(ReferenceRequestType).value
          handler(
            ReferenceParams(TextDocumentIdentifier(s"$uri/fragment.raml"),
                            org.mulesoft.lsp.common.Position(1, 1),
                            ReferenceContext(false)))
        }
      } yield {
        references.size should be(1)
        references.head.uri should be(s"$uri/api.raml")
        references.head.range should be(LspRangeConverter.toLspRange(PositionRange(Position(4, 5), Position(4, 27))))
      }
    }
  }

  override def rootPath: String = "workspace"
}
