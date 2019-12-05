package org.mulesoft.als.server.workspace

import amf.client.remote.Content
import amf.client.resource.ResourceNotFound
import amf.core.remote.Platform
import amf.core.unsafe.PlatformSecrets
import amf.internal.environment.Environment
import amf.internal.resource.ResourceLoader
import org.mulesoft.als.server.client.ClientNotifier
import org.mulesoft.als.server.logger.EmptyLogger
import org.mulesoft.als.server.modules.ast.{CHANGE_CONFIG, CHANGE_FILE}
import org.mulesoft.als.server.modules.telemetry.TelemetryManager
import org.mulesoft.als.server.modules.workspace.WorkspaceContentManager
import org.mulesoft.als.server.textsync.EnvironmentProvider
import org.mulesoft.als.server.workspace.extract.{ConfigReader, WorkspaceConf}
import org.mulesoft.amfmanager.AmfInitializationHandler
import org.mulesoft.lsp.feature.diagnostic.PublishDiagnosticsParams
import org.mulesoft.lsp.feature.telemetry.TelemetryMessage
import org.scalatest.{AsyncFunSuite, Matchers}

import scala.concurrent.{ExecutionContext, Future}

class WorkspaceCacheTest extends AsyncFunSuite with Matchers with PlatformSecrets {

  override implicit val executionContext = ExecutionContext.Implicits.global

  private val configFileName = "exchange.json"

  private val mainApiName = "api.raml"

  private val cacheUris = Set("file://folder/cachable.raml")

  test("test cache unit simple") {
    val cachable =
      """#%RAML 1.0 Library
        |types:
        |  A: string
      """.stripMargin

    val api =
      """#%RAML 1.0
        |title: test
        |uses:
        |  lib: cachable.raml
        |types:
        |  B: lib.A
      """.stripMargin
    var counter: Int = 0
    val rl: ResourceLoader = new ResourceLoader {

      /** Fetch specified resource and return associated content. Resource should have been previously accepted. */
      override def fetch(resource: String): Future[Content] = {
        val content = if (resource == "file://folder/cachable.raml") {
          counter = counter + 1
          cachable
        } else if (resource == "file://folder/" + mainApiName) api
        else throw new ResourceNotFound("Not found: " + resource)

        Future.successful(new Content(content, resource))
      }

      /** Accepts specified resource. */
      override def accepts(resource: String): Boolean =
        resource == "file://folder/cachable.raml" || resource == "file://folder/" + mainApiName
    }

    val env = new EnvironmentProvider with PlatformSecrets {

      override def environmentSnapshot(): Environment = Environment(rl)

    }

    val ws =
      new WorkspaceContentManager("folder",
                                  Some(dummyConfigurationWorkspace(configFileName, mainApiName, cacheUris, None)),
                                  env,
                                  DummyTelemetryProvider,
                                  EmptyLogger,
                                  Nil,
                                  platform)
    AmfInitializationHandler.init()
    ws.initialize()
    ws.changedFile("file://folder/" + mainApiName, CHANGE_FILE)

    ws.getCompilableUnit("file://folder/" + mainApiName).flatMap(l => l.getLast).map { _ =>
      counter should be(1)
    }
  }

  test("test cache unit sub ref") {
    val cachable =
      """#%RAML 1.0 Library
        |uses:
        |  lib2: cachableSon.raml
        |types:
        |  A: string
      """.stripMargin

    val cachableSon =
      """#%RAML 1.0 Library
        |types:
        |  C: string
      """.stripMargin

    val api =
      """#%RAML 1.0
        |title: test
        |uses:
        |  lib: cachable.raml
        |types:
        |  B: lib.A
      """.stripMargin
    var counter: Int = 0
    val rl: ResourceLoader = new ResourceLoader {

      /** Fetch specified resource and return associated content. Resource should have benn previously accepted. */
      override def fetch(resource: String): Future[Content] = {
        val content = if (resource == "file://folder/cachableSon.raml") {
          counter = counter + 1
          cachableSon
        } else if (resource == "file://folder/cachable.raml") cachable
        else if (resource == "file://folder/" + mainApiName) api
        else throw new ResourceNotFound("Not found: " + resource)

        Future.successful(new Content(content, resource))
      }

      /** Accepts specified resource. */
      override def accepts(resource: String): Boolean =
        resource == "file://folder/cachable.raml" || resource == "file://folder/" + mainApiName || resource == "file://folder/cachableSon.raml"
    }

    val env = new EnvironmentProvider with PlatformSecrets {

      override def environmentSnapshot(): Environment = Environment(rl)

    }

    val ws =
      new WorkspaceContentManager("folder",
                                  Some(dummyConfigurationWorkspace(configFileName, mainApiName, cacheUris, None)),
                                  env,
                                  DummyTelemetryProvider,
                                  EmptyLogger,
                                  Nil,
                                  platform)

    ws.initialize()
    ws.changedFile("file://folder/" + mainApiName, CHANGE_FILE)

    ws.getCompilableUnit("file://folder/" + mainApiName).flatMap(l => l.getLast).map { _ =>
      counter should be(1)
    }
  }

