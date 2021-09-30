package org.mulesoft.als.server.workspace

import amf.core.client.common.remote.Content
import amf.core.client.platform.resource.ResourceNotFound
import amf.core.client.scala.resource.ResourceLoader
import amf.core.internal.unsafe.PlatformSecrets
import org.mulesoft.als.server.client.ClientNotifier
import org.mulesoft.als.logger.EmptyLogger
import org.mulesoft.als.server.modules.ast.{CHANGE_CONFIG, CHANGE_FILE}
import org.mulesoft.als.server.modules.telemetry.TelemetryManager
import org.mulesoft.als.server.modules.workspace.WorkspaceContentManager
import org.mulesoft.als.server.textsync.{EnvironmentProvider, TextDocumentContainer}
import org.mulesoft.als.server.workspace.extract.DefaultWorkspaceConfigurationProvider
import org.mulesoft.amfintegration.amfconfiguration.AmfConfigurationWrapper
import org.mulesoft.lsp.feature.diagnostic.PublishDiagnosticsParams
import org.mulesoft.lsp.feature.telemetry.TelemetryMessage
import org.scalatest.{AsyncFunSuite, Matchers}
import org.mulesoft.als.configuration.DefaultProjectConfigurationStyle

import scala.concurrent.{ExecutionContext, Future}

class WorkspaceCacheTest extends AsyncFunSuite with Matchers with PlatformSecrets {

  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  private val rootUri     = ""
  private val mainApiName = "api.raml"
  private val folderUri   = "file://folder"
  private val cachableUri = folderUri + "/cachable.raml"
  private val cacheUris   = Set(cachableUri)
  val mainApiUri          = s"$folderUri/$mainApiName"

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
        val content = if (resource == cachableUri) {
          counter = counter + 1
          cachable
        } else if (resource == mainApiUri) api
        else throw new ResourceNotFound("Not found: " + resource)

