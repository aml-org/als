package org.mulesoft.als.suggestions

import amf.core.client.common.position.{Position => AmfPosition}
import org.mulesoft.als.common.diff.FileAssertionTest
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.common.{NodeBranchBuilder, YPartBranch}
import org.mulesoft.als.suggestions.styler.{StylerParams, YamlSuggestionStyler}
import org.mulesoft.lsp.configuration.FormattingOptions
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
      YamlSuggestionStyler(StylerParams("", Position(1, 3), dummyYPart, FormattingOptions(4, insertSpaces = true)))

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
      YamlSuggestionStyler(StylerParams("", Position(5, 19), dummyYPart, FormattingOptions(3, insertSpaces = true)))

    val styled = styler.style(RawSuggestion.forObject("url", "docs"))

    assert(styled.text == "url:\n   ")
  }

}