  test("test non cache ") {
    val cachable =
      """#%RAML 1.0 Library
        |types:
        |  A: string
      """.stripMargin

    val api =
      """#%RAML 1.0
        |title: test
        |uses:
        |  lib: cachable.raml
        |types:
        |  B: lib.A
      """.stripMargin
    var counter: Int = 0
    val rl: ResourceLoader = new ResourceLoader {

      /** Fetch specified resource and return associated content. Resource should have benn previously accepted. */
      override def fetch(resource: String): Future[Content] = {
        val content = if (resource == "file://folder/cachable.raml") {
          counter = counter + 1
          cachable
        } else if (resource == "file://folder/" + mainApiName) api
        else throw new ResourceNotFound("Not found: " + resource)

        Future.successful(new Content(content, resource))
      }

      /** Accepts specified resource. */
      override def accepts(resource: String): Boolean =
        resource == "file://folder/cachable.raml" || resource == "file://folder/" + mainApiName
    }

    val env = new EnvironmentProvider with PlatformSecrets {

      override def environmentSnapshot(): Environment = Environment(rl)

    }

    val ws =
      new WorkspaceContentManager("folder",
                                  Some(dummyConfigurationWorkspace(configFileName, mainApiName, Set.empty, None)),
                                  env,
                                  DummyTelemetryProvider,
                                  EmptyLogger,
                                  Nil,
                                  platform)

    ws.initialize()
    ws.changedFile("file://folder/" + mainApiName, CHANGE_FILE)

    ws.getCompilableUnit("file://folder/" + mainApiName).flatMap(l => l.getLast).map { _ =>
      counter should be(2)
    }
  }

  test("test invalid dependency ") {
    val cachable =
      """#%RAML 1.0 Library
        |types:
        |  A: unresolved
      """.stripMargin

    val api =
      """#%RAML 1.0
        |title: test
        |uses:
        |  lib: cachable.raml
        |types:
        |  B: lib.A
      """.stripMargin
    var counter: Int = 0
    val rl: ResourceLoader = new ResourceLoader {

      /** Fetch specified resource and return associated content. Resource should have benn previously accepted. */
      override def fetch(resource: String): Future[Content] = {
        val content = if (resource == "file://folder/cachable.raml") {
          counter = counter + 1
          cachable
        } else if (resource == "file://folder/" + mainApiName) api
        else throw new ResourceNotFound("Not found: " + resource)

        Future.successful(new Content(content, resource))
      }

      /** Accepts specified resource. */
      override def accepts(resource: String): Boolean =
        resource == "file://folder/cachable.raml" || resource == "file://folder/" + mainApiName
    }

    val env = new EnvironmentProvider with PlatformSecrets {

      override def environmentSnapshot(): Environment = Environment(rl)

    }

    val ws =
      new WorkspaceContentManager("folder",
                                  Some(dummyConfigurationWorkspace(configFileName, mainApiName, cacheUris, None)),
                                  env,
                                  DummyTelemetryProvider,
                                  EmptyLogger,
                                  Nil,
                                  platform)

    ws.initialize()
    ws.changedFile("file://folder/" + mainApiName, CHANGE_FILE)

    ws.getCompilableUnit("file://folder/" + mainApiName).flatMap(l => l.getLast).map { _ =>
      counter should be(2)
    }
  }

