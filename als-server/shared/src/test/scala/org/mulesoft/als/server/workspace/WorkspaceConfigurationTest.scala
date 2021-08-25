package org.mulesoft.als.server.workspace

import amf.core.client.common.remote.Content
import amf.core.client.platform.resource.ResourceNotFound
import amf.core.client.scala.resource.ResourceLoader
import amf.core.internal.remote.Platform
import org.mulesoft.als.common.{DirectoryResolver, PlatformDirectoryResolver}
import org.mulesoft.als.server._
import org.mulesoft.als.server.feature.configuration.workspace.{
  GetWorkspaceConfigurationParams,
  GetWorkspaceConfigurationRequestType,
  GetWorkspaceConfigurationResult
}
import org.mulesoft.als.configuration.ConfigurationStyle.{COMMAND, FILE}
import org.mulesoft.als.configuration.ProjectConfigurationStyle
import org.mulesoft.als.server.logger.{Logger, MessageSeverity}
import org.mulesoft.als.server.modules.WorkspaceManagerFactory
import org.mulesoft.als.server.modules.ast.{BaseUnitListener, ResolvedUnitListener}
import org.mulesoft.als.server.modules.common.reconciler.Runnable
import org.mulesoft.als.server.modules.configuration.ConfigurationManager
import org.mulesoft.als.server.modules.telemetry.TelemetryManager
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.protocol.configuration.AlsInitializeParams
import org.mulesoft.amfintegration.AmfResolvedUnit
import org.mulesoft.amfintegration.amfconfiguration.AmfConfigurationWrapper
import org.mulesoft.lsp.configuration.TraceKind
import org.mulesoft.lsp.feature.common.TextDocumentIdentifier
import org.mulesoft.lsp.workspace.ExecuteCommandParams
import org.scalatest.Assertion

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future, Promise}

// TODO: when implemented Validation Profile and Semantic Extension, assert in tests the mutability of AmfConfiguration
//   for example, start test, register/unregister dialect, check that the resulting unit still has the starting dialects
class WorkspaceConfigurationTest extends LanguageServerBaseTest with ChangesWorkspaceConfiguration {
  override def rootPath: String = ""

  implicit override def executionContext: ExecutionContext =
    scala.concurrent.ExecutionContext.Implicits.global
  implicit val p: Platform = platform
  private val mainApiUri   = "file://folder/api.raml"
  private val isolatedUri  = "file://folder/isolated.raml"
  private val exchangeUri  = "file://folder/exchange.json"
  val api                  = "#%RAML 1.0\ntitle: test\n"
  val isolated             = "#%RAML 1.0\ntitle: test2\n"
  val exchange             = "{\n  \"main\": \"api.raml\"\n}"

  def rl(withExchangeFile: Boolean): ResourceLoader = new ResourceLoader {

    /** Fetch specified resource and return associated content. Resource should have been previously accepted. */
    override def fetch(resource: String): Future[Content] = {
      val content =
        if (resource == mainApiUri) api
        else if (resource == isolatedUri) isolated
        else if (withExchangeFile && resource == exchangeUri) exchange
        else throw new ResourceNotFound("Not found: " + resource)

      Future.successful(new Content(content, resource))
    }

    /** Accepts specified resource. */
    override def accepts(resource: String): Boolean =
      resource == mainApiUri ||
        (withExchangeFile && resource == exchangeUri) ||
        resource == isolatedUri
  }

  test("Unit from main tree should contain configuration") {
    val (factory: WorkspaceManagerFactory, listener) = createPatchedWorkspaceManagerFactory()
    val workspaceManager: WorkspaceManager           = factory.workspaceManager
    withServer[Assertion](buildServer(factory)) { server =>
      for {
        _    <- server.initialize(AlsInitializeParams(None, Some(TraceKind.Off), rootUri = Some(s"file://folder")))
        _    <- listener.nextCall // parse main file
        unit <- workspaceManager.getUnit(mainApiUri, UUID.randomUUID().toString)
      } yield {
        assert(unit.mainFile.contains(mainApiUri))
//        assert(unit.workspaceConfiguration.isDefined)
//        assert(unit.workspaceConfiguration.exists(_.mainFile == "api.raml"))
      }
    }
  }

