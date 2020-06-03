package org.mulesoft.als.server.modules.diagnostic

import amf.client.remote.Content
import amf.internal.environment.Environment
import amf.internal.resource.ResourceLoader
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.{LanguageServerBaseTest, LanguageServerBuilder, MockDiagnosticClientNotifier}
import org.mulesoft.amfintegration.AmfInstance

import scala.concurrent.{ExecutionContext, Future}

class ServerCleanDiagnosticTest extends LanguageServerBaseTest {

  override implicit val executionContext = ExecutionContext.Implicits.global

  val rl: ResourceLoader = new ResourceLoader {

    private val files: Map[String, String] = Map(
      "file://file%20with%20spaces.raml" ->
        """#%RAML 1.0
        |description: this is a RAML without title""".stripMargin
    )

    override def fetch(resource: String): Future[Content] =
      files
        .get(resource)
        .map { f =>
          new Content(f, resource)
        }
        .map(Future.successful)
        .getOrElse(Future.failed(new Exception(s"Wrong resource $resource")))

    override def accepts(resource: String): Boolean = files.keySet.contains(resource)
  }
  val env: Environment = Environment().add(rl)

  def buildServer(diagnosticNotifier: MockDiagnosticClientNotifier): LanguageServer = {
    val amfInstance = new AmfInstance(Nil, platform, env)
    val builder     = new WorkspaceManagerFactoryBuilder(diagnosticNotifier, logger, env).withAmfConfiguration(amfInstance)
    val dm          = builder.diagnosticManager()
    val factory     = builder.buildWorkspaceManagerFactory()
    val b           = new LanguageServerBuilder(factory.documentManager, factory.workspaceManager, factory.resolutionTaskManager)
    b.addRequestModule(factory.cleanDiagnosticManager)
    dm.foreach(b.addInitializableModule)
    b.build()
  }

  override def rootPath: String = ???

  test("Test resource loader invocation from clean diagnostic with encoded uri") {
    val diagnosticNotifier: MockDiagnosticClientNotifier = new MockDiagnosticClientNotifier
    withServer(buildServer(diagnosticNotifier)) { server =>
      val apiPath = s"file://file%20with%20spaces.raml"

      for {
        d <- requestCleanDiagnostic(server)(apiPath)
      } yield {
        server.shutdown()
        assert(d.length == 1)
      }
    }
  }

  test("Clean diagnostic test, compare notification against clean") {
    val diagnosticNotifier: MockDiagnosticClientNotifier = new MockDiagnosticClientNotifier
    withServer(buildServer(diagnosticNotifier)) { s =>
      val mainFilePath = s"file://api.raml"

      val mainContent =
        """#%RAML 1.0
          |title: Recursive
          |types:
          |  Recursive:
          |    type: object
          |    properties:
          |      myP:
          |        type: Recursive
          |/recursiveType:
          |  post:
          |    responses:
          |      201:
          |        body:
          |          application/json:
          |            type: Recursive
        """.stripMargin

      for {
        _  <- openFileNotification(s)(mainFilePath, mainContent)
        d  <- diagnosticNotifier.nextCall
        v1 <- requestCleanDiagnostic(s)(mainFilePath)

      } yield {
        s.shutdown()

        d.diagnostics.size should be(1)
        v1.length should be(1)
        val fileDiagnostic = v1.head
        fileDiagnostic.diagnostics.size should be(1)
      }
    }
  }
}
