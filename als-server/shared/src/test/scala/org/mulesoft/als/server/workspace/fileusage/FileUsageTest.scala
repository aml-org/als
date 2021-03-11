package org.mulesoft.als.server.workspace.fileusage

import org.mulesoft.als.actions.fileusage.FindFileUsages
import org.mulesoft.lsp.feature.common.{Location, Position, Range}
import org.scalatest.Succeeded

import scala.concurrent.{ExecutionContext, Future}

class FileUsageTest extends ServerFileUsageTest {

  override implicit val executionContext: ExecutionContext =
    ExecutionContext.Implicits.global

  val testSets: Set[TestEntry] = Set(
    TestEntry(
      "file:///root/t.raml",
      Map(
        "file:///root/exchange.json" -> """{"main": "api.raml"}""",
        "file:///root/api.raml" ->
          """#%RAML 1.0
            |types:
            |  t: !include t.raml""".stripMargin,
        "file:///root/t.raml" ->
          """#%RAML 1.0 DataType
            |type: string""".stripMargin
      ),
      Set(Location("file:///root/api.raml", Range(Position(2, 14), Position(2, 20))))
    ),
    TestEntry(
      "file:///root/l.raml",
      Map(
        "file:///root/exchange.json" -> """{"main": "api.raml"}""",
        "file:///root/api.raml" ->
          """#%RAML 1.0
          |uses:
          |  l: l.raml""".stripMargin,
        "file:///root/l.raml" ->
          """#%RAML 1.0 Library
          |types
          |  t: string""".stripMargin
      ),
      Set(Location("file:///root/api.raml", Range(Position(2, 5), Position(2, 11))))
    ),
    TestEntry(
      "file:///root/t.yaml",
      Map(
        "file:///root/exchange.json" -> """{"main": "api.yaml"}""",
        "file:///root/api.yaml" ->
          """asyncapi: "2.0.0"
            |
            |components:
            |  schemas:
            |    mySchema:
            |      $ref: "t.yaml#/mySchema"""".stripMargin,
        "file:///root/t.yaml" ->
          """mySchema:
            |  type: object
            |  properties:
            |    id: string""".stripMargin
      ),
      Set(Location("file:///root/api.yaml", Range(Position(5, 13), Position(5, 19))))
    )
  )

  test("No handler") {
    for {
      results <- Future.sequence {
        testSets.map { test =>
          for {
            (_, wsManager) <- buildServer(test.root, test.ws)
            links          <- FindFileUsages.getUsages(test.searchedUri, wsManager.getAllDocumentLinks(test.searchedUri, ""))
          } yield {
            (links, test.result)
          }
        }
      }
    } yield {
      assert(results.forall(t => t._1.toSet == t._2))
    }
  }

  test("Through handler") {
    Future
      .sequence {
        testSets.map { test =>
          runTest(test.root, test.ws, test.searchedUri, test.result)
        }
      }
      .map(r => assert(r.forall(_ == Succeeded)))
  }

  case class TestEntry(searchedUri: String,
                       ws: Map[String, String],
                       result: Set[Location],
                       root: String = "file:///root/")
}