  test("Isolated unit should contain configuration") {
    val (factory: WorkspaceManagerFactory, listener) = createPatchedWorkspaceManagerFactory()
    val workspaceManager: WorkspaceManager           = factory.workspaceManager
    withServer[Assertion](buildServer(factory)) { server =>
      for {
        _    <- server.initialize(AlsInitializeParams(None, Some(TraceKind.Off), rootUri = Some(s"file://folder")))
        _    <- listener.nextCall // parse main file
        _    <- openFileNotification(server)(isolatedUri, isolated)
        _    <- listener.nextCall // parse isolated
        unit <- workspaceManager.getUnit(isolatedUri, UUID.randomUUID().toString)
      } yield {
        assert(unit.mainFile.isEmpty)
//        assert(unit.workspaceConfiguration.isDefined)
//        assert(unit.workspaceConfiguration.exists(_.mainFile == "api.raml"))
      }
    }
  }
  test("Should update the configuration by command for new Units") {
    val (factory: WorkspaceManagerFactory, parserListener) = createPatchedWorkspaceManagerFactory()
    val workspaceManager: WorkspaceManager                 = factory.workspaceManager
    val initialArgs                                        = List(wrapJson(mainApiUri))
    val args                                               = wrapJson(isolatedUri)
    withServer[Assertion](buildServer(factory)) { server =>
      for {
        _ <- server.initialize(
          AlsInitializeParams(None,
                              Some(TraceKind.Off),
                              rootUri = Some(s"file://folder"),
                              projectConfigurationStyle = Some(ProjectConfigurationStyle(COMMAND))))
        _          <- workspaceManager.executeCommand(ExecuteCommandParams("didChangeConfiguration", initialArgs))
        _          <- parserListener.nextCall // parse main file
        _          <- openFileNotification(server)(isolatedUri, isolated)
        _          <- parserListener.nextCall // parse isolated
        firstUnit  <- workspaceManager.getUnit(mainApiUri, UUID.randomUUID().toString)
        _          <- changeWorkspaceConfiguration(workspaceManager, args)
        _          <- parserListener.nextCall
        _          <- openFileNotification(server)(mainApiUri, isolated)
        _          <- parserListener.nextCall
        secondUnit <- workspaceManager.getUnit(mainApiUri, UUID.randomUUID().toString)
        thirdUnit  <- workspaceManager.getUnit(isolatedUri, UUID.randomUUID().toString)
      } yield {
        assert(firstUnit.mainFile.contains(mainApiUri))
//        assert(firstUnit.workspaceConfiguration.isDefined)
//        assert(firstUnit.workspaceConfiguration.exists(_.mainFile == "api.raml"))

        assert(secondUnit.mainFile.isEmpty)
//        assert(secondUnit.workspaceConfiguration.isDefined)
//        assert(secondUnit.workspaceConfiguration.exists(_.mainFile == "isolated.raml"))

        assert(thirdUnit.mainFile.contains(isolatedUri))
//        assert(thirdUnit.workspaceConfiguration.isDefined)
//        assert(thirdUnit.workspaceConfiguration.exists(_.mainFile == "isolated.raml"))
      }
    }
  }

  test("Should notify project dependencies the configuration used") {
    val (factory: WorkspaceManagerFactory, listener) = createPatchedWorkspaceManagerFactory()
    withServer[Assertion](buildServer(factory)) { server =>
      for {
        _              <- server.initialize(AlsInitializeParams(None, Some(TraceKind.Off), rootUri = Some(s"file://folder")))
        mainFileResult <- listener.nextCall
        _              <- openFileNotification(server)(isolatedUri, isolated)
        isolatedResult <- listener.nextCall
      } yield {
        assert(!isolatedResult.tree)
//        assert(isolatedResult.workspaceConfiguration.isDefined)
//        assert(isolatedResult.workspaceConfiguration.exists(_.mainFile == "api.raml"))

        assert(mainFileResult.tree)
//        assert(mainFileResult.workspaceConfiguration.isDefined)
//        assert(mainFileResult.workspaceConfiguration.exists(_.mainFile == "api.raml"))
      }
    }
  }

  test("Should notify resolution dependencies the configuration used") {
    val listener                              = new MockResolutionListener(logger)
    val (factory: WorkspaceManagerFactory, _) = createPatchedWorkspaceManagerFactory(List.empty, List(listener))

    withServer[Assertion](buildServer(factory)) { server =>
      for {
        _              <- server.initialize(AlsInitializeParams(None, Some(TraceKind.Off), rootUri = Some(s"file://folder")))
        mainFileResult <- listener.nextCall
        _              <- openFileNotification(server)(isolatedUri, isolated)
        isolatedResult <- listener.nextCall
      } yield {
        assert(isolatedResult.amfConfiguration.workspaceConfiguration.exists(_.mainFile == "api.raml"))
        assert(mainFileResult.amfConfiguration.workspaceConfiguration.isDefined)
        assert(mainFileResult.amfConfiguration.workspaceConfiguration.exists(_.mainFile == "api.raml"))
      }
    }
  }

  def getWorkspaceConfiguration(server: LanguageServer, uri: String): Future[GetWorkspaceConfigurationResult] =
    server
      .resolveHandler(GetWorkspaceConfigurationRequestType)
      .get(GetWorkspaceConfigurationParams(TextDocumentIdentifier(uri)))

