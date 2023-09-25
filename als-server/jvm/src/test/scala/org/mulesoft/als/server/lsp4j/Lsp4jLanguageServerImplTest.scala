package org.mulesoft.als.server.lsp4j

import amf.core.client.common.remote.Content
import amf.core.client.platform.resource.ResourceNotFound
import amf.core.client.scala.resource.ResourceLoader
import com.google.gson.{Gson, GsonBuilder}
import org.eclipse.lsp4j.ExecuteCommandParams
import org.mulesoft.als.logger.{EmptyLogger, Logger}
import org.mulesoft.als.server._
import org.mulesoft.als.server.client.platform.{
  AlsClientNotifier,
  AlsLanguageServerFactory,
  ClientConnection,
  ClientNotifier
}
import org.mulesoft.als.server.client.scala.LanguageServerBuilder
import org.mulesoft.als.server.lsp4j.extension.AlsInitializeParams
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.modules.telemetry.TelemetryManager
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.textsync.{EnvironmentProvider, TextDocument}
import org.mulesoft.als.server.workspace.command.{CommandExecutor, Commands, DidChangeConfigurationCommandExecutor}
import org.mulesoft.als.server.workspace.{
  ChangesWorkspaceConfiguration,
  IgnoreProjectConfigurationAdapter,
  WorkspaceManager
}
import org.mulesoft.amfintegration.amfconfiguration.EditorConfiguration
import org.mulesoft.lsp.feature.diagnostic.PublishDiagnosticsParams
import org.mulesoft.lsp.feature.telemetry.TelemetryMessage
import org.mulesoft.lsp.textsync.{DidChangeConfigurationNotificationParams, KnownDependencyScopes}
import org.mulesoft.lsp.workspace.{ExecuteCommandParams => SharedExecuteParams}

import java.io._
import java.util
import scala.collection.JavaConverters._
import scala.compat.java8.FutureConverters._
import scala.concurrent.Future

class Lsp4jLanguageServerImplTest extends AMFValidatorTest with ChangesWorkspaceConfiguration {

  test("Lsp4j LanguageServerImpl: initialize correctly") {

    val myString = "#%RAML 1.0\ntitle:test"
    val in       = new ByteArrayInputStream(myString.getBytes())
    val baos     = new ByteArrayOutputStream()
    val out      = new ObjectOutputStream(baos)

    val logger: Logger   = EmptyLogger
    val clientConnection = ClientConnection()

    val notifier: AlsClientNotifier[StringWriter] = new MockAlsClientNotifier
    val server = new LanguageServerImpl(
      new AlsLanguageServerFactory(clientConnection)
        .withSerializationProps(JvmSerializationProps(notifier))
        .withLogger(logger)
        .build()
    )

    server.initialize(new AlsInitializeParams()).toScala.map(_ => succeed)
  }

  test("Lsp4j LanguageServerImpl with null params: initialize should not fail") {
    val myString = "#%RAML 1.0\ntitle:test"
    val in       = new ByteArrayInputStream(myString.getBytes())
    val baos     = new ByteArrayOutputStream()
    val out      = new ObjectOutputStream(baos)

    val logger: Logger                            = EmptyLogger
    val clientConnection                          = ClientConnection()
    val notifier: AlsClientNotifier[StringWriter] = new MockAlsClientNotifier
    val server = new LanguageServerImpl(
      new AlsLanguageServerFactory(clientConnection)
        .withSerializationProps(JvmSerializationProps(notifier))
        .withLogger(logger)
        .build()
    )

    server.initialize(null).toScala.map(_ => succeed)
  }

  test("Lsp4j LanguageServerImpl Command - Index Dialect") {
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
    val wM           = buildWorkspaceManager
    val (server, wm) = buildServer(wM)
    withServer(server) { s =>
      val server      = new LanguageServerImpl(s)
      val dialectPath = s"file://api.raml"

      val dialectContent =
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

      for {
        _      <- executeCommandIndexDialect(server)(dialectPath, dialectContent)
        config <- wm.getWorkspace(dialectPath).flatMap(_.getConfigurationState)
      } yield {
        server.shutdown()
        assert(config.dialects.map(_.id).contains("file://api.raml"))
      }
    }
  }

  class TestDidChangeConfigurationCommandExecutor(
      wsc: WorkspaceManager,
      fn: DidChangeConfigurationNotificationParams => Unit
  ) extends DidChangeConfigurationCommandExecutor(wsc) {
    override protected def runCommand(param: DidChangeConfigurationNotificationParams): Future[Unit] = {
      fn(param)
      Future.unit
    }
  }

  class DummyTelemetryProvider extends TelemetryManager(new DummyClientNotifier())

  class DummyClientNotifier extends ClientNotifier {
    override def notifyDiagnostic(params: PublishDiagnosticsParams): Unit = {}

    override def notifyTelemetry(params: TelemetryMessage): Unit = {}
  }

