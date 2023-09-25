package org.mulesoft.als.suggestions.test.oas30

import amf.apicontract.client.scala.OASConfiguration
import amf.core.client.scala.AMFGraphConfiguration
import org.mulesoft.als.configuration.ProjectConfiguration
import org.mulesoft.als.suggestions.test.{BaseSuggestionsForTest, TestProjectConfigurationState}
import org.mulesoft.amfintegration.amfconfiguration.{ALSConfigurationState, EditorConfigurationState}
import org.mulesoft.lsp.feature.completion.CompletionItem
import org.scalatest.funsuite.AsyncFunSuite
import org.scalatest.matchers.should.Matchers

import scala.concurrent.{ExecutionContext, Future}

class Oas30ComponentRefSuggestionTest extends AsyncFunSuite with BaseSuggestionsForTest with Matchers {

  def rootPath: String = "file://als-suggestions/shared/src/test/resources/test/oas30/oas-components/root-oas/"
  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  test("oas3 including cached oas components should suggest the components file") {
    suggest("api-ref-level0.yaml", "components.yaml").map { ci =>
      assert(ci.map(_.label).contains("components.yaml"))
    }
  }

  test("oas3 including cached oas components should suggest the correct headers component (not parameters)") {
    suggest("api-ref-header.yaml", "components.yaml").map { ci =>
      assert(ci.size == 3)
      assert(ci.map(_.label).exists(_.contains("headers")))
      assert(ci.map(_.label).exists(_.contains("parameters")))
    }
  }

  test("oas3 including cached oas components should suggest the correct parameters component") {
    suggest("api-ref-parameter.yaml", "components.yaml").map { ci =>
      assert(ci.size == 3)
      assert(ci.map(_.label).exists(_.contains("headers")))
      assert(ci.map(_.label).exists(_.contains("parameters")))
    }
  }

  test("oas3 including cached oas components should suggest the correct links component") {
    suggest("api-ref-links.yaml", "components.yaml").map { ci =>
      assert(ci.size == 1)
      assert(ci.map(_.label).forall(_.contains("links")))
    }
  }

  test("oas3 including cached oas components should suggest the correct responses component") {
    suggest("api-ref-responses.yaml", "components.yaml").map { ci =>
      assert(ci.size == 1)
      assert(ci.map(_.label).forall(_.contains("responses")))
    }
  }

  test("oas3 including cached oas components should suggest the correct security component") {
    suggest("api-ref-security.yaml", "components.yaml").map { ci =>
      assert(ci.size == 1)
      assert(ci.map(_.label).forall(_.contains("security")))
    }
  }

  test("oas3 including cached oas components should only suggest declared within specific declared key") {
    suggest("api-ref-level3.yaml", "components.yaml").map { ci =>
      ci.length shouldBe 2
      assert(ci.map(_.label).contains("mySchema1"))
      assert(ci.map(_.label).contains("mySchema2"))
    }
  }

  test("oas3 including cached oas components should only suggest declared names (outside specific key)") {
    suggest("api-ref-level2.yaml", "components.yaml").map { ci =>
      ci.length shouldBe 2
      assert(ci.map(_.label).contains("schemas/mySchema1"))
      assert(ci.map(_.label).contains("schemas/mySchema2"))
    }
  }

  test("oas3 including cached oas components should only suggest declared names inside a given file") {
    suggest("api-ref-level1.yaml", "components.yaml").map { ci =>
      ci.length shouldBe 2
      assert(ci.map(_.label).contains("components/schemas/mySchema1"))
      assert(ci.map(_.label).contains("components/schemas/mySchema2"))
    }
  }

  test("oas3 including cached oas components should suggest the hashtag-slash wildcard (#/) with declared names") {
    suggest("api-ref-level1-without-hashtag-slash.yaml", "components.yaml").map { ci =>
      ci.length shouldBe 3 // also includes `yaml` from the file suggestions
      assert(ci.map(_.label).exists(_.contains("components/schemas/mySchema1")))
      assert(ci.map(_.filterText.get).exists(_.contains("components.yaml#/components/schemas/mySchema1")))
      assert(ci.map(_.label).exists(_.contains("components/schemas/mySchema2")))
      assert(ci.map(_.filterText.get).exists(_.contains("components.yaml#/components/schemas/mySchema2")))
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
