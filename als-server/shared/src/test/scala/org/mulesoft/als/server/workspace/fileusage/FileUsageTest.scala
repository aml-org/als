package org.mulesoft.als.server.workspace.fileusage

import amf.core.client.scala.AMFGraphConfiguration
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
      "api.raml",
      Map(
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
      "api.raml",
      Map(
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
      "api.yaml",
      Map(
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
            (_, wsManager) <- buildServer(test.root, test.ws, test.mainFile)
            links <- FindFileUsages.getUsages(test.searchedUri, wsManager.getAllDocumentLinks(test.searchedUri, ""))
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
          runTest(test.root, test.mainFile, test.ws, test.searchedUri, test.result)
        }
      }
      .map(r => assert(r.forall(_ == Succeeded)))
  }

  // ignored until amf adds links instead of inlined references (W-11461036)
  ignore("Json Schema reference") {
    val path1 = "file:///root/json-schema.json"
    val path2 = "file:///root/json-schema2.json"

    for {
      cont1 <- platform
        .fetchContent(filePath("json-schema/basic-schema.json"), AMFGraphConfiguration.predefined())
        .map(_.stream.toString)
      cont2 <- platform
        .fetchContent(filePath("json-schema/basic-schema2.json"), AMFGraphConfiguration.predefined())
        .map(_.stream.toString)
      (_, wsManager) <- {
        val ws = Map(
          path1 -> cont1,
          path2 -> cont2
        )
        buildServer(path1, ws, path1)
      }
      links <- FindFileUsages.getUsages(path2, wsManager.getAllDocumentLinks(path2, ""))
    } yield {
      val goldenLink = Seq(Location(path1, Range(Position(7, 24), Position(7, 42))))
      assert(links == goldenLink)
    }
  }

  case class TestEntry(
      searchedUri: String,
      mainFile: String,
      ws: Map[String, String],
      result: Set[Location],
      root: String = "file:///root/"
  )
}
