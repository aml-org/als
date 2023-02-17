package org.mulesoft.als.suggestions.test.raml10

import amf.aml.client.scala.AMLConfiguration
import amf.apicontract.client.scala.RAMLConfiguration
import amf.core.client.scala.AMFGraphConfiguration
import org.mulesoft.als.configuration.ProjectConfiguration
import org.mulesoft.als.suggestions.test.{BaseSuggestionsForTest, TestProjectConfigurationState}
import org.mulesoft.amfintegration.amfconfiguration.{ALSConfigurationState, EditorConfigurationState}
import org.mulesoft.lsp.edit.TextEdit
import org.mulesoft.lsp.feature.common.{Position, Range}
import org.mulesoft.lsp.feature.completion.CompletionItem

import scala.concurrent.{ExecutionContext, Future}

class AliasedSemexSuggestionTest extends AsyncFunSuite with BaseSuggestionsForTest with Matchers {
  def rootPath: String = "file://als-suggestions/shared/src/test/resources/test/raml10/aliased-semex/"
  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  private def suggest(api: String): Future[Seq[CompletionItem]] = {
    for {
      d <- AMLConfiguration.predefined().baseUnitClient().parseDialect(rootPath + "dialect.yaml")
      l <- RAMLConfiguration.RAML10().baseUnitClient().parseLibrary(rootPath + "companion.raml")
      c <- platform.fetchContent(rootPath + api, AMFGraphConfiguration.predefined())
      ci <- {
        l.library.withReferences(Seq(d.dialect.cloneUnit()))
        val projectState = TestProjectConfigurationState(
          Seq(d.dialect),
          new ProjectConfiguration(
            rootPath,
            Some(api),
            Set(rootPath + "companion.raml"),
            Set.empty,
            Set(rootPath + "dialect.yaml"),
            Set.empty
          ),
          l.library
        )
        val state = ALSConfigurationState(EditorConfigurationState.empty, projectState, None)

        suggestFromFile(c.stream.toString, rootPath + api, "*", state)
      }
    } yield ci
  }

  test("test semex suggestion - empty") {
    suggest("empty-semex.raml").map { ci =>
      ci.length shouldBe 1
      ci.head.label shouldBe "(lib."
    }
  }

  test("test semex suggestion - import companion") {
    suggest("annotation-companion-not-included.raml").map { ci =>
      val maybeItem = ci.find(_.label == "(Extensions.key)")
      maybeItem.nonEmpty shouldBe true
      maybeItem
        .exists(ci =>
          ci.additionalTextEdits.contains(
            Seq(TextEdit(Range(Position(5, 0), Position(5, 0)), "\nuses:\n  Extensions: companion.raml\n"))
          ) &&
            ci.textEdit.contains(Left(TextEdit(Range(Position(8, 4), Position(8, 4)), "(Extensions.key):\n  ")))
        ) shouldBe true

      val scalarItem = ci.find(_.label == "(Extensions.scalar)")
      scalarItem.nonEmpty shouldBe true
      scalarItem.exists(ci =>
        ci.textEdit.contains(Left(TextEdit(Range(Position(8, 4), Position(8, 4)), "(Extensions.scalar): ")))
      ) shouldBe true
    }
  }

  test("test semex suggestion - import companion with uses key written") {
    suggest("annotation-companion-not-included-with-uses.raml").map { ci =>
      val maybeItem = ci.find(_.label == "(Extensions.key)")
      maybeItem.nonEmpty shouldBe true
      maybeItem
        .exists(ci =>
          ci.additionalTextEdits.contains(
            Seq(TextEdit(Range(Position(3, 5), Position(3, 5)), "\n  Extensions: companion.raml\n"))
          )
        ) shouldBe true
    }
  }

  test("test aliased value semex suggestion") {
    suggest("aliased-value-semex.raml").map { ci =>
      ci.length shouldBe 2
      ci.head.label shouldBe "key)"
      ci.head.detail.get shouldBe "extensions"
    }
  }

  test("test properties suggestion  of aliased semex") {
    suggest("semex-content.raml").map { ci =>
      ci.length shouldBe 2
      val namePro = ci.find(_.label == "name")
      namePro.isDefined shouldBe true
      val keyProp = ci.find(_.label == "key")
      keyProp.isDefined shouldBe true
    }
  }

  test("test to not suggest local annotations overridden by semex") {
    suggest("local-annotation-other-target.raml").map { ci =>
      ci.isEmpty shouldBe true
    }
  }
}
