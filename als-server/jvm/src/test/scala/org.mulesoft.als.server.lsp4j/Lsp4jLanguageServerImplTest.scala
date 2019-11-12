package org.mulesoft.als.server.lsp4j

import java.io._
import java.util

import amf.core.unsafe.PlatformSecrets
import amf.plugins.document.vocabularies.AMLPlugin
import com.google.gson.{Gson, GsonBuilder}
import org.eclipse.lsp4j.{ExecuteCommandParams, InitializeParams}
import org.mulesoft.als.server.client.ClientConnection
import org.mulesoft.als.server.logger.{EmptyLogger, Logger}
import org.mulesoft.als.server.modules.ManagersFactory
import org.mulesoft.als.server.workspace.command.Commands
import org.mulesoft.als.server.{LanguageServerBaseTest, LanguageServerBuilder}
import org.mulesoft.lsp.feature.diagnostic.PublishDiagnosticsParams
import org.mulesoft.lsp.server.LanguageServer
import org.mulesoft.lsp.textsync.{DidFocusParams, IndexDialectParams}

import scala.compat.java8.FutureConverters._
import scala.concurrent.Future

class Lsp4jLanguageServerImplTest extends LanguageServerBaseTest with PlatformSecrets {

  test("Lsp4j LanguageServerImpl: initialize correctly") {

    val myString = "#%RAML 1.0\ntitle:test"
    val in       = new ByteArrayInputStream(myString.getBytes())
    val baos     = new ByteArrayOutputStream()
    val out      = new ObjectOutputStream(baos)

    val logger: Logger   = EmptyLogger
    val clientConnection = ClientConnection(logger)

    val server = new LanguageServerImpl(
      LanguageServerFactory.alsLanguageServer(clientConnection, logger, withDiagnostics = false))

    server.initialize(new InitializeParams()).toScala.map(_ => succeed)
  }

  test("Lsp4j LanguageServerImpl with null params: initialize should not fail") {
    val myString = "#%RAML 1.0\ntitle:test"
    val in       = new ByteArrayInputStream(myString.getBytes())
    val baos     = new ByteArrayOutputStream()
    val out      = new ObjectOutputStream(baos)

    val logger: Logger   = EmptyLogger
    val clientConnection = ClientConnection(logger)

    val server = new LanguageServerImpl(
      LanguageServerFactory.alsLanguageServer(clientConnection, logger, withDiagnostics = false))

    server.initialize(null).toScala.map(_ => succeed)
  }

  test("Lsp4j LanguageServerImpl Command - Index Dialect") {
    def wrapJson(file: String, content: String, gson: Gson): String =
      s"""{"uri": "${file}", "content": ${gson.toJson(content)}}"""

    def executeCommandIndexDialect(server: LanguageServerImpl)(file: String, content: String): Future[Unit] = {
      val args: java.util.List[AnyRef] = new util.ArrayList[AnyRef]()
      args.add(wrapJson(file, content, new GsonBuilder().create()))
      server.getWorkspaceService
        .executeCommand(new ExecuteCommandParams(Commands.INDEX_DIALECT, args))
        .toScala
        .map(_ => {
          Thread.sleep(1000)
          Unit
        })

    }

    withServer { s =>
      val server       = new LanguageServerImpl(s)
      val mainFilePath = s"file://api.raml"

      val mainContent =
        """#%Dialect 1.0
          |
          |dialect: Test
          |version: 0.1
          |
          |external:
          |  runtime: http://mulesoft.com/vocabularies/runtime#
          |  schema-org: http://schema.org/
          |
          |documents:
          |  root:
          |    encodes: DeploymentNode
          |
          |
          |nodeMappings:
          |
          |  DeploymentNode:
          |    classTerm: runtime.Deployment
          |    mapping:
          |      connections:
          |        propertyTerm: runtime.connections
          |        range: link
          |        allowMultiple: true
          |        mandatory: true
        """.stripMargin

      /*
        open lib -> open main -> focus lib -> fix lib -> focus main
       */
      for {
        _ <- executeCommandIndexDialect(server)(mainFilePath, mainContent)
      } yield {
        server.shutdown()
        assert(AMLPlugin.registry.findDialectForHeader("%Test0.1").isDefined)

      }
    }
  }

  override def buildServer(): LanguageServer = {

    val managers = ManagersFactory(MockDiagnosticClientNotifier, platform, logger, withDiagnostics = false)

    new LanguageServerBuilder(managers.documentManager, managers.workspaceManager, platform)
      .addInitializableModule(managers.diagnosticManager)
      .build()
  }

  override def rootPath: String = ""
}
