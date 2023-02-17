package org.mulesoft.als.suggestions.test.oas30

import amf.apicontract.client.scala.APIConfiguration
import amf.core.client.scala.AMFGraphConfiguration
import org.mulesoft.als.configuration.ProjectConfiguration
import org.mulesoft.als.suggestions.test.{BaseSuggestionsForTest, TestProjectConfigurationState}
import org.mulesoft.amfintegration.amfconfiguration.{ALSConfigurationState, EditorConfigurationState}
import org.mulesoft.lsp.feature.completion.CompletionItem

import scala.concurrent.{ExecutionContext, Future}

class Oas30JsonSchemaRefSuggestionTest extends AsyncFunSuite with BaseSuggestionsForTest {

  def rootPath: String = "file://als-suggestions/shared/src/test/resources/test/oas30/json-schema/"
  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  test("test oas3 including cached json schema should only suggest definitions") {
    suggest("api.yaml", "schema.json").map { ci =>
      ci.length shouldBe 2
      assert(ci.map(_.label).contains("definitions/state"))
    }
  }

  test("test oas3 including cached json schema should only suggest definitions without the declaration key") {
    suggest("api-definitions.yaml", "schema.json").map { ci =>
      ci.length shouldBe 2
      assert(ci.map(_.label).contains("state"))
    }
  }

  test("test oas3 including cached json schema draft 2019 should only suggest definitions") {
    suggest("api-2019.yaml", "schema-2019.json").map { ci =>
      ci.length shouldBe 2
      assert(ci.flatMap(_.textEdit).map(_.left.get).map(_.newText).forall(_.contains("/definitions/")))
    }
  }

  test("test oas3 including cached json schema draft 2019 should only suggest defs") {
    suggest("api-2019-defs.yaml", "schema-2019-defs.json").map { ci =>
      ci.length shouldBe 2
      assert(ci.flatMap(_.textEdit).map(_.left.get).map(_.newText).forall(_.contains("/$defs/")))
    }
  }

  private def suggest(api: String, schemaName: String): Future[Seq[CompletionItem]] = {
    for {
      schema <- APIConfiguration.APIWithJsonSchema().baseUnitClient().parse(rootPath + schemaName).map(_.baseUnit)
      c      <- platform.fetchContent(rootPath + api, AMFGraphConfiguration.predefined())
      ci <- {
        val projectState = TestProjectConfigurationState(
          Nil,
          new ProjectConfiguration(
            rootPath,
            Some(api),
            Set(rootPath + schemaName),
            Set.empty,
            Set.empty,
            Set.empty
          ),
          schema
        )
        val state = ALSConfigurationState(EditorConfigurationState.empty, projectState, None)

        suggestFromFile(c.stream.toString, rootPath + api, "*", state)
      }
    } yield ci
  }
}
