package org.mulesoft.als.server.lsp4j

import java.io._
import java.util

import amf.core.remote.Platform
import amf.core.unsafe.PlatformSecrets
import amf.internal.environment.Environment
import amf.plugins.document.vocabularies.AMLPlugin
import org.eclipse.lsp4j.{ExecuteCommandParams, InitializeParams}
import org.mulesoft.als.common.DirectoryResolver
import org.mulesoft.als.server.client.ClientConnection
import org.mulesoft.als.server.logger.{EmptyLogger, Logger}
import org.mulesoft.als.server.modules.ast.AstManager
import org.mulesoft.als.server.modules.diagnostic.DiagnosticManager
import org.mulesoft.als.server.modules.telemetry.TelemetryManager
import org.mulesoft.als.server.textsync.TextDocumentManager
import org.mulesoft.als.server.{LanguageServerBaseTest, LanguageServerBuilder}
import org.mulesoft.lsp.feature.diagnostic.PublishDiagnosticsParams
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

    val server = new LanguageServerImpl(LanguageServerFactory.alsLanguageServer(clientConnection, logger))

    server.initialize(new InitializeParams()).toScala.map(_ => succeed)
  }

  test("Lsp4j LanguageServerImpl with null params: initialize should not fail") {
    val myString = "#%RAML 1.0\ntitle:test"
    val in       = new ByteArrayInputStream(myString.getBytes())
    val baos     = new ByteArrayOutputStream()
    val out      = new ObjectOutputStream(baos)

    val logger: Logger   = EmptyLogger
    val clientConnection = ClientConnection(logger)

    val server = new LanguageServerImpl(LanguageServerFactory.alsLanguageServer(clientConnection, logger))

    server.initialize(null).toScala.map(_ => succeed)
  }

  test("Lsp4j LanguageServerImpl Command - Did Focus: Command should be notify DidFocus") {
    def executeCommandFocus(server: LanguageServerImpl)(file: String, version: Int): Future[PublishDiagnosticsParams] = {
      val args: java.util.List[AnyRef] = new util.ArrayList[AnyRef]()
      args.add(DidFocusParams(file, version))
      server.getWorkspaceService.executeCommand(new ExecuteCommandParams("didFocusChange", args))
      MockDiagnosticClientNotifier.nextCall
    }

    withServer { s =>
      val server       = new LanguageServerImpl(s)
      val mainFilePath = s"file://api.raml"
      val libFilePath  = s"file://lib1.raml"

      val mainContent =
        """#%RAML 1.0
          |
          |title: test API
          |uses:
          |  lib1: lib1.raml
          |
          |/resource:
          |  post:
          |    responses:
          |      200:
          |        body:
          |          application/json:
          |            type: lib1.TestType
          |            example:
          |              {"a":"1"}
        """.stripMargin

      val libFileContent =
        """#%RAML 1.0 Library
          |
          |types:
          |  TestType:
          |    properties:
          |      b: string
        """.stripMargin

      /*
        open lib -> open main -> focus lib -> fix lib -> focus main
       */
      for {
        a <- openFileNotification(s)(libFilePath, libFileContent)
        b <- openFileNotification(s)(mainFilePath, mainContent)
        c <- executeCommandFocus(server)(libFilePath, 0)
        d <- changeNotification(s)(libFilePath, libFileContent.replace("b: string", "a: string"), 1)
        e <- executeCommandFocus(server)(mainFilePath, 0)
      } yield {
        server.shutdown()
        assert(
          a.diagnostics.isEmpty && a.uri == libFilePath &&
            b.diagnostics.length == 1 && b.uri == mainFilePath && // todo: search coinciding message between JS and JVM
            c.diagnostics.isEmpty && c.uri == libFilePath &&
            d.diagnostics.isEmpty && d.uri == libFilePath &&
            e.diagnostics.isEmpty && e.uri == mainFilePath)
      }
    }
  }

  test("Lsp4j LanguageServerImpl Command - Index Dialect") {
    def executeCommandIndexDialect(server: LanguageServerImpl)(file: String, content: String): Future[Unit] = {
      val args: java.util.List[AnyRef] = new util.ArrayList[AnyRef]()
      args.add(IndexDialectParams(file, Some(content)))
      server.getWorkspaceService
        .executeCommand(new ExecuteCommandParams(Commands.INDEX_DIALECT, args))
        .toScala
        .map(_ => {
          Thread.sleep(100)
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

  override def addModules(documentManager: TextDocumentManager,
                          platform: Platform,
                          directoryResolver: DirectoryResolver,
                          baseEnvironment: Environment,
                          builder: LanguageServerBuilder): LanguageServerBuilder = {

    val telemetryManager = new TelemetryManager(MockDiagnosticClientNotifier, logger)
    val astManager       = new AstManager(documentManager, baseEnvironment, telemetryManager, platform, logger)
    val diagnosticManager =
      new DiagnosticManager(documentManager,
                            astManager,
                            telemetryManager,
                            MockDiagnosticClientNotifier,
                            platform,
                            logger)

    builder
      .addInitializable(astManager)
      .addInitializableModule(diagnosticManager)
  }

  override def rootPath: String = ""
}
