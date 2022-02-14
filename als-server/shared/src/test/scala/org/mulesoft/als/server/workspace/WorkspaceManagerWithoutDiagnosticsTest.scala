package org.mulesoft.als.server.workspace

import amf.aml.client.scala.AMLConfiguration
import amf.core.internal.unsafe.PlatformSecrets
import org.mulesoft.als.server.client.scala.LanguageServerBuilder
import org.mulesoft.als.server.modules.{WorkspaceManagerFactory, WorkspaceManagerFactoryBuilder}
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.protocol.configuration.AlsInitializeParams
import org.mulesoft.als.server.{LanguageServerBaseTest, MockDiagnosticClientNotifier}
import org.mulesoft.lsp.configuration.TraceKind
import org.mulesoft.lsp.feature.common.TextDocumentIdentifier
import org.mulesoft.lsp.feature.documentsymbol.{DocumentSymbolParams, DocumentSymbolRequestType}
import org.scalatest.Assertion

import scala.concurrent.{ExecutionContext, Future}

class WorkspaceManagerWithoutDiagnosticsTest extends LanguageServerBaseTest with PlatformSecrets {

  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  test("on close notification") {
    val diagnosticClientNotifier = new MockDiagnosticClientNotifier

    val factory: WorkspaceManagerFactory =
      new WorkspaceManagerFactoryBuilder(diagnosticClientNotifier, logger).buildWorkspaceManagerFactory()

    val changedFragment =
      """#%RAML 1.0 DataType
        |
        |properties:
        |  a: string
        |  b: string
      """.stripMargin
    val fragmentUri = s"${filePath("ws2/fragment.raml")}"
    val config      = AMLConfiguration.predefined()
    val initialArgs = changeConfigArgs(Some("api.raml"), filePath("ws2"))
    withServer[Assertion](buildServer(factory)) { server =>
      for {
        _ <- server.testInitialize(
          AlsInitializeParams(None, Some(TraceKind.Off), rootUri = Some(s"${filePath("ws2")}")))
        _               <- changeWorkspaceConfiguration(server)(initialArgs)
        apiContent      <- platform.fetchContent(s"${filePath("ws2/api.raml")}", config)
        fragmentContent <- platform.fetchContent(fragmentUri, config)
        _               <- openFile(server)(s"${filePath("ws2/api.raml")}", apiContent.stream.toString)
        _               <- openFile(server)(fragmentUri, fragmentContent.stream.toString)
        _               <- changeFile(server)(fragmentUri, changedFragment, 1)
        r1 <- {
          val handler = server.resolveHandler(DocumentSymbolRequestType).value

          handler(DocumentSymbolParams(TextDocumentIdentifier(fragmentUri)))
            .collect { case Right(symbols) => symbols }
            .map(symbols =>
              symbols.headOption match {
                case Some(o) =>
                  o.name should be("properties")
                  o.children.size should be(2)
                case _ => fail("Missing first symbol")
            })
        }
        _ <- closeFile(server)(fragmentUri)
        r2 <- {
          if (r1 == succeed) {
            val handler = server.resolveHandler(DocumentSymbolRequestType).value

            handler(DocumentSymbolParams(TextDocumentIdentifier(fragmentUri)))
              .collect { case Right(symbols) => symbols }
              .map(symbols =>
                symbols.headOption match {
                  case Some(o) => o.children.size should be(1)
                  case _       => fail("Missing first symbol")
              })
          } else Future.successful(r1)
        }
      } yield {
        r2
      }
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