  test("test cache unit when changing configuration") {
    val cachable =
      """#%RAML 1.0 Library
        |types:
        |  A: string
      """.stripMargin

    val api =
      """#%RAML 1.0
        |title: test
        |uses:
        |  lib: cachable.raml
        |types:
        |  B: lib.A
      """.stripMargin
    var counter: Int = 0
    val rl: ResourceLoader = new ResourceLoader {

      /** Fetch specified resource and return associated content. Resource should have benn previously accepted. */
      override def fetch(resource: String): Future[Content] = {
        val content = if (resource == "file://folder/cachable.raml") {
          counter = counter + 1
          cachable
        } else if (resource == "file://folder/" + mainApiName) api
        else throw new ResourceNotFound("Not found: " + resource)

        Future.successful(new Content(content, resource))
      }

      /** Accepts specified resource. */
      override def accepts(resource: String): Boolean =
        resource == "file://folder/cachable.raml" || resource == "file://folder/" + mainApiName
    }

    val env = new EnvironmentProvider with PlatformSecrets {

      override def environmentSnapshot(): Environment = Environment(rl)

    }

    val ws =
      new WorkspaceContentManager("folder",
                                  Some(dummyConfigurationWorkspace(configFileName, mainApiName, cacheUris, None)),
                                  env,
                                  DummyTelemetryProvider,
                                  EmptyLogger,
                                  Nil,
                                  platform)
    AmfInitializationHandler.init()
    ws.initialize()
    ws.changedFile("file://folder/" + mainApiName, CHANGE_FILE)

    for {
      _ <- { // first reparse
        ws.changedFile("file://folder/" + mainApiName, CHANGE_FILE)
        ws.getCompilableUnit("file://folder/" + mainApiName).flatMap(l => l.getLast).map { _ =>
          counter should be(1)
        }
      }
      _ <- { // remove caché
        counter = 0
        ws.changeConfigurationProvider(dummyConfig(configFileName, mainApiName, Set.empty))
        ws.changedFile("file://folder/" + configFileName, CHANGE_CONFIG)
        ws.getCompilableUnit("file://folder/" + mainApiName).flatMap(l => l.getLast).map { _ =>
          counter should be(1)
        }
      }
      _ <- { //reparse without cache
        ws.changedFile("file://folder/" + mainApiName, CHANGE_FILE)
        ws.getCompilableUnit("file://folder/" + mainApiName).flatMap(l => l.getLast).map { _ =>
          counter should be(2)
        }
      }
      _ <- { // with cache
        counter = 0
        ws.changeConfigurationProvider(dummyConfig(configFileName, mainApiName, cacheUris))
        ws.changedFile("file://folder/" + configFileName, CHANGE_CONFIG)
        ws.changedFile("file://folder/" + mainApiName, CHANGE_FILE)
        ws.getCompilableUnit("file://folder/" + mainApiName).flatMap(l => l.getLast).map { _ =>
          counter should be(1)
        }
      }
    } yield {
      succeed
    }
  }

  private def dummyConfig(confFileName: String,
                          mainApiName: String,
                          cacheUris: Set[String]): WorkspaceConfigurationProvider =
    new WorkspaceConfigurationProvider {
      override def obtainConfiguration(platform: Platform, environment: Environment): Future[Option[WorkspaceConf]] =
        Future.successful {
          Some(
            dummyConfigurationWorkspace(confFileName,
                                        mainApiName,
                                        cacheUris,
                                        Some(dummyConfigurationWorkspace(confFileName, mainApiName, cacheUris, None))))
        }
    }

  private def dummyConfigurationWorkspace(confFileName: String,
                                          mainApiName: String,
                                          cacheUris: Set[String],
                                          dummyWorkspaceConfig: Option[WorkspaceConf]) =
    WorkspaceConf(
      confFileName,
      mainApiName,
      cacheUris,
      new ConfigReader {
        override val configFileName: String = confFileName

        protected def buildConfig(content: String, path: String, platform: Platform): Option[Future[WorkspaceConf]] =
          dummyWorkspaceConfig.map(Future.successful)
      }
    )

  object DummyTelemetryProvider extends TelemetryManager(DummyClientNotifier, EmptyLogger)

  object DummyClientNotifier extends ClientNotifier {
    override def notifyDiagnostic(params: PublishDiagnosticsParams): Unit = {}

    override def notifyTelemetry(params: TelemetryMessage): Unit = {}
  }

}