  test("Get workspace notification request should return current configuration") {
    val listener                              = new MockResolutionListener(logger)
    val (factory: WorkspaceManagerFactory, _) = createPatchedWorkspaceManagerFactory(List.empty, List(listener))
    val args                                  = wrapJson(isolatedUri)
    val args2                                 = wrapJson(isolatedUri, None, Set.empty, Set("profile.yaml"))
    val args3                                 = wrapJson(isolatedUri, None, Set("dependency.yaml"))
    val workspaceManager                      = factory.workspaceManager
    withServer[Assertion](buildServer(factory)) { server =>
      for {
        _ <- server.initialize(
          AlsInitializeParams(None,
                              Some(TraceKind.Off),
                              rootUri = Some(s"file://folder"),
                              projectConfigurationStyle = Some(ProjectConfigurationStyle(COMMAND))))
        config1 <- getWorkspaceConfiguration(server, mainApiUri)
        _       <- changeWorkspaceConfiguration(workspaceManager, args)
        _       <- listener.nextCall
        config2 <- getWorkspaceConfiguration(server, mainApiUri)
        _       <- changeWorkspaceConfiguration(workspaceManager, args2)
        _       <- listener.nextCall
        config3 <- getWorkspaceConfiguration(server, mainApiUri)
        _       <- changeWorkspaceConfiguration(workspaceManager, args3)
        _       <- listener.nextCall
        config4 <- getWorkspaceConfiguration(server, mainApiUri)
      } yield {
        assert(config1.workspace == """file://folder""")
        assert(config2.workspace == """file://folder""")
        assert(config3.workspace == """file://folder""")
        assert(config4.workspace == """file://folder""")
        assert(config1.configuration.mainUri == "")
        assert(config2.configuration.mainUri == "isolated.raml")
        assert(config3.configuration.mainUri == "isolated.raml")
        assert(config4.configuration.mainUri == "isolated.raml")
        assert(config1.configuration.customValidationProfiles.isEmpty)
        assert(config2.configuration.customValidationProfiles.isEmpty)
        assert(config3.configuration.customValidationProfiles.contains("profile.yaml"))
        assert(config1.configuration.dependencies.isEmpty)
        assert(config2.configuration.dependencies.isEmpty)
        assert(config3.configuration.dependencies.isEmpty)
        assert(config4.configuration.dependencies.contains("dependency.yaml"))

      }
    }
  }

  def createPatchedWorkspaceManagerFactory(
      projectDependencies: List[BaseUnitListener] = List.empty,
      resolutionDependencies: List[ResolvedUnitListener] = List.empty,
      resourceLoader: ResourceLoader = rl(true)): (WorkspaceManagerFactory, MockParseListener) = {

    val clientNotifier                       = new MockDiagnosticClientNotifier
    val telemetryManager: TelemetryManager   = new TelemetryManager(clientNotifier, logger)
    val directoryResolver: DirectoryResolver = new PlatformDirectoryResolver(platform)
    val parserListener                       = new MockParseListener()
    val amfConfiguration                     = AmfConfigurationWrapper(Seq(resourceLoader))
    (WorkspaceManagerFactory(
       projectDependencies :+ parserListener,
       resolutionDependencies,
       telemetryManager,
       directoryResolver,
       logger,
       amfConfiguration,
       new ConfigurationManager()
     ),
     parserListener)
  }

  def buildServer(factory: WorkspaceManagerFactory): LanguageServer =
    new LanguageServerBuilder(factory.documentManager,
                              factory.workspaceManager,
                              factory.configurationManager,
                              factory.resolutionTaskManager)
      .addRequestModule(factory.workspaceConfigurationManager)
      .build()

  class MockResolutionListener(override val logger: Logger)
      extends ResolvedUnitListener
      with AbstractTestClientNotifier[AmfResolvedUnit]
      with TimeoutFuture {
    override type RunType = CallbackRunnable

    override protected def runnable(ast: AmfResolvedUnit, uuid: String): CallbackRunnable =
      new CallbackRunnable(ast.baseUnit.id, ast, this)

    override protected def onSuccess(uuid: String, uri: String): Unit =
      logger.log(s"success: $uri, uuid: $uuid", MessageSeverity.DEBUG, "MockResolutionListener", "onSuccess")

    override protected def onFailure(uuid: String, uri: String, t: Throwable): Unit =
      logger.log(s"Failed: $uri, uuid: $uuid", MessageSeverity.ERROR, "MockResolutionListener", "onFailure")

    override protected def onNewAstPreprocess(resolved: AmfResolvedUnit, uuid: String): Unit =
      logger.debug("notified", "MockResolutionListener", "onNewAstPreprocess")

    override def onRemoveFile(uri: String): Unit = {}

    override def nextCall: Future[AmfResolvedUnit] = timeoutFuture(super.nextCall, 1000)

    class CallbackRunnable(val uri: String, ast: AmfResolvedUnit, callback: MockResolutionListener)
        extends Runnable[Unit] {
      val kind      = "CallbackRunnable"
      var cancelled = false
      override def run(): Promise[Unit] = {
        callback.notify(ast)
        Promise.successful()
      }

      override def conflicts(other: Runnable[Any]): Boolean =
        other.asInstanceOf[CallbackRunnable].kind == kind && uri == other
          .asInstanceOf[CallbackRunnable]
          .uri

      override def cancel(): Unit = cancelled = true

      override def isCanceled(): Boolean = cancelled
    }
  }

}
