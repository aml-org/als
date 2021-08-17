package org.mulesoft.als.server.modules.diagnostic

import amf.core.client.common.remote.Content
import amf.core.client.common.validation.ProfileNames
import amf.core.client.scala.resource.ResourceLoader
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.{LanguageServerBaseTest, LanguageServerBuilder, MockDiagnosticClientNotifier}

import scala.concurrent.{ExecutionContext, Future}

class ServerCleanDiagnosticTest extends LanguageServerBaseTest {

  override implicit val executionContext = ExecutionContext.Implicits.global

  val rl: ResourceLoader = new ResourceLoader {

    private val files: Map[String, String] = Map(
      "file://file%20with%20spaces.raml" ->
        """#%RAML 1.0
        |description: this is a RAML without title""".stripMargin,
      "file://api.raml"        -> """#%RAML 0.8
                             |title: GitHub API
                             |resourceTypes:
                             |   - collection: !include collection.raml
                             |
                             |traits:
                             |   - paged: !include paged.raml
                             |
                             |schemas:
                             |  - User: !include /user.raml
                             |
                             |securitySchemes:
                             |  - oauth_2_0: !include /oauth_2_0.raml
                             |
                             |/users:
                             |  type: collection
                             |  securedBy: [ oauth_2_0: { scopes: [ ADMIN ] } ]
                             |  get:
                             |    is: [ paged ]
                             |    responses:
                             |        200:
                             |          body:
                             |            application/json:
                             |              schema: User""".stripMargin,
      "file://collection.raml" -> """#%RAML 1.0 ResourceType
                                    |usage: This resourceType should be used for any collection of items
                                    |description: The collection of <<resourcePathName>>
                                    |get:
                                    |  description: Get all <<resourcePathName>>, optionally filtered
                                    |post:
                                    |  description: Create a new <<resourcePathName | !singularize>>""".stripMargin,
      "file://oauth_2_0.raml"  -> """#%RAML 1.0 SecurityScheme
                                   |description: |
                                   |  This API supports OAuth 2.0 for authenticating all API requests.
                                   |type: OAuth 2.0
                                   |settings:
                                   |  accessTokenUri:   https://esboam-dev.hhq.hud.dev/openam/oauth2/access_token
                                   |  authorizationGrants: [ client_credentials ]
                                   |  scopes: [ ADMIN ]""".stripMargin,
      "file://paged.raml"      -> """#%RAML 1.0 Trait
                               |queryParameters:
                               |  start:
                               |    type: number""".stripMargin,
      "file://user.raml"       -> """#%RAML 1.0 DataType
                              |properties:
                              |  name: string
                              |  age?: number""".stripMargin
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

  def buildServer(diagnosticNotifier: MockDiagnosticClientNotifier): LanguageServer = {
    val builder = new WorkspaceManagerFactoryBuilder(diagnosticNotifier, logger, Seq(rl))
    val dm      = builder.buildDiagnosticManagers()
    val factory = builder.buildWorkspaceManagerFactory()
    val b = new LanguageServerBuilder(factory.documentManager,
                                      factory.workspaceManager,
                                      factory.configurationManager,
                                      factory.resolutionTaskManager)
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

  test("Test clean validation with invalid vendor inclusions") {
    val diagnosticNotifier: MockDiagnosticClientNotifier = new MockDiagnosticClientNotifier
    withServer(buildServer(diagnosticNotifier)) { server =>
      val apiPath = s"file://api.raml"
      for {
        d <- requestCleanDiagnostic(server)(apiPath)
      } yield {
        server.shutdown()
        assert(d.exists(_.diagnostics.nonEmpty))
      }
    }
  }

  test("Clean diagnostic test, compare notification against clean") {
    val diagnosticNotifier: MockDiagnosticClientNotifier = new MockDiagnosticClientNotifier(5000)
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

  test("Clean diagnostic test - ASYNC20 vendor") {
    val diagnosticNotifier: MockDiagnosticClientNotifier = new MockDiagnosticClientNotifier(5000)
    withServer(buildServer(diagnosticNotifier)) { s =>
      val mainFilePath = s"file://async.yaml"

      val mainContent =
        """asyncapi: "2.0.0"
          |""".stripMargin

      for {
        _ <- openFileNotification(s)(mainFilePath, mainContent)
        _ <- diagnosticNotifier.nextCall
        d <- requestCleanDiagnostic(s)(mainFilePath)
      } yield {
        s.shutdown()
        assert(d.nonEmpty)
        assert(d.forall(_.profile.profile == ProfileNames.ASYNC20.profile))
      }
    }
  }
}
