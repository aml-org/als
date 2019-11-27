package org.mulesoft.als.server.workspace

import amf.client.remote.Content
import amf.client.resource.ResourceNotFound
import amf.core.unsafe.PlatformSecrets
import amf.internal.environment.Environment
import amf.internal.resource.ResourceLoader
import org.mulesoft.als.server.client.ClientNotifier
import org.mulesoft.als.server.logger.EmptyLogger
import org.mulesoft.als.server.modules.ast.CHANGE_FILE
import org.mulesoft.als.server.modules.telemetry.TelemetryManager
import org.mulesoft.als.server.modules.workspace.WorkspaceContentManager
import org.mulesoft.als.server.textsync.EnvironmentProvider
import org.mulesoft.als.server.workspace.extract.WorkspaceConf
import org.mulesoft.amfmanager.AmfInitializationHandler
import org.mulesoft.lsp.feature.diagnostic.PublishDiagnosticsParams
import org.mulesoft.lsp.feature.telemetry.TelemetryMessage
import org.scalatest.{AsyncFunSuite, Matchers}

import scala.concurrent.{ExecutionContext, Future}

class WorkspaceCacheTest extends AsyncFunSuite with Matchers {

  override implicit val executionContext = ExecutionContext.Implicits.global

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

      /** Fetch specified resource and return associated content. Resource should have benn previously accepted. */
      override def fetch(resource: String): Future[Content] = {
        val content = if (resource == "file://folder/cachable.raml") {
          counter = counter + 1
          cachable
        } else if (resource == "file://folder/api.raml") api
        else throw new ResourceNotFound("Not found: " + resource)

        Future.successful(new Content(content, resource))
      }

      /** Accepts specified resource. */
      override def accepts(resource: String): Boolean =
        resource == "file://folder/cachable.raml" || resource == "file://folder/api.raml"
    }

    val env = new EnvironmentProvider with PlatformSecrets {

      override def environmentSnapshot(): Environment = Environment(rl)

    }

    val ws =
      new WorkspaceContentManager("folder",
                                  Some(WorkspaceConf("exchange.json", "api.raml", Set("file://folder/cachable.raml"))),
                                  env,
                                  DummyTelemetryProvider,
                                  EmptyLogger,
                                  Nil)
    AmfInitializationHandler.init()
    ws.initialize()
    ws.changedFile("file://folder/api.raml", CHANGE_FILE)

    ws.getCompilableUnit("file://folder/api.raml").flatMap(l => l.getLast).map { _ =>
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
        else if (resource == "file://folder/api.raml") api
        else throw new ResourceNotFound("Not found: " + resource)

        Future.successful(new Content(content, resource))
      }

      /** Accepts specified resource. */
      override def accepts(resource: String): Boolean =
        resource == "file://folder/cachable.raml" || resource == "file://folder/api.raml" || resource == "file://folder/cachableSon.raml"
    }

    val env = new EnvironmentProvider with PlatformSecrets {

      override def environmentSnapshot(): Environment = Environment(rl)

    }

    val ws =
      new WorkspaceContentManager("folder",
                                  Some(WorkspaceConf("exchange.json", "api.raml", Set("file://folder/cachable.raml"))),
                                  env,
                                  DummyTelemetryProvider,
                                  EmptyLogger,
                                  Nil)

    ws.initialize()
    ws.changedFile("file://folder/api.raml", CHANGE_FILE)

    ws.getCompilableUnit("file://folder/api.raml").flatMap(l => l.getLast).map { _ =>
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
        } else if (resource == "file://folder/api.raml") api
        else throw new ResourceNotFound("Not found: " + resource)

        Future.successful(new Content(content, resource))
      }

      /** Accepts specified resource. */
      override def accepts(resource: String): Boolean =
        resource == "file://folder/cachable.raml" || resource == "file://folder/api.raml"
    }

    val env = new EnvironmentProvider with PlatformSecrets {

      override def environmentSnapshot(): Environment = Environment(rl)

    }

    val ws =
      new WorkspaceContentManager("folder",
                                  Some(WorkspaceConf("exchange.json", "api.raml", Set.empty)),
                                  env,
                                  DummyTelemetryProvider,
                                  EmptyLogger,
                                  Nil)

    ws.initialize()
    ws.changedFile("file://folder/api.raml", CHANGE_FILE)

    ws.getCompilableUnit("file://folder/api.raml").flatMap(l => l.getLast).map { _ =>
      counter should be(2)
    }
  }
  object DummyTelemetryProvider extends TelemetryManager(DummyClientNotifier, EmptyLogger)

  object DummyClientNotifier extends ClientNotifier {
    override def notifyDiagnostic(params: PublishDiagnosticsParams): Unit = {}

    override def notifyTelemetry(params: TelemetryMessage): Unit = {}
  }
}