  class TestWorkspaceManager(
      editorConfiguration: EditorConfiguration,
      fn: DidChangeConfigurationNotificationParams => Unit
  ) extends WorkspaceManager(
        new EnvironmentProvider {

          override def openedFiles: Seq[String] = Seq.empty

          override def initialize(): Future[Unit] = Future.unit

          override def filesInMemory: Map[String, TextDocument] = ???

          override def getResourceLoader: ResourceLoader = new ResourceLoader {
            override def fetch(resource: String): Future[Content] = Future.failed(new ResourceNotFound("Failed"))

            override def accepts(resource: String): Boolean = false
          }
        },
        editorConfiguration,
        IgnoreProjectConfigurationAdapter,
        Nil,
        Nil,
        buildWorkspaceManager.configurationManager
      ) {
    Logger.withTelemetry(new DummyTelemetryProvider())

    private val commandExecutors: Map[String, CommandExecutor[_, _]] = Map(
      Commands.DID_CHANGE_CONFIGURATION -> new TestDidChangeConfigurationCommandExecutor(this, fn)
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

  test("Lsp4j LanguageServerImpl Command - Change configuration Params Serialization") {
    var parsedOK = false
    def parsed(p: DidChangeConfigurationNotificationParams): Unit = {
      p.folder should equal("file://")
      p.mainPath should be(defined)
      p.mainPath.get should equal("path.raml")
      p.dependencies.exists({
        case Left(value) if value == "dep1"                                  => true
        case Right(value) if value.scope == KnownDependencyScopes.DEPENDENCY => value.file == "dep1"
        case _                                                               => false
      }) should be(true)
      p.dependencies.exists({
        case Right(value) => value.file == "profile1" && value.scope == KnownDependencyScopes.CUSTOM_VALIDATION
        case _            => false
      }) should be(true)
      p.dependencies.exists({
        case Right(value) => value.file == "semantic" && value.scope == KnownDependencyScopes.SEMANTIC_EXTENSION
        case _            => false
      }) should be(true)
      p.dependencies.exists({
        case Right(value) => value.file == "dialect" && value.scope == KnownDependencyScopes.DIALECT
        case _            => false
      }) should be(true)
      parsedOK = true
    }

    val args = List(
      changeConfigArgs(
        Some("path.raml"),
        "file://",
        Set("dep1", "dep2"),
        Set("profile1"),
        Set("semantic"),
        Set("dialect")
      )
    )

    new TestWorkspaceManager(EditorConfiguration(), parsed)
      .executeCommand(SharedExecuteParams(Commands.DID_CHANGE_CONFIGURATION, args))
      .map(_ => assert(parsedOK))
  }

  test("Lsp4j LanguageServerImpl Command - Change configuration Params LSP4J Serialization") {
    val argument = """"{\n\"folder\": \"file:///full/workspace/uri/\",\n\"mainPath\": \"インターフェース.raml\"\n}""""
    var parsedOK = false
    def parsed(p: DidChangeConfigurationNotificationParams): Unit = {
      parsedOK = true
      assert(p.folder == "file:///full/workspace/uri/")
      assert(p.mainPath.contains("インターフェース.raml"))
    }

    val args = List(argument)

    new TestWorkspaceManager(EditorConfiguration(), parsed)
      .executeCommand(SharedExecuteParams(Commands.DID_CHANGE_CONFIGURATION, args))
      .map(_ => assert(parsedOK))

  }

  test("Language server with AMF Validator test", Flaky) {

    val logger: Logger                            = EmptyLogger
    val clientConnection                          = new MockDiagnosticClientNotifier(3000)
    val notifier: AlsClientNotifier[StringWriter] = new MockAlsClientNotifier

    var flag: Boolean = false

    def fn = () => {
      flag = true
    }

    val server = new AlsLanguageServerFactory(clientConnection)
      .withSerializationProps(JvmSerializationProps(notifier))
      .withAmfPlugins(Seq(TestValidator(fn).asInstanceOf[ALSConverters.ClientAMFPlugin]).asJava)
      .withLogger(logger)
      .build()

    val content =
      """#%RAML 1.0
        |title: Example of request bodies
        |mediaType: application/json
        |
        |
        |/groups:
        |  post:
        |    body:
        |      application/xml:
        |        type: Person
        |        example: !include person.xml
        |
        |types:
        |  Person:
        |    properties:
        |      age: integer
        |""".stripMargin

    val payload: String = """<Person>
                            |  <age>false</age>
                            |</Person>""".stripMargin

    withServer(server)(s => {
      for {
        _ <- openFile(s)("file:///person.xml", payload)
        _ <- openFile(s)("file:///uri.raml", content)
        _ <- clientConnection.nextCall
        _ <- clientConnection.nextCall
        _ <- clientConnection.nextCall
      } yield {
        assert(flag)
      }
    })
  }

  def buildWorkspaceManager = new WorkspaceManagerFactoryBuilder(new MockDiagnosticClientNotifier)

  def buildServer(builder: WorkspaceManagerFactoryBuilder): (LanguageServer, WorkspaceManager) = {

    val dm       = builder.buildDiagnosticManagers()
    val managers = builder.buildWorkspaceManagerFactory()

    val b =
      new LanguageServerBuilder(
        managers.documentManager,
        managers.workspaceManager,
        managers.configurationManager,
        managers.resolutionTaskManager
      )
    dm.foreach(m => b.addInitializableModule(m))
    (b.build(), managers.workspaceManager)
  }

  override def rootPath: String = ""
}
