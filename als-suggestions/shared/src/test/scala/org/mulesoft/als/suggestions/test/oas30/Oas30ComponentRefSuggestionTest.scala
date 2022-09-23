package org.mulesoft.als.suggestions.test.oas30

import amf.apicontract.client.scala.OASConfiguration
import amf.core.client.scala.AMFGraphConfiguration
import org.mulesoft.als.configuration.ProjectConfiguration
import org.mulesoft.als.suggestions.test.{BaseSuggestionsForTest, TestProjectConfigurationState}
import org.mulesoft.amfintegration.amfconfiguration.{ALSConfigurationState, EditorConfigurationState}
import org.mulesoft.lsp.feature.completion.CompletionItem
import org.scalatest.AsyncFunSuite
import org.scalatest.Matchers.convertToAnyShouldWrapper

import scala.concurrent.{ExecutionContext, Future}

class Oas30ComponentRefSuggestionTest extends AsyncFunSuite with BaseSuggestionsForTest {

  def rootPath: String = "file://als-suggestions/shared/src/test/resources/test/oas30/oas-components/root-oas/"
  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  test("test oas3 including cached oas components should suggest the components file") {
    suggest("api-ref-level0.yaml", "components.yaml").map { ci =>
      assert(ci.map(_.label).contains("components.yaml"))
    }
  }

  test("test oas3 including cached oas components should only suggest declared within specific declared key") {
    suggest("api-ref-level3.yaml", "components.yaml").map { ci =>
      ci.length shouldBe 2
      assert(ci.map(_.label).contains("mySchema1"))
      assert(ci.map(_.label).contains("mySchema2"))
    }
  }

  test("test oas3 including cached oas components should only suggest declared names (outside specific key)") {
    suggest("api-ref-level2.yaml", "components.yaml").map { ci =>
      ci.length shouldBe 2
      assert(ci.map(_.label).contains("schemas/mySchema1"))
      assert(ci.map(_.label).contains("schemas/mySchema2"))
    }
  }

  test("test oas3 including cached oas components should only suggest declared names inside a given file") {
    suggest("api-ref-level1.yaml", "components.yaml").map { ci =>
      ci.length shouldBe 2
      assert(ci.map(_.label).contains("components/schemas/mySchema1"))
      assert(ci.map(_.label).contains("components/schemas/mySchema2"))
    }
  }

  private def suggest(api: String, componentName: String): Future[Seq[CompletionItem]] = {
    for {
      schema <- OASConfiguration
        .OAS30Component()
        .baseUnitClient()
        .parse(rootPath + componentName)
        .map(_.baseUnit)
      c <- platform.fetchContent(rootPath + api, AMFGraphConfiguration.predefined())
      ci <- {
        val projectState = TestProjectConfigurationState(
          Nil,
          new ProjectConfiguration(
            rootPath,
            Some(api),
            Set(rootPath + componentName),
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
