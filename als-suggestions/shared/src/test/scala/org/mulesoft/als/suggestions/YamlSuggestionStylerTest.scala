package org.mulesoft.als.suggestions

import amf.core.client.common.position.{Position => AmfPosition}
import org.mulesoft.als.common.diff.FileAssertionTest
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.als.common.{NodeBranchBuilder, YPartBranch}
import org.mulesoft.als.suggestions.styler.{StylerParams, YamlSuggestionStyler}
import org.mulesoft.lsp.configuration.FormattingOptions
import org.mulesoft.lsp.edit.TextEdit
import org.mulesoft.lsp.feature.common.{Range, Position => LspPosition}
import org.scalatest.AsyncFunSuite
import org.yaml.model.{YDocument, YNode}
import org.yaml.parser.YamlParser

class YamlSuggestionStylerTest extends AsyncFunSuite with FileAssertionTest {

  private val dummyYPart: YPartBranch =
    YPartBranch(YNode.Null, AmfPosition(0, 0), Nil, isJson = false, isInFlow = false)

  test("Formatting options - indent") {
    val content =
      """swagger: "2.0"
        |
        |""".stripMargin

    RawSuggestion.forObject("info", "docs", mandatory = true)

    val styler =
      YamlSuggestionStyler(
        StylerParams(
          "",
          Position(1, 3),
          dummyYPart,
          FormattingOptions(4, insertSpaces = true)
        )
      )

    val styled = styler.style(RawSuggestion.forObject("info", "docs"))
    assert(styled.plain)
    assert(styled.text == "info:\n    ")
  }

  test("Formatting options - indent 3") {
    val content = """swagger: "2.0"
                    |paths:
                    |   /pets:
                    |      get:
                    |         externalDocs:
                    |
                    |""".stripMargin

    RawSuggestion.forObject("info", "docs", mandatory = true)

    val parts      = YamlParser(content).parse(false)
    val node       = parts.collectFirst({ case d: YDocument => d }).get.node
    val dummyYPart = NodeBranchBuilder.build(node, AmfPosition(5, 11), isJson = false)

    val styler =
      YamlSuggestionStyler(
        StylerParams(
          "",
          Position(5, 19),
          dummyYPart,
          FormattingOptions(3, insertSpaces = true)
        )
      )

    val styled = styler.style(RawSuggestion.forObject("url", "docs"))

    assert(styled.text == "url:\n   ")
  }

  test("Additional Text Edits: direct from Text Edit") {
    val content = """swagger: "2.0"
                    |paths:
                    |   /pets:
                    |      get:
                    |         externalDocs:
                    |
                    |""".stripMargin

    val edit = TextEdit(Range(LspPosition(1, 0), LspPosition(1, 0)), "additional:\n   Text: Edit\n")
    val suggestion = RawSuggestion
      .forObject("info", "docs", mandatory = true)
      .withAdditionalTextEdits(
        Seq(
          Left(
            edit
          )
        )
      )

    val parts      = YamlParser(content).parse(false)
    val node       = parts.collectFirst({ case d: YDocument => d }).get.node
    val dummyYPart = NodeBranchBuilder.build(node, AmfPosition(5, 19), isJson = false)

    val styler = YamlSuggestionStyler(
      StylerParams(
        "",
        Position(5, 19),
        dummyYPart,
        FormattingOptions(3, insertSpaces = true)
      )
    )

    val completionItem = styler.rawToStyledSuggestion(suggestion)

    assert(completionItem.additionalTextEdits.exists(_.contains(edit)))
  }

  test("Additional Text Edits: from YPart") {
    val content = """swagger: "2.0"
                    |paths:
                    |   /pets:
                    |      get:
                    |         externalDocs:
                    |
                    |""".stripMargin

    val formattedValue = "additional:\n    Text: Edit\n"
    val value          = "additional:\n Text: Edit\n"
    val edit           = TextEdit(Range(LspPosition(1, 0), LspPosition(1, 0)), formattedValue)
    val additionalSuggestion =
      AdditionalSuggestion(YamlParser(value).parse(false).head, PositionRange(Position(1, 0), Position(1, 0)))
    val suggestion = RawSuggestion
      .forObject("info", "docs", mandatory = true)
      .withAdditionalTextEdits(
        Seq(
          Right(
            additionalSuggestion
          )
        )
      )

    val parts      = YamlParser(content).parse(false)
    val node       = parts.collectFirst({ case d: YDocument => d }).get.node
    val dummyYPart = NodeBranchBuilder.build(node, AmfPosition(5, 19), isJson = false)

    val styler = YamlSuggestionStyler(
      StylerParams(
        "",
        Position(5, 19),
        dummyYPart,
        FormattingOptions(4, insertSpaces = true)
      )
    )

    val completionItem = styler.rawToStyledSuggestion(suggestion)

    assert(completionItem.additionalTextEdits.exists(_.contains(edit)))
  }

}
