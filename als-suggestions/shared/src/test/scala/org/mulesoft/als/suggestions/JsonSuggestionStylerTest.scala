package org.mulesoft.als.suggestions

import common.diff.FileAssertionTest
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.suggestions.interfaces.Syntax
import org.mulesoft.als.suggestions.styler.{JsonSuggestionStyler, StylerParams}
import org.scalatest.AsyncFunSuite

import scala.concurrent.{ExecutionContext, Future}

class JsonSuggestionStylerTest extends AsyncFunSuite with FileAssertionTest {

  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  test("should not have quotes if key and inside quotes") {
    val content =
      """{
        |  "sw"
        |}
        |""".stripMargin

    val styler = JsonSuggestionStyler(StylerParams("sw", content, Position(1, 5)))

    val styled = styler.style(RawSuggestion("swagger", isAKey = true))

    assert(styled.plain)
    assert(styled.text == "swagger")
  }

  test("should have quotes if key and not inside quotes") {
    val content =
      """{
        |   ,
        |}
        |""".stripMargin

    val styler = JsonSuggestionStyler(StylerParams("", content, Position(1, 2)))

    val styled = styler.style(RawSuggestion("swagger", isAKey = true))

    assert(!styled.plain)
    assert(styled.text == "\"swagger\":\"$1\"")
  }

  test("should close quotes if key and only open quotes") {
    val content =
      """{
        |  "
        |}
        |""".stripMargin

    val styler = JsonSuggestionStyler(StylerParams("", content, Position(1, 3)))

    val styled = styler.style(RawSuggestion("swagger", isAKey = true))

    assert(!styled.plain)
    assert(styled.text == "swagger\":\"$1\"")
  }

}
