package org.mulesoft.als.suggestions.test.oas30

import amf.apicontract.client.scala.APIConfiguration
import amf.core.client.scala.AMFGraphConfiguration
import org.mulesoft.als.configuration.ProjectConfiguration
import org.mulesoft.als.suggestions.test.{BaseSuggestionsForTest, TestProjectConfigurationState}
import org.mulesoft.amfintegration.amfconfiguration.{ALSConfigurationState, EditorConfigurationState}
import org.mulesoft.lsp.feature.completion.CompletionItem
import org.scalatest.AsyncFunSuite
import org.scalatest.Matchers.convertToAnyShouldWrapper

import scala.concurrent.{ExecutionContext, Future}

class Oas30JsonSchemaRefSuggestionTest extends AsyncFunSuite with BaseSuggestionsForTest {

  def rootPath: String = "file://als-suggestions/shared/src/test/resources/test/oas30/json-schema/"
  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  test("test oas3 including cached json schema should only suggest definitions") {
    suggest("api.yaml").map { ci =>
      ci.length shouldBe 2
    }
  }

  private def suggest(api: String): Future[Seq[CompletionItem]] = {
    for {
      schema <- APIConfiguration.APIWithJsonSchema().baseUnitClient().parse(rootPath + "schema.json").map(_.baseUnit)
      c      <- platform.fetchContent(rootPath + api, AMFGraphConfiguration.predefined())
      ci <- {
        val projectState = TestProjectConfigurationState(
          Nil,
          new ProjectConfiguration(
            rootPath,
            Some(api),
            Set(rootPath + "schema.json"),
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