        Future.successful(new Content(content, resource))
      }

      /** Accepts specified resource. */
      override def accepts(resource: String): Boolean =
        resource == cachableUri || resource == mainApiUri
    }

    val env = newProvider(rl)

    for {
      ws <- WorkspaceContentManager(folderUri,
                                    env,
                                    DummyTelemetryProvider,
                                    EmptyLogger,
                                    Nil,
                                    DefaultProjectConfigurationStyle)
      _ <- ws
        .withConfiguration(
          DefaultWorkspaceConfigurationProvider(ws, mainApiUri, cacheUris, Set.empty, Set.empty, Set.empty, None))
        .stage(mainApiUri, CHANGE_CONFIG)
      counter1 <- ws.getUnit(mainApiUri).flatMap(l => l.getLast).map { _ =>
        counter
      }
      _ <- ws.stage(mainApiUri, CHANGE_FILE)
      counter2 <- ws.getUnit(mainApiUri).flatMap(l => l.getLast).map { _ =>
        counter
      }
    } yield {
      counter1 should be(1)
      counter2 should be(1)
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
        val content = if (resource == folderUri + "/cachableSon.raml") {
          counter = counter + 1
          cachableSon
        } else if (resource == cachableUri) cachable
        else if (resource == mainApiUri) api
        else throw new ResourceNotFound("Not found: " + resource)

        Future.successful(new Content(content, resource))
      }

      /** Accepts specified resource. */
      override def accepts(resource: String): Boolean =
        resource == cachableUri || resource == mainApiUri || resource == folderUri + "/cachableSon.raml"
    }

    val env = newProvider(rl)

    for {
      ws <- WorkspaceContentManager("folder",
                                    env,
                                    DummyTelemetryProvider,
                                    EmptyLogger,
                                    Nil,
                                    DefaultProjectConfigurationStyle)
      _ <- ws
        .withConfiguration(
          DefaultWorkspaceConfigurationProvider(ws, mainApiName, cacheUris, Set.empty, Set.empty, Set.empty, None))
        .stage(mainApiUri, CHANGE_CONFIG)
      counter1 <- ws.getUnit(mainApiUri).flatMap(l => l.getLast).map { _ =>
        counter
      }
      _ <- ws.stage(mainApiUri, CHANGE_FILE)
      counter2 <- ws.getUnit(mainApiUri).flatMap(l => l.getLast).map { _ =>
        counter
      }
    } yield {
      counter1 should be(1)
      counter2 should be(1)
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

      /** Fetch specified resource and return associated content. Resource should have been previously accepted. */
      override def fetch(resource: String): Future[Content] = {
        val content = if (resource == cachableUri) {
          counter = counter + 1
          cachable
        } else if (resource == mainApiUri) api
        else throw new ResourceNotFound("Not found: " + resource)

        Future.successful(new Content(content, resource))
      }

      /** Accepts specified resource. */
      override def accepts(resource: String): Boolean =
        resource == cachableUri || resource == mainApiUri
    }

    val env = newProvider(rl)

    for {
      ws <- WorkspaceContentManager("folder",
                                    env,
                                    DummyTelemetryProvider,
                                    EmptyLogger,
                                    Nil,
                                    DefaultProjectConfigurationStyle)
      _ <- ws
        .withConfiguration(
          DefaultWorkspaceConfigurationProvider(ws, mainApiName, Set.empty, Set.empty, Set.empty, Set.empty, None))
        .stage(mainApiUri, CHANGE_CONFIG)
      counter1 <- ws.getUnit(mainApiUri).flatMap(l => l.getLast).map { _ =>
        counter
      }
      _ <- ws.stage(mainApiUri, CHANGE_FILE)
      counter2 <- ws.getUnit(mainApiUri).flatMap(l => l.getLast).map { _ =>
        counter
      }
    } yield {
      counter1 should be(1)
      counter2 should be(2)
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
        val content = if (resource == cachableUri) {
          counter = counter + 1
          cachable
        } else if (resource == mainApiUri) api
        else throw new ResourceNotFound("Not found: " + resource)

        Future.successful(new Content(content, resource))
      }

      /** Accepts specified resource. */
      override def accepts(resource: String): Boolean =
        resource == cachableUri || resource == mainApiUri
    }

    val env = newProvider(rl)

    for {
      ws <- WorkspaceContentManager(folderUri,
                                    env,
                                    DummyTelemetryProvider,
                                    EmptyLogger,
                                    Nil,
                                    DefaultProjectConfigurationStyle)
      _ <- ws
        .withConfiguration(
          DefaultWorkspaceConfigurationProvider(ws, mainApiName, cacheUris, Set.empty, Set.empty, Set.empty, None))
        .stage(mainApiUri, CHANGE_CONFIG)
      counter1 <- ws.getUnit(mainApiUri).flatMap(l => l.getLast).map { _ =>
        counter
      }
      _ <- ws.stage(mainApiUri, CHANGE_FILE)
      counter2 <- ws.getUnit(mainApiUri).flatMap(l => l.getLast).map { _ =>
        counter
      }
    } yield {
      counter1 should be(1)
      counter2 should be(2)
    }
  }

  private def newProvider(rl: ResourceLoader): EnvironmentProvider =
    TextDocumentContainer(AmfConfigurationWrapper(Seq(rl)))

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
        val content = if (resource == cachableUri) {
          counter = counter + 1
          cachable
        } else if (resource == mainApiUri) api
        else throw new ResourceNotFound("Not found: " + resource)

        Future.successful(new Content(content, resource))
      }

      /** Accepts specified resource. */
      override def accepts(resource: String): Boolean =
        resource == cachableUri || resource == mainApiUri
    }

    val env = newProvider(rl)

    for {
      ws <- WorkspaceContentManager("folder",
                                    env,
                                    DummyTelemetryProvider,
                                    EmptyLogger,
                                    Nil,
                                    DefaultProjectConfigurationStyle)
      _ <- ws
        .withConfiguration(
          DefaultWorkspaceConfigurationProvider(ws, mainApiName, cacheUris, Set.empty, Set.empty, Set.empty, None))
        .stage(mainApiUri, CHANGE_CONFIG)
      counter1 <- ws.getUnit(mainApiUri).flatMap(l => l.getLast).map { _ =>
        counter
      }
      _ <- // first reparse
      ws.stage(mainApiUri, CHANGE_FILE)
      counter2 <- ws.getUnit(mainApiUri).flatMap(l => l.getLast).map { _ =>
        counter
      }
      _ <- { // remove caché
        counter = 0
        ws.withConfiguration(
            DefaultWorkspaceConfigurationProvider(ws, mainApiName, Set.empty, Set.empty, Set.empty, Set.empty, None))
          .stage(mainApiUri, CHANGE_CONFIG)
      }
      counter3 <- ws.getUnit(mainApiUri).flatMap(l => l.getLast).map { _ =>
        counter
      }
      _ <- // reparse without cache
      ws.stage(mainApiUri, CHANGE_FILE)
      counter4 <- ws.getUnit(mainApiUri).flatMap(l => l.getLast).map { _ =>
        counter
      }
      _ <- { // with cache
        counter = 0
        ws.withConfiguration(
            DefaultWorkspaceConfigurationProvider(ws, mainApiName, cacheUris, Set.empty, Set.empty, Set.empty, None))
          .stage(folderUri + "/" + rootUri, CHANGE_CONFIG)
      }
      _ <- ws.stage(mainApiUri, CHANGE_FILE)
      counter5 <- ws.getUnit(mainApiUri).flatMap(l => l.getLast).map { _ =>
        counter
      }
    } yield {
      counter1 should be(1)
      counter2 should be(1)
      counter3 should be(1)
      counter4 should be(2)
      counter5 should be(1)
    }
  }

  object DummyTelemetryProvider extends TelemetryManager(DummyClientNotifier, EmptyLogger)

  object DummyClientNotifier extends ClientNotifier {
    override def notifyDiagnostic(params: PublishDiagnosticsParams): Unit = {}

    override def notifyTelemetry(params: TelemetryMessage): Unit = {}
  }

}
