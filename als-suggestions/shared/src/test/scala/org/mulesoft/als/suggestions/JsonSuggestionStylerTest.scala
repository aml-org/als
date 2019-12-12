package org.mulesoft.als.suggestions

import amf.core.parser.{Position => AmfPosition}
import common.diff.FileAssertionTest
import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.suggestions.patcher.{ColonToken, PatchedContent, QuoteToken}
import org.mulesoft.als.suggestions.styler.{JsonSuggestionStyler, StylerParams}
import org.scalatest.AsyncFunSuite
import org.yaml.model.YNode

import scala.concurrent.ExecutionContext

class JsonSuggestionStylerTest extends AsyncFunSuite with FileAssertionTest {

  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  val dummyYPart = YPartBranch(YNode.Null, AmfPosition(0, 0), Nil, isJson = true)

  test("should not have quotes if key and inside quotes") {
    val content =
      """{
        |  "sw"
        |}
        |""".stripMargin

    val patchedContent =
      """{
        |  "sw" : ""
        |}
        |""".stripMargin
    val styler = JsonSuggestionStyler(
      StylerParams("sw",
                   PatchedContent(content, patchedContent, List(ColonToken, QuoteToken, QuoteToken)),
                   Position(1, 5),
                   dummyYPart))

    val styled = styler.style(RawSuggestion("swagger", isAKey = true))

    assert(styled.text == "\"swagger\": \"$1\"")
  }

  test("should have quotes if key and not inside quotes") {
    val content =
      """{
        |   ,
        |}
        |""".stripMargin

    val patchedContent =
      """{
        |  "x" : "" ,
        |}
        |""".stripMargin
    val styler = JsonSuggestionStyler(
      StylerParams(
        "",
        PatchedContent(content, patchedContent, List(QuoteToken, QuoteToken, ColonToken, QuoteToken, QuoteToken)),
        Position(1, 2),
        dummyYPart))

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

    val patched =
      """{
        |  "x" : ""
        |}
        |""".stripMargin
    val styler = JsonSuggestionStyler(
      StylerParams("",
                   PatchedContent(content, content, List(QuoteToken, ColonToken, QuoteToken, QuoteToken)),
                   Position(1, 3),
                   dummyYPart))

    val styled = styler.style(RawSuggestion("swagger", isAKey = true))

    assert(!styled.plain)
    assert(styled.text == "\"swagger\": \"$1\"")
  }

}
