package org.mulesoft.als.suggestions.test.raml10

import amf.aml.client.scala.AMLConfiguration
import amf.aml.client.scala.model.document.Dialect
import amf.apicontract.client.scala.RAMLConfiguration
import amf.core.client.scala.AMFGraphConfiguration
import amf.core.client.scala.model.document.{BaseUnit, Module}
import org.mulesoft.als.configuration.ProjectConfiguration
import org.mulesoft.als.suggestions.test.{BaseSuggestionsForTest, SuggestionsTest}
import org.mulesoft.amfintegration.amfconfiguration.{
  ALSConfigurationState,
  EditorConfigurationState,
  ProjectConfigurationState
}
import org.scalatest.{AsyncFunSuite, Matchers}

import scala.concurrent.ExecutionContext

class AliasedSemexSuggestionTest extends AsyncFunSuite with BaseSuggestionsForTest with Matchers {
  def rootPath: String                                     = "file://als-suggestions/shared/src/test/resources/test/raml10/aliased-semex/"
  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  test("test semex suggestion - empty") {
    for {
      d <- AMLConfiguration.predefined().baseUnitClient().parseDialect(rootPath + "dialect.yaml")
      l <- RAMLConfiguration.RAML10().baseUnitClient().parseLibrary(rootPath + "companion.raml")
      c <- platform.fetchContent(rootPath + "empty-semex.raml", AMFGraphConfiguration.predefined())
      ci <- {
        l.library.withReferences(Seq(d.dialect.cloneUnit()))
        val projectState = TestProjectConfigurationState(
          d.dialect,
          new ProjectConfiguration(rootPath,
                                   Some("empty-semex.raml"),
                                   Set(rootPath + "companion.raml"),
                                   Set.empty,
                                   Set(rootPath + "dialect.yaml"),
                                   Set.empty),
          l.library
        )
        val state = ALSConfigurationState(EditorConfigurationState.empty, projectState, None)

        suggestFromFile(c.stream.toString, rootPath + "empty-semex.raml", "*", state)
      }
    } yield {
      ci.length shouldBe (1)
      ci.head.label shouldBe ("(lib.)")
    }
  }

  // TODO: enable test when annotation types suggestion is fixed
  ignore("test properties suggestion  of aliased semex") {
    for {
      d <- AMLConfiguration.predefined().baseUnitClient().parseDialect(rootPath + "dialect.yaml")
      l <- RAMLConfiguration.RAML10().baseUnitClient().parseLibrary(rootPath + "companion.raml")
      c <- platform.fetchContent(rootPath + "semex-content.raml", AMFGraphConfiguration.predefined())
      ci <- {
        l.library.withReferences(Seq(d.dialect.cloneUnit()))
        val projectState = TestProjectConfigurationState(
          d.dialect,
          new ProjectConfiguration(rootPath,
                                   Some("semex-content.raml"),
                                   Set(rootPath + "companion.raml"),
                                   Set.empty,
                                   Set(rootPath + "dialect.yaml"),
                                   Set.empty),
          l.library
        )
        val state = ALSConfigurationState(EditorConfigurationState.empty, projectState, None)

        suggestFromFile(c.stream.toString, rootPath + "semex-content.raml", "*", state)
      }
    } yield {
      ci.length shouldBe (2)
    }
  }
}

case class TestProjectConfigurationState(d: Dialect, override val config: ProjectConfiguration, lib: Module)
    extends ProjectConfigurationState(Seq(d), Nil, config, Nil, Nil, Nil) {
  override def cache: Seq[BaseUnit] = Seq(lib.cloneUnit())
}
