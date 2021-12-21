package org.mulesoft.als.server.workspace.highlights

import amf.core.client.common.remote.Content
import amf.core.client.scala.resource.ResourceLoader
import org.mulesoft.als.logger.EmptyLogger
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.protocol.configuration.AlsInitializeParams
import org.mulesoft.als.server.workspace.WorkspaceManager
import org.mulesoft.als.server.{LanguageServerBaseTest, LanguageServerBuilder, MockDiagnosticClientNotifier}
import org.mulesoft.amfintegration.amfconfiguration.EditorConfiguration
import org.mulesoft.lsp.configuration.TraceKind
import org.mulesoft.lsp.feature.RequestHandler
import org.mulesoft.lsp.feature.common.{Position, Range, TextDocumentIdentifier, TextDocumentItem}
import org.mulesoft.lsp.feature.highlight.{DocumentHighlight, DocumentHighlightKind, DocumentHighlightParams, DocumentHighlightRequestType}
import org.mulesoft.lsp.textsync.DidOpenTextDocumentParams
import org.scalatest.{AsyncFreeSpec, AsyncFreeSpecLike}

import scala.concurrent.{ExecutionContext, Future}

class DocumentHighlightTest extends AsyncFreeSpecLike {

  override implicit val executionContext: ExecutionContext =
    ExecutionContext.Implicits.global

  private val ws1 = Map(
    "file:///root/api.raml" ->
      """#%RAML 1.0
        |uses:
        |  lib: lib.raml
        |
        |/links:
        |  is:
        |    - lib.tr""".stripMargin,
    "file:///root/lib.raml" ->
      """#%RAML 1.0 Library
        |traits:
        |  tr:
        |    description: example trait
        |types:
        |  A: string
        |  C: A
        |  D: A""".stripMargin
  )

  private val ws2 = Map(
    "file:///root/api.json" ->
      """{
        |  "swagger": "2.0",
        |  "definitions": {
        |      "User": {
        |        "$ref": "test/properties.json"
        |      }
        |  },
        |  "paths": {
        |    "/get": {
        |        "get": {
        |            "parameters": [
        |                {
        |                  "in": "body",
        |                  "name": "user",
        |                  "schema": {
        |                      "$ref": "#/definitions/User"
        |                  }
        |                }
        |            ]
        |          }
        |        }
        |    }
        |}""".stripMargin,
    "file:///root/test/properties.json" ->
      """{
        |    "properties": {
        |            "username": {
        |              "type": "string"
        |            }
        |    }
        |}""".stripMargin
  )

  private val ws3 = Map(
    "file:///root/api.raml" ->
      """#%RAML 1.0
        |title: api
        |resourceTypes:
        |  details: !include resourceTypes/details.raml
        |  export: !include resourceTypes/export.raml
        |
        |/path:
        |  /details:
        |    type: details
        |
        |  /export:
        |    type: export""".stripMargin,
    "file:///root/resourceTypes/details.raml" ->
      """#%RAML 1.0 ResourceType
        |  responses:
        |    200:
        |      body:
        |        application/json:
        |          type: array
        |          items:
        |            type: string""".stripMargin,
    "file:///root/resourceTypes/export.raml" ->
      """get:
        |  responses:
        |    200:
        |      body:
        |        application/json:
        |          type: array
        |          items:
        |            type: string""".stripMargin,
  )
  private val ws4 = Map(
    "file:///root/api.raml" ->
      """#%RAML 1.0
        |title: test
        |securitySchemes:
        |  oauth_2_0: !include securitySchemes/oauth_2_0.raml
        |  tokenSchema: !include securitySchemes/tokenSchema.raml
        |
        |/get:
        |  description: |
        |    This endpoint retrieves all updated and newly created Project Assignments.
        |  securedBy: tokenSchema
        |              """.stripMargin,
    "file:///root/securitySchemes/oauth_2_0.raml" ->
      """#%RAML 1.0 SecurityScheme
        |description: ""
        |type: OAuth 2.0
        |describedBy:
        |  headers:
        |    Authorization:
        |      description: |
        |        Used to send a valid OAuth 2 access token. The token must be preceeded by the word "Bearer".
        |      example: Bearer _token_
        |settings:
        |  authorizationUri: https://login.salesforce.com/services/oauth2/authorize
        |  accessTokenUri: https://login.salesforce.com/services/oauth2/token
        |  authorizationGrants: [ authorization_code ]""".stripMargin,
    "file:///root/securitySchemes/tokenSchema.raml" ->
      """#%RAML 1.0 SecurityScheme
        |type: x-custom
        |describedBy:
        |  headers:
        |    Authorization:
        |      description: |
        |        Used to send a valid OAuth 2 access token. The token must be preceeded by the word "Bearer".
        |      example: Bearer _token_""".stripMargin,
  )

