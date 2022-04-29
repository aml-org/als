package org.mulesoft.als.suggestions

import amf.core.client.common.position.{Position => AmfPosition}
import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.common.diff.FileAssertionTest
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.suggestions.styler.{JsonSuggestionStyler, StylerParams}
import org.mulesoft.lexer.{InputRange, SourceLocation}
import org.mulesoft.lsp.configuration.{DefaultFormattingOptions, FormattingOptions}
import org.scalatest.AsyncFunSuite
import org.yaml.model._

import scala.concurrent.ExecutionContext

class JsonSuggestionStylerTest extends AsyncFunSuite with FileAssertionTest {

  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  val dummyYPart: YPartBranch = YPartBranch(YNode.Null, AmfPosition(0, 0), Nil, isJson = true, isInFlow = true)

  test("should not have quotes if key and inside quotes") {
    val content =
      """{
        |  "sw"
        |}
        |""".stripMargin

    val styler = JsonSuggestionStyler(StylerParams("sw", Position(1, 5), dummyYPart, DefaultFormattingOptions))

    val styled = styler.style(RawSuggestion("swagger", isAKey = true))

    assert(styled.text == "\"swagger\": \"$1\"")
  }

  test("should have quotes if key and not inside quotes") {
    val content =
      """{
        |   ,
        |}
        |""".stripMargin

    val styler = JsonSuggestionStyler(
      StylerParams(
        "",
        Position(1, 2),
        dummyYPart,
        DefaultFormattingOptions
      )
    )

    val styled = styler.style(RawSuggestion("swagger", isAKey = true))

    assert(!styled.plain)
    assert(styled.text == "\"swagger\": \"$1\"")
  }

  test("should close quotes if key and only open quotes") {
    val content =
      """{
        |  "
        |}
        |""".stripMargin

    val styler = JsonSuggestionStyler(StylerParams("", Position(1, 3), dummyYPart, DefaultFormattingOptions))

    val styled = styler.style(RawSuggestion("swagger", isAKey = true))

    assert(!styled.plain)
    assert(styled.text == "\"swagger\": \"$1\"")
  }

  test("should add commas if there is a brother afterwards") {
    val content =
      """{
        |  "
        |  "swagger": "2.0"
        |}
        |""".stripMargin

    val node = new YNodePlain(YScalar("\"x\"", ""), YType.Str.tag, None, SourceLocation("x", 2, 3, 2, 4), IndexedSeq())
    val emptyVal   = YNode("")
    val current    = YMapEntry(SourceLocation("x", 2, 3, 2, 4), IndexedSeq(node, emptyVal))
    val swagger    = YNode("\"swagger\"")
    val swaggerVal = YNode("\"2.0\"")
    val swaggerMap = YMapEntry(SourceLocation("swagger", InputRange(3, 3, 3, 19)), IndexedSeq(swagger, swaggerVal))
    val stack      = Seq(current, YMap(IndexedSeq(current, swaggerMap), "noname"))

    val dummyYPart = YPartBranch(node, AmfPosition(2, 3), stack, isJson = true, isInFlow = true)

    val styler = JsonSuggestionStyler(StylerParams("", Position(2, 3), dummyYPart, DefaultFormattingOptions))

    val styled = styler.style(RawSuggestion.forObject("info", "none"))

    assert(!styled.plain)
    assert(styled.text == "\"info\": {\n  \"$1\"\n},")
  }

  test("Formatting options - indent") {
    val content = """{
                    |  ""
                    |}""".stripMargin

    val styler =
      JsonSuggestionStyler(StylerParams("", Position(1, 3), dummyYPart, FormattingOptions(4, insertSpaces = true)))

    val styled = styler.style(RawSuggestion.forObject("info", "docs"))

    assert(!styled.plain)
    assert(styled.text == "\"info\": {\n    \"$1\"\n}")
  }

  test("Formatting options - indent 3") {
    val content = """{
                    |  "swagger": "2.0",
                    |  "paths": {
                    |      "/path" : {
                    |        ""
                    |      }
                    |    }
                    |}""".stripMargin

    val styler =
      JsonSuggestionStyler(StylerParams("", Position(4, 9), dummyYPart, FormattingOptions(3, insertSpaces = true)))

    val styled = styler.style(RawSuggestion.forObject("get", "operation"))

    assert(!styled.plain)
    assert(styled.text == "\"get\": {\n   \"$1\"\n}")
  }

  test("Formatting options - prefer tabs") {
    val content = """{
                    |  ""
                    |}""".stripMargin

    RawSuggestion.forObject("info", "docs", mandatory = true)

    val styler =
      JsonSuggestionStyler(StylerParams("", Position(1, 3), dummyYPart, FormattingOptions(2, insertSpaces = false)))

    val styled = styler.style(RawSuggestion.forObject("info", "docs"))

    assert(!styled.plain)
    assert(styled.text == "\"info\": {\n\t\"$1\"\n}")
  }
}
