package org.mulesoft.als.server.lsp4j

import amf.core.internal.remote.Platform

import java.io._
import java.util
import amf.core.internal.unsafe.PlatformSecrets
import com.google.gson.{Gson, GsonBuilder}
import org.eclipse.lsp4j.ExecuteCommandParams
import org.mulesoft.als.server._
import org.mulesoft.als.server.client.{AlsClientNotifier, ClientConnection, ClientNotifier}
import org.mulesoft.als.logger.{EmptyLogger, Logger}
import org.mulesoft.als.server.lsp4j.extension.AlsInitializeParams
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.modules.telemetry.TelemetryManager
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.textsync.EnvironmentProvider
import org.mulesoft.als.server.workspace.WorkspaceManager
import org.mulesoft.als.server.workspace.command.{CommandExecutor, Commands, DidChangeConfigurationCommandExecutor}
import org.mulesoft.amfintegration.amfconfiguration.AmfConfigurationWrapper
import org.mulesoft.lsp.feature.diagnostic.PublishDiagnosticsParams
import org.mulesoft.lsp.feature.telemetry.TelemetryMessage
import org.mulesoft.lsp.textsync.DidChangeConfigurationNotificationParams
import org.mulesoft.lsp.workspace.{ExecuteCommandParams => SharedExecuteParams}

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

    val notifier: AlsClientNotifier[StringWriter] = new MockAlsClientNotifier
    val server = new LanguageServerImpl(
      new LanguageServerFactory(clientConnection)
        .withSerializationProps(JvmSerializationProps(notifier))
        .withLogger(logger)
        .build())

    server.initialize(new AlsInitializeParams()).toScala.map(_ => succeed)
  }

  test("Lsp4j LanguageServerImpl with null params: initialize should not fail") {
    val myString = "#%RAML 1.0\ntitle:test"
    val in       = new ByteArrayInputStream(myString.getBytes())
    val baos     = new ByteArrayOutputStream()
    val out      = new ObjectOutputStream(baos)

    val logger: Logger                            = EmptyLogger
    val clientConnection                          = ClientConnection(logger)
    val notifier: AlsClientNotifier[StringWriter] = new MockAlsClientNotifier
    val server = new LanguageServerImpl(
      new LanguageServerFactory(clientConnection)
        .withSerializationProps(JvmSerializationProps(notifier))
        .withLogger(logger)
        .build())

    server.initialize(null).toScala.map(_ => succeed)
  }

  // todo: enable test after APIMF-3305 is adopted
  ignore("Lsp4j LanguageServerImpl Command - Index Dialect") {
    def wrapJson(file: String, content: String, gson: Gson): String =
      s"""{"uri": "$file", "content": ${gson.toJson(content)}}"""

    def executeCommandIndexDialect(server: LanguageServerImpl)(file: String, content: String): Future[Unit] = {
      val args: java.util.List[AnyRef] = new util.ArrayList[AnyRef]()
      args.add(wrapJson(file, content, new GsonBuilder().create()))
      server.getWorkspaceService
        .executeCommand(new ExecuteCommandParams(Commands.INDEX_DIALECT, args))
        .toScala
        .map(_ => {
          Unit
        })

    }
    val wM = buildWorkspaceManager
    withServer(buildServer(wM)) { s =>
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
        assert(wM.getConfig.definitionsFor("Test0.1").isDefined)
      }
    }
  }

  test("Lsp4j LanguageServerImpl Command - Change configuration Params Serialization") {

    var parsedOK = false

    class TestDidChangeConfigurationCommandExecutor(wsc: WorkspaceManager)
        extends DidChangeConfigurationCommandExecutor(EmptyLogger, wsc) {
      override protected def runCommand(param: DidChangeConfigurationNotificationParams): Future[Unit] = {
        parsedOK = true // If it reaches this command, it was parsed correctly
        Future.unit
      }
    }

    def wrapJson(mainUri: String, dependecies: Array[String], gson: Gson): String =
      s"""{"mainUri": "$mainUri", "dependencies": ${gson.toJson(dependecies)}}"""

    class DummyTelemetryProvider extends TelemetryManager(new DummyClientNotifier(), EmptyLogger)

    class DummyClientNotifier extends ClientNotifier {
      override def notifyDiagnostic(params: PublishDiagnosticsParams): Unit = {}

      override def notifyTelemetry(params: TelemetryMessage): Unit = {}
    }

    val p = platform
    class TestWorkspaceManager
        extends WorkspaceManager(
          new EnvironmentProvider {

            override def branch: EnvironmentProvider = ???

            override val amfConfiguration: AmfConfigurationWrapper = AmfConfigurationWrapper()

            override def openedFiles: Seq[String] = Seq.empty

            override def amfConfigurationSnapshot(): AmfConfigurationWrapper = amfConfiguration

            override def initialize(): Future[Unit] = Future.unit

            override def platform: Platform = p
          },
          new DummyTelemetryProvider(),
          Nil,
          Nil,
          EmptyLogger
        ) {

      private val commandExecutors: Map[String, CommandExecutor[_, _]] = Map(
        Commands.DID_CHANGE_CONFIGURATION -> new TestDidChangeConfigurationCommandExecutor(this)
      )

      override def executeCommand(params: SharedExecuteParams): Future[AnyRef] = Future {
        commandExecutors.get(params.command) match {
          case Some(exe) => exe.runCommand(params)
          case _ =>
            logger.error(s"Command [${params.command}] not recognized", "WorkspaceManager", "executeCommand")
        }
        Unit
      }
    }
    val args = List(wrapJson("file://uri.raml", Array("dep1", "dep2"), new GsonBuilder().create()))

    val ws = new TestWorkspaceManager()
    ws.executeCommand(SharedExecuteParams(Commands.DID_CHANGE_CONFIGURATION, args))
      .map(_ => assert(parsedOK))

  }

  def buildServer(): LanguageServer = buildServer(buildWorkspaceManager)

  def buildWorkspaceManager = new WorkspaceManagerFactoryBuilder(new MockDiagnosticClientNotifier, logger)

  def buildServer(builder: WorkspaceManagerFactoryBuilder): LanguageServer = {

    val dm       = builder.buildDiagnosticManagers()
    val managers = builder.buildWorkspaceManagerFactory()

    val b =
      new LanguageServerBuilder(managers.documentManager,
                                managers.workspaceManager,
                                managers.configurationManager,
                                managers.resolutionTaskManager)
    dm.foreach(b.addInitializableModule)
    b.build()
  }

  override def rootPath: String = ""
}
