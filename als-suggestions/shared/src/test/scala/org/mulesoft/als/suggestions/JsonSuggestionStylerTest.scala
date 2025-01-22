package org.mulesoft.als.suggestions

import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.common.diff.FileAssertionTest
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.suggestions.styler.{JsonSuggestionStyler, SyamlStylerParams, YamlSuggestionStyler}
import org.mulesoft.common.client.lexical.{PositionRange, SourceLocation, Position => AmfPosition}
import org.mulesoft.lsp.configuration.{DefaultFormattingOptions, FormattingOptions}
import org.scalatest.funsuite.AsyncFunSuite
import org.yaml.model._

import scala.concurrent.ExecutionContext

class JsonSuggestionStylerTest extends AsyncFunSuite with FileAssertionTest {

  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  val dummyYPart: YPartBranch = YPartBranch(YNode.Null, AmfPosition(0, 0), Nil, strict = true)

  test("should not have quotes if key and inside quotes") {

    val styler = JsonSuggestionStyler(SyamlStylerParams("sw", Position(1, 5), dummyYPart, DefaultFormattingOptions))

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
      SyamlStylerParams(
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

    val styler = JsonSuggestionStyler(SyamlStylerParams("", Position(1, 3), dummyYPart, DefaultFormattingOptions))

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
    val swaggerMap = YMapEntry(SourceLocation("swagger", PositionRange(3, 3, 3, 19)), IndexedSeq(swagger, swaggerVal))
    val stack      = Seq(current, YMap(IndexedSeq(current, swaggerMap), "noname"))

    val dummyYPart = YPartBranch(node, AmfPosition(2, 3), stack, strict = true)

    val styler = JsonSuggestionStyler(SyamlStylerParams("", Position(2, 3), dummyYPart, DefaultFormattingOptions))

    val styled = styler.style(RawSuggestion.forObject("info", "none"))

    assert(!styled.plain)
    assert(styled.text == "\"info\": {\n  \"$1\"\n},")
  }

  test("Formatting options - indent") {
    val content = """{
                    |  ""
                    |}""".stripMargin

    val styler =
      JsonSuggestionStyler(SyamlStylerParams("", Position(1, 3), dummyYPart, FormattingOptions(4, insertSpaces = true)))

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
      JsonSuggestionStyler(SyamlStylerParams("", Position(4, 9), dummyYPart, FormattingOptions(3, insertSpaces = true)))

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
      JsonSuggestionStyler(
        SyamlStylerParams("", Position(1, 3), dummyYPart, FormattingOptions(2, insertSpaces = false))
      )

    val styled = styler.style(RawSuggestion.forObject("info", "docs"))

    assert(!styled.plain)
    assert(styled.text == "\"info\": {\n\t\"$1\"\n}")
  }


  test("render RawSuggestion with children and set values - empty value") {
    val styler = JsonSuggestionStyler(
      SyamlStylerParams(
        "",
        Position(0, 0),
        dummyYPart,
        FormattingOptions(2, insertSpaces = true),
        supportSnippets = false
      )
    )

    val golden = """"root": {
                   |  "k1": ,
                   |  "k2": "v1"
                   |}""".stripMargin
    val rawSuggestion = RawSuggestion("root", SuggestionStructure(ObjectRange, isKey = true))
      .withChildren(Seq(
        RawSuggestion(
          "k1", SuggestionStructure(BoolScalarRange, isKey = true)
        ),
        RawSuggestion("k2", SuggestionStructure(StringScalarRange, isKey = true))
          .withChildren(Seq(RawSuggestion("v1", isAKey = false)))
      ))

    val result = styler.style(rawSuggestion).text

    assert(result == golden)
  }
}
