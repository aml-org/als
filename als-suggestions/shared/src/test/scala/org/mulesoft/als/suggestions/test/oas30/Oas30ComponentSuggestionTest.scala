package org.mulesoft.als.suggestions.test.oas30

import amf.apicontract.client.scala.AMFConfiguration
import amf.apicontract.client.scala.OASConfiguration
import amf.core.client.scala.AMFGraphConfiguration
import amf.core.client.scala.resource.ResourceLoader
import org.mulesoft.als.configuration.ProjectConfiguration
import org.mulesoft.als.suggestions.test.{BaseSuggestionsForTest, TestProjectConfigurationState}
import org.mulesoft.amfintegration.amfconfiguration.{ALSConfigurationState, EditorConfigurationState}
import org.mulesoft.lsp.feature.completion.CompletionItem
import org.scalatest.AsyncFunSuite
import org.scalatest.Matchers.convertToAnyShouldWrapper

import scala.concurrent.{ExecutionContext, Future}

class Oas30ComponentSuggestionTest extends AsyncFunSuite with BaseSuggestionsForTest {

  def rootPath: String = "file://als-suggestions/shared/src/test/resources/test/oas30/oas-components/root-components/"
  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  test("test oas 3 components at root") {
    suggest("empty.yaml").map { ci =>
      ci.length shouldBe 4
      assert(ci.map(_.label).contains("info"))
      assert(ci.map(_.label).contains("New info"))
      assert(ci.map(_.label).contains("paths"))
      assert(ci.map(_.label).contains("components"))
    }
  }

  test("test oas 3 components at info") {
    suggest("inside-info.yaml").map { ci =>
      ci.length shouldBe 2
      assert(ci.map(_.label).contains("title"))
      assert(ci.map(_.label).contains("version"))
    }
  }

  test("test oas 3 components at components") {
    suggest("inside-components.yaml").map { ci =>
      ci.length shouldBe 9
      assert(
        ci.map(_.label).toSet == Set(
          "schemas",
          "responses",
          "parameters",
          "examples",
          "requestBodies",
          "headers",
          "securitySchemes",
          "links",
          "callbacks"
        )
      )
    }
  }

  test("test oas 3 components inside a component") {
    suggest("inside-specific-component.yaml").map { ci =>
      assert(ci.map(_.label).contains("type"))
    }
  }

  private def suggest(componentName: String): Future[Seq[CompletionItem]] = {
    for {
      schema <- OASConfiguration
        .OAS30Component()
        .baseUnitClient()
        .parse(rootPath + componentName)
        .map(_.baseUnit)
      c <- platform.fetchContent(rootPath + componentName, AMFGraphConfiguration.predefined())
      ci <- {
        val projectState = TestProjectConfigurationState(
          Nil,
          new ProjectConfiguration(
            rootPath,
            Some(componentName),
            Set.empty,
            Set.empty,
            Set.empty,
            Set.empty
          ),
          schema
        )
        val state = ALSConfigurationState(EditorConfigurationState.empty, projectState, None)
        suggestFromFile(c.stream.toString, rootPath + componentName, "*", state)
      }
    } yield ci
  }

  // in Anypoint-ALS this will be plugged by APB
  override protected def createNewStateWithLoaders(
      configurationState: ALSConfigurationState,
      resourceLoader: ResourceLoader
  ): ALSConfigurationState =
    new ALSConfigurationState(configurationState.editorState, configurationState.projectState, Some(resourceLoader)) {
      override def getAmfConfig: AMFConfiguration =
        getAmfConfig(OASConfiguration.OAS30Component())
    }
}
