package org.mulesoft.als.suggestions

import amf.core.parser.{Position => AmfPosition}
import org.mulesoft.als.common.diff.FileAssertionTest
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.common.{NodeBranchBuilder, YPartBranch}
import org.mulesoft.als.suggestions.patcher.{ColonToken, PatchedContent, QuoteToken}
import org.mulesoft.als.suggestions.styler.{StylerParams, YamlSuggestionStyler}
import org.mulesoft.lsp.configuration.FormattingOptions
import org.scalatest.AsyncFunSuite
import org.yaml.model.{YDocument, YNode}
import org.yaml.parser.YamlParser

class YamlSuggestionStylerTest extends AsyncFunSuite with FileAssertionTest {

  val dummyYPart = YPartBranch(YNode.Null, AmfPosition(0, 0), Nil, isJson = false, isInFlow = false)

  test("Formatting options - indent") {
    val content =
      """swagger: "2.0"
        |
        |""".stripMargin

    val patchedContent =
      """swagger: "2.0"
        |k:
        |""".stripMargin

    RawSuggestion.forObject("info", "docs", mandatory = true)

    val styler = YamlSuggestionStyler(
      StylerParams("",
                   PatchedContent(content, patchedContent, List(ColonToken, QuoteToken, QuoteToken)),
                   Position(1, 3),
                   dummyYPart,
                   FormattingOptions(4, insertSpaces = true)))

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

    val patchedContent = """swagger: "2.0"
                           |paths:
                           |   /pets:
                           |      get:
                           |         externalDocs:
                           |            k:
                           |""".stripMargin

    RawSuggestion.forObject("info", "docs", mandatory = true)

    val parts      = YamlParser(patchedContent).parse(false)
    val node       = parts.collectFirst({ case d: YDocument => d }).get.node
    val dummyYPart = NodeBranchBuilder.build(node, AmfPosition(5, 19), isJson = true)

    val styler = YamlSuggestionStyler(
      StylerParams("",
                   PatchedContent(content, patchedContent, List(ColonToken, QuoteToken, QuoteToken)),
                   Position(5, 19),
                   dummyYPart,
                   FormattingOptions(3, insertSpaces = true)))

    val styled = styler.style(RawSuggestion.forObject("url", "docs"))

    assert(styled.text == "url:\n   ")
  }

}
