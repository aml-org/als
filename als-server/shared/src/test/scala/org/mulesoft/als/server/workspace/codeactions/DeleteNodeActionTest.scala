package org.mulesoft.als.server.workspace.codeactions

import org.mulesoft.als.actions.codeactions.plugins.declarations.delete.DeleteDeclaredNodeCodeAction
import org.mulesoft.als.common.diff.WorkspaceEditsTest
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.lsp.feature.codeactions.{CodeActionContext, CodeActionParams, CodeActionRequestType}
import org.mulesoft.lsp.feature.common.{Location, Position, Range, TextDocumentIdentifier}

import scala.concurrent.{ExecutionContext, Future}

class DeleteNodeActionTest extends CodeActionsTest with WorkspaceEditsTest {

  override implicit val executionContext: ExecutionContext =
    ExecutionContext.Implicits.global

  val traitMap = WorkspaceEntry(
    Map(
      "file:///root/api.raml" ->
        """#%RAML 1.0
        |title: test
        |uses:
        |    lib: l.raml
        |/e:
        |    is:
        |        - lib.tr
        |        - lib.tr2
        |/e2:
        |    is: [lib.tr, lib.tr2]""".stripMargin,
      "file:///root/l.raml" ->
        """#%RAML 1.0 Library
        |traits:
        |    tr:
        |        description: test
        |    tr2:
        |        description: test""".stripMargin
    ),
    Some("api.raml")
  )

  val traitMapResult = Map(
    "file:///root/api.raml" ->
      """#%RAML 1.0
        |title: test
        |uses:
        |    lib: l.raml
        |/e:
        |    is:
        |        - 
        |        - lib.tr2
        |/e2:
        |    is: [, lib.tr2]""".stripMargin,
    "file:///root/l.raml" ->
      """#%RAML 1.0 Library
        |traits:
        |    tr2:
        |        description: test""".stripMargin
  )

  val typeMap = WorkspaceEntry(
    Map(
      "file:///root/api.raml" ->
        """#%RAML 1.0
        |title: test
        |
        |uses:
        |    lib: l.raml""".stripMargin,
      "file:///root/l.raml" ->
        """#%RAML 1.0 Library
          |types:
          |    t1: string
          |    t2: t1 | number""".stripMargin
    ),
    Some("api.raml")
  )

  val typeMapResult = Map(
    "file:///root/api.raml" ->
      """#%RAML 1.0
        |title: test
        |
        |uses:
        |    lib: l.raml""".stripMargin,
    "file:///root/l.raml" ->
      """#%RAML 1.0 Library
        |types:
        |    t2:  | number""".stripMargin
  )

  val te1: TestEntry =
    TestEntry(traitMap, Location("file:///root/l.raml", Range(Position(2, 5), Position(3, 15))), traitMapResult)

  val te2: TestEntry =
    TestEntry(typeMap, Location("file:///root/l.raml", Range(Position(2, 5), Position(2, 14))), typeMapResult)

  val testSets: Set[TestEntry] = Set(te1, te2)

  test("Delete node reference") {
    Future
      .sequence(testSets.map { test =>
        for {
          (server, _) <- buildServer(test.root, test.workspace)
          result      <- deleteNode(server, test)
        } yield {
          assert(result.size == test.result.size)
          result.foreach {
            case (k, v) => assert(v == test.result.getOrElse(k, ""))
          }
        }
      })
      .map(_ => succeed)
  }

  private def deleteNode(server: LanguageServer, testEntry: TestEntry): Future[Map[String, String]] =
    server
      .resolveHandler(CodeActionRequestType)
      .map { handler =>
        handler(
          CodeActionParams(TextDocumentIdentifier(testEntry.location.uri),
                           testEntry.location.range,
                           CodeActionContext(Nil, None)))
          .map { all =>
            all
              .find(_.title == DeleteDeclaredNodeCodeAction.title)
              .map(ca =>
                applyEdits(ca.edit.getOrElse(throw new Exception("Empty workspace edit")),
                           testEntry.workspace.resources))
              .getOrElse(Map.empty)
          }
      }
      .getOrElse(Future.failed(new Exception("No handler found for CodeAction")))

  case class TestEntry(workspace: WorkspaceEntry,
                       location: Location,
                       result: Map[String, String],
                       root: String = "file:///root/")

}
