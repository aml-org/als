package org.mulesoft.als.actions.codeactions

import org.mulesoft.als.actions.codeactions.plugins.declarations.delete.DeleteDeclaredNodeCodeAction
import org.mulesoft.als.common.WorkspaceEditSerializer
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.als.common.edits.codeaction.AbstractCodeAction
import org.mulesoft.amfintegration.amfconfiguration.AmfConfigurationWrapper
import org.scalatest.Assertion
import org.yaml.model.YDocument
import org.yaml.model.YDocument.{EntryBuilder, PartBuilder}
import org.yaml.render.YamlRender

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DeleteDeclaredNodeTest extends BaseCodeActionTests {

  implicit def tupleToPosition(lc: (Int, Int)): Position = {
    Position(lc._1, lc._2)
  }

  implicit def tuplesToPositionRange(lc: ((Int, Int), (Int, Int))): PositionRange = {
    PositionRange(lc._1, lc._2)
  }

  behavior of "Delete declared node"

  it should s" delete nodes at raml api" in {
    val cases = Seq(
      DeleteDeclaredNodeRequestCase("annotation type", ((4, 6), (4, 10))),
      DeleteDeclaredNodeRequestCase("type", ((15, 6), (16, 9))),
      DeleteDeclaredNodeRequestCase("type expression", ((17, 8), (17, 9))),
      DeleteDeclaredNodeRequestCase("resource type", ((28, 7), (28, 14))),
      DeleteDeclaredNodeRequestCase("trait", ((36, 8), (36, 8))),
      DeleteDeclaredNodeRequestCase("security scheme", ((44, 8), (44, 10)))
    )

    val elementUri = "delete-declaration/raml/api.raml"
    val goldenUri  = "delete-declaration/raml/expected/api.yaml"
    runCase(elementUri, goldenUri, cases)
  }

  it should s"delete nodes at raml without brothers" in {
    val cases = Seq(
      DeleteDeclaredNodeRequestCase("annotation type", ((4, 6), (4, 10))),
      DeleteDeclaredNodeRequestCase("type", ((8, 6), (8, 9))),
      DeleteDeclaredNodeRequestCase("resource type", ((12, 7), (12, 14))),
      DeleteDeclaredNodeRequestCase("trait", ((18, 8), (18, 8))),
      DeleteDeclaredNodeRequestCase("security scheme", ((23, 8), (23, 10)))
    )

    val elementUri = "delete-declaration/raml/without-brothers.raml"
    val goldenUri  = "delete-declaration/raml/expected/without-brothers.yaml"
    runCase(elementUri, goldenUri, cases)
  }

  it should s"delete nodes at library raml" in {
    val cases = Seq(
      DeleteDeclaredNodeRequestCase("annotation type", ((3, 6), (3, 10))),
      DeleteDeclaredNodeRequestCase("type", ((14, 6), (14, 9))),
      DeleteDeclaredNodeRequestCase("resource type", ((21, 7), (21, 14))),
      DeleteDeclaredNodeRequestCase("trait", ((29, 8), (29, 8))),
      DeleteDeclaredNodeRequestCase("security scheme", ((37, 8), (37, 10)))
    )

    val elementUri = "delete-declaration/raml/from-library.raml"
    val goldenUri  = "delete-declaration/raml/expected/library.yaml"
    runCase(elementUri, goldenUri, cases, Some("delete-declaration/raml/library.raml"))
  }

  it should s"delete nodes at api oas 2.0" in {
    val cases = Seq(
      DeleteDeclaredNodeRequestCase("definition", ((10, 5), (10, 10))),
      DeleteDeclaredNodeRequestCase("parameter", ((16, 6), (16, 9))),
      DeleteDeclaredNodeRequestCase("response", ((22, 7), (23, 14))),
      DeleteDeclaredNodeRequestCase("security definition", ((28, 8), (28, 8)))
    )

    val elementUri = "delete-declaration/oas20/api.yaml"
    val goldenUri  = "delete-declaration/oas20/expected/api.yaml"
    runCase(elementUri, goldenUri, cases)
  }

  it should s"delete nodes at api without brothers oas 2.0" in {
    val cases = Seq(
      DeleteDeclaredNodeRequestCase("definition", ((3, 5), (3, 10))),
      DeleteDeclaredNodeRequestCase("parameter", ((7, 6), (7, 9))),
      DeleteDeclaredNodeRequestCase("response", ((11, 7), (11, 14))),
      DeleteDeclaredNodeRequestCase("security definition", ((15, 8), (15, 8)))
    )

    val elementUri = "delete-declaration/oas20/api-without-brothers.yaml"
    val goldenUri  = "delete-declaration/oas20/expected/api-without-brothers.yaml"
    runCase(elementUri, goldenUri, cases)
  }

  it should s"delete nodes at api oas 3.0" in {
    val cases = Seq(
      DeleteDeclaredNodeRequestCase("schema", ((4, 7), (4, 12))),
      DeleteDeclaredNodeRequestCase("responses", ((16, 9), (16, 10))),
      DeleteDeclaredNodeRequestCase("parameter", ((23, 7), (23, 12))),
      DeleteDeclaredNodeRequestCase("example", ((26, 8), (26, 8))),
      DeleteDeclaredNodeRequestCase("requestBody", ((32, 8), (32, 8))),
      DeleteDeclaredNodeRequestCase("headers", ((40, 8), (40, 8))),
      DeleteDeclaredNodeRequestCase("securityScheme", ((44, 5), (44, 5))),
      DeleteDeclaredNodeRequestCase("link", ((51, 8), (51, 8))),
      DeleteDeclaredNodeRequestCase("callback", ((54, 8), (54, 8)))
    )

    val elementUri = "delete-declaration/oas30/api.yaml"
    val goldenUri  = "delete-declaration/oas30/expected/api.yaml"
    runCase(elementUri, goldenUri, cases)
  }

  it should s"delete nodes at api without brothers oas 3.0" in {
    val cases = Seq(
      DeleteDeclaredNodeRequestCase("schema", ((4, 7), (4, 12))),
      DeleteDeclaredNodeRequestCase("responses", ((12, 9), (12, 10))),
      DeleteDeclaredNodeRequestCase("parameter", ((15, 7), (15, 12))),
      DeleteDeclaredNodeRequestCase("example", ((18, 8), (18, 8))),
      DeleteDeclaredNodeRequestCase("requestBody", ((22, 8), (22, 8))),
      DeleteDeclaredNodeRequestCase("headers", ((26, 8), (26, 8))),
      DeleteDeclaredNodeRequestCase("securityScheme", ((30, 5), (30, 5))),
      DeleteDeclaredNodeRequestCase("link", ((33, 8), (33, 8))),
      DeleteDeclaredNodeRequestCase("callback", ((54, 8), (54, 8)))
    )

    val elementUri = "delete-declaration/oas30/api-without-brothers.yaml"
    val goldenUri  = "delete-declaration/oas30/expected/api-without-brothers.yaml"
    runCase(elementUri, goldenUri, cases)
  }

  it should s"delete node at api one component oas 3.0" in {
    val cases = Seq(DeleteDeclaredNodeRequestCase("schema", ((4, 7), (4, 12))))

    val elementUri = "delete-declaration/oas30/one-component.yaml"
    val goldenUri  = "delete-declaration/oas30/expected/one-component.yaml"
    runCase(elementUri, goldenUri, cases)
  }

  it should s"not delete node response at api oas 3.0" in {
    val cases = Seq(DeleteDeclaredNodeRequestCase("200", ((7, 11), (7, 11))))

    val elementUri = "delete-declaration/oas30/response.yaml"
    val goldenUri  = "delete-declaration/oas30/expected/response.yaml"
    runCase(elementUri, goldenUri, cases)
  }

  it should s"delete nodes at asyncapi 2.0" in {
    val cases = Seq(
      DeleteDeclaredNodeRequestCase("schema", ((6, 9), (6, 11))),
      DeleteDeclaredNodeRequestCase("messages", ((9, 9), (9, 10))),
      DeleteDeclaredNodeRequestCase("security schemes", ((18, 7), (18, 12))),
      DeleteDeclaredNodeRequestCase("parameters", ((21, 8), (21, 8))),
      DeleteDeclaredNodeRequestCase("corrrelation ids", ((30, 8), (30, 8))),
      DeleteDeclaredNodeRequestCase("operation traits", ((37, 8), (37, 8))),
      DeleteDeclaredNodeRequestCase("message traits", ((40, 5), (40, 5))),
      DeleteDeclaredNodeRequestCase("server bindings", ((47, 8), (47, 8))),
      DeleteDeclaredNodeRequestCase("channel bindings", ((50, 8), (50, 8))),
      DeleteDeclaredNodeRequestCase("operation bindings", ((57, 8), (57, 8))),
      DeleteDeclaredNodeRequestCase("message bindings", ((62, 8), (62, 8)))
    )

    val elementUri = "delete-declaration/asyncapi/api.yaml"
    val goldenUri  = "delete-declaration/asyncapi/expected/api.yaml"
    runCase(elementUri, goldenUri, cases)
  }

  it should s"delete nodes at api without brothers asyncapi 2.0" in {
    val cases = Seq(
      DeleteDeclaredNodeRequestCase("schema", ((4, 9), (4, 11))),
      DeleteDeclaredNodeRequestCase("messages", ((7, 9), (7, 10))),
      DeleteDeclaredNodeRequestCase("security schemes", ((11, 7), (11, 12))),
      DeleteDeclaredNodeRequestCase("parameters", ((14, 8), (14, 8))),
      DeleteDeclaredNodeRequestCase("corrrelation ids", ((19, 8), (19, 8))),
      DeleteDeclaredNodeRequestCase("operation traits", ((22, 8), (22, 8))),
      DeleteDeclaredNodeRequestCase("message traits", ((25, 5), (25, 5))),
      DeleteDeclaredNodeRequestCase("server bindings", ((28, 8), (28, 8))),
      DeleteDeclaredNodeRequestCase("channel bindings", ((31, 8), (31, 8))),
      DeleteDeclaredNodeRequestCase("operation bindings", ((34, 8), (34, 8))),
      DeleteDeclaredNodeRequestCase("message bindings", ((37, 8), (37, 8)))
    )

    val elementUri = "delete-declaration/asyncapi/api-without-brothers.yaml"
    val goldenUri  = "delete-declaration/asyncapi/expected/api-without-brothers.yaml"
    runCase(elementUri, goldenUri, cases)
  }

  it should s"delete node at api one component async 2.0" in {
    val cases = Seq(DeleteDeclaredNodeRequestCase("schema", ((4, 7), (4, 12))))

    val elementUri = "delete-declaration/asyncapi/one-component.yaml"
    val goldenUri  = "delete-declaration/asyncapi/expected/one-component.yaml"
    runCase(elementUri, goldenUri, cases)
  }

  it should s"delete node at aml document" in {
    val cases = Seq(
      DeleteDeclaredNodeRequestCase("dec1", ((2, 4), (2, 4))),
      DeleteDeclaredNodeRequestCase("dec2", ((5, 4), (5, 4))),
      DeleteDeclaredNodeRequestCase("dec3", ((8, 4), (8, 4)))
    )

    val elementUri = "delete-declaration/aml/basic.yaml"
    val goldenUri  = "delete-declaration/aml/expected/basic.yaml"
    val dialectUri = Some("delete-declaration/aml/dialect.yaml")
    runCase(elementUri, goldenUri, cases, dialectUri = dialectUri)
  }

  it should s"delete node at aml without brothers document" in {
    val cases = Seq(DeleteDeclaredNodeRequestCase("dec1", ((3, 4), (3, 4))))

    val elementUri = "delete-declaration/aml/basic-without-brothers.yaml"
    val goldenUri  = "delete-declaration/aml/expected/basic-without-brothers.yaml"
    val dialectUri = Some("delete-declaration/aml/dialect.yaml")
    runCase(elementUri, goldenUri, cases, dialectUri = dialectUri)
  }

  case class DeleteResultCase(name: String, actions: Seq[AbstractCodeAction]) {
    def ast(e: EntryBuilder): Unit = {
      e.entry(name, p => p.list(lp => actions.foreach(a => buildAction(a, lp))))
    }

    private def buildAction(a: AbstractCodeAction, p: PartBuilder): Unit = {
      p.obj { eb =>
        a.edit.foreach { e =>
          WorkspaceEditSerializer(e.toWorkspaceEdit(true)).entryChanges(eb)
        }
      }
    }
  }

  private def runCase(file: String,
                      golden: String,
                      cases: Seq[DeleteDeclaredNodeRequestCase],
                      activeFile: Option[String] = None,
                      dialectUri: Option[String] = None): Future[Assertion] = {

    for {
      content <- getNodeDeletions(file, cases, activeFile, dialectUri)
      out     <- writeTemporaryFile(golden)(content)
      r       <- assertDifferences(out, relativeUri(golden))
    } yield r
  }

  private def getNodeDeletions(file: String,
                               cases: Seq[DeleteDeclaredNodeRequestCase],
                               activeFile: Option[String],
                               defineBy: Option[String] = None) = {

    val amfConfiguration = AmfConfigurationWrapper()

    parseElement(file, defineBy, amfConfiguration).map(r => buildPreParam(file, r)).flatMap { pr =>
      val results: Seq[Future[DeleteResultCase]] = cases.map { deletecase =>
        val params = pr.buildParam(deletecase.positionRange, activeFile, amfConfiguration)
        val plugin = DeleteDeclaredNodeCodeAction(params)
        val r: Future[Seq[AbstractCodeAction]] =
          if (plugin.isApplicable) plugin.run(params) else Future.successful(Seq.empty)
        r.map(ca => DeleteResultCase(deletecase.name, ca))
      }
      Future.sequence(results).map(serializeResults)
    }
  }

  private def serializeResults(l: Seq[DeleteResultCase]): String = {
    val document = YDocument.objFromBuilder(eb => {
      l.foreach { r =>
        r.ast(eb)
      }
    })
    YamlRender.render(document)
  }
}

case class DeleteDeclaredSpecSuit(file: String, cases: Seq[DeleteDeclaredNodeRequestCase], extension: String = "yaml")

case class DeleteDeclaredNodeRequestCase(name: String, positionRange: PositionRange)
