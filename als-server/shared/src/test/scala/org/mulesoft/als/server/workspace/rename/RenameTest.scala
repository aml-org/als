package org.mulesoft.als.server.workspace.rename

import amf.client.remote.Content
import amf.internal.environment.Environment
import amf.internal.resource.ResourceLoader
import org.mulesoft.als.actions.rename.FindRenameLocations
import org.mulesoft.als.common.WorkspaceEditSerializer
import org.mulesoft.als.common.diff.{FileAssertionTest, Tests}
import org.mulesoft.als.common.dtoTypes.{Position => DtoPosition}
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.modules.actions.rename.RenameTools
import org.mulesoft.als.server.modules.workspace.CompilableUnit
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.protocol.configuration.AlsInitializeParams
import org.mulesoft.als.server.workspace.WorkspaceManager
import org.mulesoft.als.server.{LanguageServerBaseTest, LanguageServerBuilder, MockDiagnosticClientNotifier}
import org.mulesoft.lsp.configuration.TraceKind
import org.mulesoft.lsp.edit.{TextDocumentEdit, TextEdit, WorkspaceEdit}
import org.mulesoft.lsp.feature.common.{Position, Range, VersionedTextDocumentIdentifier}

import scala.concurrent.{ExecutionContext, Future}

class RenameTest extends LanguageServerBaseTest with FileAssertionTest with RenameTools {

  override implicit val executionContext: ExecutionContext =
    ExecutionContext.Implicits.global

  private val ws1 = Map(
    "file:///root/exchange.json" -> """{"main": "api.raml"}""",
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
    "file:///root/exchange.json" -> """{"main": "api.raml"}""",
    "file:///root/api.raml" ->
      """#%RAML 1.0
        |types:
        |  simpleType:
        |    properties:
        |      name: string
        |  ramlExpression:
        |    properties:
        |      reports: anotherType | anotherType[] | supertype
        |      prop: string
        |  anotherType:
        |    properties:
        |        name: anotherType[]""".stripMargin
  )
  private val ws3 = Map(
    "file:///root/exchange.json" -> """{"main": "api.raml"}""",
    "file:///root/api.raml" ->
      """#%RAML 1.0
        |types:
        |  person:
        |    properties:
        |      name: manager[]
        |      age?: person
        |  manager:
        |    properties:
        |      reports: person
        |""".stripMargin
  )

  private val ws4 = Map(
    "file:///root/exchange.json" -> """{"main": "api.raml"}""",
    "file:///root/api.raml" ->
      """#%RAML 1.0
        |title: rename
        |uses:
        |  lib: library.raml
        |/person:
        |  get:
        |    responses:
        |      200:
        |        body:
        |          application/xml:
        |            type: array
        |            items:
        |              type: lib.Person []""".stripMargin,
    "file:///root/library.raml" ->
      """#%RAML 1.0 Library
        |
        |types:
        |  Person:
        |    properties:
        |      name: string""".stripMargin
  )
  private val ws5 = Map(
    "file:///root/exchange.json" -> """{"main": "api.raml"}""",
    "file:///root/api.raml" -> """#%RAML 1.0
                           |title: test
                           |uses:
                           |    lib: lib.raml
                           |/e:
                           |    type: lib.rt
                           |    is:
                           |        - lib.tr:
                           |    securedBy:
                           |        - lib.sc
                           |    get:
                           |        body:
                           |            application/json:
                           |                type: lib.t
                           |                (lib.a): test annotation""".stripMargin,
    "file:///root/lib.raml" -> """#%RAML 1.0 Library
                           |types:
                           |    t:
                           |        type: object
                           |        properties:
                           |            n: string
                           |resourceTypes:
                           |    rt:
                           |        get:
                           |            description: test
                           |securitySchemes:
                           |    sc:
                           |        describedBy:
                           |traits:
                           |    tr:
                           |        description: trait test
                           |annotationTypes:
                           |    a:
                           |        type: string""".stripMargin
  )

