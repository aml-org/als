package org.mulesoft.als.suggestions

import org.mulesoft.als.common.diff.FileAssertionTest
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.als.common.{NodeBranchBuilder, YPartBranch}
import org.mulesoft.als.suggestions.styler.{SyamlStylerParams, YamlSuggestionStyler}
import org.mulesoft.common.client.lexical.{Position => AmfPosition}
import org.mulesoft.lsp.configuration.FormattingOptions
import org.mulesoft.lsp.edit.TextEdit
import org.mulesoft.lsp.feature.common.{Range, Position => LspPosition}
import org.scalatest.funsuite.AsyncFunSuite
import org.yaml.model.{YDocument, YNode}
import org.yaml.parser.YamlParser

class YamlSuggestionStylerTest extends AsyncFunSuite with FileAssertionTest {

  private val dummyYPart: YPartBranch =
    YPartBranch(YNode.Null, AmfPosition(0, 0), Nil, strict = false)

  test("Formatting options - indent") {
    val content =
      """swagger: "2.0"
        |
        |""".stripMargin

    RawSuggestion.forObject("info", "docs", mandatory = true)

    val styler =
      YamlSuggestionStyler(
        SyamlStylerParams(
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
    val dummyYPart = NodeBranchBuilder.build(node, AmfPosition(5, 11), strict = false)

    val styler =
      YamlSuggestionStyler(
        SyamlStylerParams(
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
    val dummyYPart = NodeBranchBuilder.build(node, AmfPosition(5, 19), strict = false)

    val styler = YamlSuggestionStyler(
      SyamlStylerParams(
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

    val formattedValue = "\nadditional:\n    Text: Edit\n\n"
    val value          = "additional:\n Text: Edit"
    val edit           = TextEdit(Range(LspPosition(1, 0), LspPosition(1, 0)), formattedValue)
    val additionalSuggestion =
      AdditionalSuggestion(YamlParser(value).parse(false).head, Left(PositionRange(Position(1, 0), Position(1, 0))))
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
    val dummyYPart = NodeBranchBuilder.build(node, AmfPosition(5, 19), strict = false)

    val styler = YamlSuggestionStyler(
      SyamlStylerParams(
        "",
        Position(5, 19),
        dummyYPart,
        FormattingOptions(4, insertSpaces = true)
      )
    )

    val completionItem = styler.rawToStyledSuggestion(suggestion)

    assert(completionItem.additionalTextEdits.exists(_.contains(edit)))
  }

  test("Additional Text Edits: from YPart, with ParentEntry, no uses key") {
    val content = """#%RAML 1.0
                    |
                    |types:
                    |  t:
                    |
                    |""".stripMargin

    val value      = "\nuses:\n  lib: mylib.raml\n"
    val edit       = TextEdit(Range(LspPosition(1, 0), LspPosition(1, 0)), value)
    val parts      = YamlParser(content).parse(false)
    val node       = parts.collectFirst({ case d: YDocument => d }).get.node
    val dummyYPart = NodeBranchBuilder.build(node, AmfPosition(5, 4), strict = false)

    val additionalSuggestion =
      AdditionalSuggestion(YNode("mylib.raml"), Seq("uses", "lib"), node, PositionRange.TopLine)
    val suggestion = RawSuggestion
      .forObject("object", "unknown", mandatory = true)
      .withAdditionalTextEdits(
        Seq(
          Right(
            additionalSuggestion
          )
        )
      )
    val styler = YamlSuggestionStyler(
      SyamlStylerParams(
        "",
        Position(5, 19),
        dummyYPart,
        FormattingOptions(2, insertSpaces = true)
      )
    )

    val completionItem = styler.rawToStyledSuggestion(suggestion)

    assert(completionItem.additionalTextEdits.exists(_.contains(edit)))
  }

  test("Additional Text Edits: from YPart, with ParentEntry, with uses key") {
    val content = """#%RAML 1.0
                    |
                    |uses:
                    |
                    |
                    |types:
                    |  t:
                    |
                    |""".stripMargin

    val value      = "\n  lib: mylib.raml\n"
    val edit       = TextEdit(Range(LspPosition(2, 5), LspPosition(2, 5)), value)
    val parts      = YamlParser(content).parse(false)
    val node       = parts.collectFirst({ case d: YDocument => d }).get.node
    val dummyYPart = NodeBranchBuilder.build(node, AmfPosition(9, 4), strict = false)

    val additionalSuggestion =
      AdditionalSuggestion(YNode("mylib.raml"), Seq("uses", "lib"), node, PositionRange.TopLine)
    val suggestion = RawSuggestion
      .forObject("object", "unknown", mandatory = true)
      .withAdditionalTextEdits(
        Seq(
          Right(
            additionalSuggestion
          )
        )
      )
    val styler = YamlSuggestionStyler(
      SyamlStylerParams(
        "",
        Position(5, 19),
        dummyYPart,
        FormattingOptions(2, insertSpaces = true)
      )
    )

    val completionItem = styler.rawToStyledSuggestion(suggestion)

    assert(completionItem.additionalTextEdits.exists(_.contains(edit)))
  }

}
