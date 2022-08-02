package org.mulesoft.als.server.modules.diagnostic
import amf.core.client.common.remote.Content
import amf.core.client.scala.resource.ResourceLoader
import amf.custom.validation.client.scala.BaseProfileValidatorBuilder
import org.mulesoft.als.server.client.scala.LanguageServerBuilder
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.{LanguageServerBaseTest, MockDiagnosticClientNotifier}
import org.mulesoft.amfintegration.amfconfiguration.EditorConfiguration

import scala.concurrent.Future

class DiagnosticServerImpl extends LanguageServerBaseTest with DiagnosticServer {

  override def rootPath: String = "diagnostics"

  val rl: ResourceLoader = new ResourceLoader {

    private val files: Map[String, String] = Map(
      "file://file%20with%20spaces.raml" ->
        """#%RAML 1.0
          |description: this is a RAML without title""".stripMargin,
      "file://api.raml" -> """#%RAML 0.8
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
      "file://oauth_2_0.raml" -> """#%RAML 1.0 SecurityScheme
                                   |description: |
                                   |  This API supports OAuth 2.0 for authenticating all API requests.
                                   |type: OAuth 2.0
                                   |settings:
                                   |  accessTokenUri:   https://esboam-dev.hhq.hud.dev/openam/oauth2/access_token
                                   |  authorizationGrants: [ client_credentials ]
                                   |  scopes: [ ADMIN ]""".stripMargin,
      "file://paged.raml" -> """#%RAML 1.0 Trait
                               |queryParameters:
                               |  start:
                               |    type: number""".stripMargin,
      "file://user.raml" -> """#%RAML 1.0 DataType
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

  def buildServer(
                   diagnosticNotifier: MockDiagnosticClientNotifier,
                   validator: Option[BaseProfileValidatorBuilder] = None
                 ): LanguageServer = {
    val global  = EditorConfiguration.withPlatformLoaders(Seq(rl))
    val builder = new WorkspaceManagerFactoryBuilder(diagnosticNotifier, logger, global)
    val dm      = builder.buildDiagnosticManagers(validator)
    val factory = builder.buildWorkspaceManagerFactory()
    val b = new LanguageServerBuilder(
      factory.documentManager,
      factory.workspaceManager,
      factory.configurationManager,
      factory.resolutionTaskManager
    )
    b.addRequestModule(factory.cleanDiagnosticManager)
    dm.foreach(m => b.addInitializableModule(m))
    b.build()
  }
}