  private val ws6 = Map(
    "file:///root/exchange.json" -> """{"main": "api.raml"}""",
    "file:///root/api.raml" -> """#%RAML 1.0
                                 |title: Test
                                 |types:
                                 |  T: !include type.raml
                                 |  Ts: T []""".stripMargin,
    "file:///root/type.raml" -> """#%RAML 1.0 DataType
                                  |uses:
                                  |  lib: lib.raml
                                  |
                                  |type: lib.A
                                  |""".stripMargin,
    "file:///root/lib.raml" -> """#%RAML 1.0 Library
                                 |
                                 |types:
                                 |  A:
                                 |    type: object
                                 |    properties:
                                 |        a: string""".stripMargin
  )
  val testSets: Map[String, TestEntry] = Map(
    "Test1" ->
      TestEntry(
        "file:///root/lib.raml",
        Position(2, 3),
        "trait1",
        ws1,
        createWSE(
          Seq(
            ("file:///root/api.raml",
             Seq(
               TextEdit(Range(Position(6, 10), Position(6, 12)), "trait1")
             )),
            ("file:///root/lib.raml",
             Seq(
               TextEdit(Range(Position(2, 2), Position(2, 4)), "trait1")
             ))
          ))
      ),
    "Test2" -> TestEntry(
      "file:///root/lib.raml",
      Position(5, 3),
      "type1",
      ws1,
      createWSE(
        Seq(
          ("file:///root/lib.raml",
           Seq(
             TextEdit(Range(Position(6, 5), Position(6, 6)), "type1"),
             TextEdit(Range(Position(7, 5), Position(7, 6)), "type1"),
             TextEdit(Range(Position(5, 2), Position(5, 3)), "type1")
           ))
        ))
    ),
    "raml-expression" -> TestEntry(
      "file:///root/api.raml",
      Position(9, 6),
      "type1",
      ws2,
      createWSE(
        Seq(
          ("file:///root/api.raml",
           Seq(
             TextEdit(Range(Position(7, 15), Position(7, 26)), "type1"),
             TextEdit(Range(Position(11, 14), Position(11, 25)), "type1"),
             TextEdit(Range(Position(7, 29), Position(7, 40)), "type1"),
             TextEdit(Range(Position(9, 2), Position(9, 13)), "type1")
           ))
        ))
    ),
    "test3" -> TestEntry(
      "file:///root/api.raml",
      Position(2, 3),
      "RENAMED",
      ws3,
      createWSE(
        Seq(
          ("file:///root/api.raml",
           Seq(
             TextEdit(Range(Position(8, 15), Position(8, 21)), "RENAMED"),
             TextEdit(Range(Position(5, 12), Position(5, 18)), "RENAMED"),
             TextEdit(Range(Position(2, 2), Position(2, 8)), "RENAMED")
           ))
        ))
    ),
    "Test5" -> TestEntry(
      "file:///root/library.raml",
      Position(3, 6),
      "RENAMED",
      ws4,
      createWSE(Seq(
        ("file:///root/library.raml",
        Seq(
          TextEdit(Range(Position(3, 2), Position(3, 8)), "RENAMED"),

        )),
        ("file:///root/api.raml",
          Seq(
            TextEdit(Range(Position(12, 24), Position(12, 30)), "RENAMED"),
          ))
      ))
    ),
    "Test6-lib-alias-rename" -> TestEntry(
      "file:///root/api.raml",
      Position(3, 6),
      "RENAMED",
      ws5,
      createWSE(Seq(
        ("file:///root/api.raml",
          Seq(
            TextEdit(Range(Position(7, 10), Position(7, 13)), "RENAMED"),
            TextEdit(Range(Position(5, 10), Position(5, 13)), "RENAMED"),
            TextEdit(Range(Position(14, 17), Position(14, 20)), "RENAMED"),
            TextEdit(Range(Position(13, 22), Position(13, 25)), "RENAMED"),
            TextEdit(Range(Position(9, 10), Position(9, 13)), "RENAMED"),
            TextEdit(Range(Position(3, 4), Position(3, 7)), "RENAMED")
          ))
      ))
    ),
    "Test7-lib-trait" -> TestEntry(
      "file:///root/lib.raml",
      Position(14, 5),
      "RENAMED",
      ws5,
      createWSE(Seq(
        ("file:///root/api.raml",
          Seq(
            TextEdit(Range(Position(7, 14), Position(7, 16)), "RENAMED")
          )),
        ("file:///root/lib.raml",
          Seq(
            TextEdit(Range(Position(14, 4), Position(14, 6)), "RENAMED")
          ))
      ))
    ),
    "Test7-lib-annotation" -> TestEntry(
      "file:///root/lib.raml",
      Position(17, 5),
      "RENAMED",
      ws5,
      createWSE(Seq(
        ("file:///root/api.raml",
          Seq(
            TextEdit(Range(Position(14, 21), Position(14, 22)), "RENAMED")
          )),
        ("file:///root/lib.raml",
          Seq(
            TextEdit(Range(Position(17, 4), Position(17, 5)), "RENAMED")
          ))
      ))
    ),
    "Test7-lib-type" -> TestEntry(
      "file:///root/lib.raml",
      Position(2, 5),
      "RENAMED",
      ws5,
      createWSE(Seq(
        ("file:///root/api.raml",
          Seq(
            TextEdit(Range(Position(13, 26), Position(13, 27)), "RENAMED")
          )),
        ("file:///root/lib.raml",
          Seq(
            TextEdit(Range(Position(2, 4), Position(2, 5)), "RENAMED")
          ))
      ))
    ),
    "Test7-lib-resourceType" -> TestEntry(
      "file:///root/lib.raml",
      Position(7, 5),
      "RENAMED",
      ws5,
      createWSE(Seq(
        ("file:///root/api.raml",
          Seq(
            TextEdit(Range(Position(5, 14), Position(5, 16)), "RENAMED")
          )),
        ("file:///root/lib.raml",
          Seq(
            TextEdit(Range(Position(7, 4), Position(7, 6)), "RENAMED")
          ))
      ))
    ),
    "Test7-lib-securityScheme" -> TestEntry(
      "file:///root/lib.raml",
      Position(11, 5),
      "RENAMED",
      ws5,
      createWSE(Seq(
        ("file:///root/api.raml",
          Seq(
            TextEdit(Range(Position(9, 14), Position(9, 16)), "RENAMED")
          )),
        ("file:///root/lib.raml",
          Seq(
            TextEdit(Range(Position(11, 4), Position(11, 6)), "RENAMED")
          ))
      ))
    ),
    "Array-expression" -> TestEntry(
      "file:///root/api.raml",
      Position(6, 6),
      "RENAMED",
      ws3,
      createWSE(
        Seq(
          ("file:///root/api.raml",
           Seq(
             TextEdit(Range(Position(4, 12), Position(4, 19)), "RENAMED"),
             TextEdit(Range(Position(6, 2), Position(6, 9)), "RENAMED")
           ))
        ))
    ),
    "DataType with Library" -> TestEntry(
      "file:///root/lib.raml",
      Position(3, 3),
      "RENAMED",
      ws6,
      createWSE(
        Seq(
          ("file:///root/type.raml",
            Seq(
              TextEdit(Range(Position(4, 10), Position(4, 11)), "RENAMED")
            )),
          ("file:///root/lib.raml",
            Seq(
              TextEdit(Range(Position(3, 2), Position(3, 3)), "RENAMED")
            ))
        ))
    )
  )