  val testSets: Set[TestEntry] = Set(
    TestEntry(
      "file:///root/lib.raml",
      Position(5, 3),
      "ws1",
      ws1,
      Set(
        DocumentHighlight(Range(Position(6, 5), Position(6, 6)), DocumentHighlightKind.Text),
        DocumentHighlight(Range(Position(7, 5), Position(7, 6)), DocumentHighlightKind.Text)
      )
    ),
    TestEntry(
      "file:///root/api.json",
      Position(3, 9),
      "ws2",
      ws2,
      Set(
        DocumentHighlight(Range(Position(15, 30), Position(15, 50)), DocumentHighlightKind.Text)
      )
    ),
    TestEntry(
      "file:///root/api.raml",
      Position(4, 5),
      "ws3",
      ws3,
      Set(
        DocumentHighlight(Range(Position(11, 10), Position(11, 16)), DocumentHighlightKind.Text)
      )
    ),
    TestEntry(
      "file:///root/api.raml",
      Position(4,5),
      "ws4",
      ws4,
      Set(
        DocumentHighlight(Range(Position(9, 13), Position(9, 24)), DocumentHighlightKind.Text)
      )
    ),
    TestEntry(
      "file:///root/api.raml",
      Position(3,5),
      "ws4",
      ws4,
      Set()
    )
  )

  "Document Highlight tests" - {
    testSets.toSeq.map { test =>
      s"Document highlight ${test.targetUri} @ ${test.targetPosition} (${test.wsName})" in {
        for {
          (server, _) <- buildServer(test.root, test.ws)
          _ <- server.textDocumentSyncConsumer.didOpen(
            DidOpenTextDocumentParams(
              TextDocumentItem(
                test.targetUri,
                "",
                0,
                test.ws(test.targetUri)
              )))
          highlights <- {
            val dhHandler: RequestHandler[DocumentHighlightParams, Seq[DocumentHighlight]] =
              server.resolveHandler(DocumentHighlightRequestType).get
            dhHandler(DocumentHighlightParams(TextDocumentIdentifier(test.targetUri), test.targetPosition))
          }
        } yield {
          assert(highlights.toSet == test.result)
        }
      }
    }
  }

  case class TestEntry(targetUri: String,
                       targetPosition: Position,
                       wsName: String,
                       ws: Map[String, String],
                       result: Set[DocumentHighlight],
                       root: String = "file:///root")

  def buildServer(root: String, ws: Map[String, String]): Future[(LanguageServer, WorkspaceManager)] = {
    val rs = new ResourceLoader {
      override def fetch(resource: String): Future[Content] =
        ws.get(resource)
          .map(c => new Content(c, resource))
          .map(Future.successful)
          .getOrElse(Future.failed(new Exception("File not found on custom ResourceLoader")))
      override def accepts(resource: String): Boolean =
        ws.keySet.contains(resource)
    }

      val factory =
        new WorkspaceManagerFactoryBuilder(new MockDiagnosticClientNotifier, EmptyLogger, EditorConfiguration.withPlatformLoaders(Seq(rs)))
          .buildWorkspaceManagerFactory()
    val workspaceManager: WorkspaceManager = factory.workspaceManager
    val server =
      new LanguageServerBuilder(factory.documentManager,
                                workspaceManager,
                                factory.configurationManager,
                                factory.resolutionTaskManager)
        .addRequestModule(factory.documentHighlightManager)
        .build()

    server
      .initialize(AlsInitializeParams(None, Some(TraceKind.Off), rootUri = Some(root)))
      .andThen { case _ => server.initialized() }
      .map(_ => (server, workspaceManager))

  }

}