  private def createWSE(edits: Seq[(String, Seq[TextEdit])]): WorkspaceEdit =
    WorkspaceEdit(
      edits.groupBy(_._1).mapValues(_.flatMap(_._2)),
      edits.map(e => Left(TextDocumentEdit(VersionedTextDocumentIdentifier(e._1, None), e._2)))
    )

  testSets.foreach {
    case (name, testCase) =>
      test("No handler " + name) {
        for {
          (_, wsManager) <- buildServer(testCase.root, testCase.ws)
          cu             <- wsManager.getLastUnit(testCase.targetUri, "")
          position = DtoPosition(testCase.targetPosition)
          renames <- FindRenameLocations
            .changeDeclaredName(testCase.targetUri,
                                position,
                                testCase.newName,
                                wsManager.getAliases(testCase.targetUri, ""),
                                wsManager.getRelationships(testCase.targetUri, ""),
                                cu.yPartBranch, cu.unit)
          actual <- writeTemporaryFile(s"file://rename-test-$name-actual.yaml")(
            WorkspaceEditSerializer(renames).serialize())
          expected <- writeTemporaryFile(s"file://rename-test-$name-expected.yaml")(
            WorkspaceEditSerializer(testCase.result).serialize())
          r <- Tests.checkDiff(actual, expected)
        } yield r
      }
  }

  case class TestEntry(targetUri: String,
                       targetPosition: Position,
                       newName: String,
                       ws: Map[String, String],
                       result: WorkspaceEdit,
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

    val env = Environment().withLoaders(Seq(rs))

    val factory =
      new WorkspaceManagerFactoryBuilder(new MockDiagnosticClientNotifier, logger, env)
        .buildWorkspaceManagerFactory()
    val workspaceManager: WorkspaceManager = factory.workspaceManager
    val server =
      new LanguageServerBuilder(factory.documentManager,
                                workspaceManager,
                                factory.configurationManager,
                                factory.resolutionTaskManager)
        .addRequestModule(factory.renameManager)
        .build()

    server
      .initialize(AlsInitializeParams(None, Some(TraceKind.Off), rootUri = Some(root)))
      .andThen { case _ => server.initialized() }
      .map(_ => (server, workspaceManager))
  }

  override def rootPath: String = ???
}
