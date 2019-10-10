package org.mulesoft.als.suggestions.styler

import org.mulesoft.als.suggestions.{BoolScalarRange, NumberScalarRange, RawSuggestion, SuggestionOptions}
import org.yaml.model._
import org.yaml.render.YamlRender

case class YamlSuggestionStyler(override val params: StylerParams) extends SuggestionStyler {
  override def style(raw: RawSuggestion): Styled = {

    val prefix =
      if (!raw.options.isKey && (raw.options.isArray || raw.options.isObject)) // never will suggest object in value as is not key. Suggestions should be empty
        "\n  "
      else ""
    val astBuilder = new AstRawBuilder(raw, false)
    val text       = prefix + YamlRender.render(astBuilder.ast)

    Styled(text, plain = astBuilder.asSnippet)
  }

//  private def arrayItemPrefixIfNecessary(rawSuggestion: RawSuggestion) =
//    if (rawSuggestion.options.arrayItem) arrayItemPrefix(rawSuggestion) else ""

  private def arrayItemPrefix(rawSuggestion: RawSuggestion) = {
    startWithEOL(rawSuggestion.whiteSpacesEnding) + whiteSpaceOrSpace(rawSuggestion.whiteSpacesEnding) + "- "
  }

  private def whiteSpaceOrSpace(str: String): String = if (str.isEmpty) " " else str

  /**
    * in case there are children, they each will calculate their own indentation
    * @param whiteSpacesEnding
    * @param children
    * @return
    */
  private def whiteSpaceOrSpaceIfSingleParent(whiteSpacesEnding: String, children: Seq[String]): String =
    if (children.isEmpty) whiteSpaceOrSpace(whiteSpacesEnding)
    else ""

//  private def keyAdapter(rawSuggestion: RawSuggestion) =
//    if (!params.hasLine || !params.hasColon)
//      s"${rawSuggestion.newText}:${whiteSpaceOrSpaceIfSingleParent(rawSuggestion.whiteSpacesEnding, rawSuggestion.sons)}"
//    else rawSuggestion.newText
//
//
//  private def arrayAdapter(rawSuggestion: RawSuggestion) = if (rawSuggestion.options.arrayProperty) "- " else ""

  override def styleKey(key: String): String = s"$key: "

  class AstRawBuilder(raw: RawSuggestion, isSnippet: Boolean) {

    private var snippet: Boolean = false
    def asSnippet: Boolean       = snippet

    def ast: YNode = {
      if (raw.options.isKey) YNode(YMap(IndexedSeq(emitKey()), ""))
      else {
        value(raw.newText, raw.options)
      }
    }

    def emitKey(index: Integer = 0): YMapEntry = {
      val node =
        if (raw.options.isObject) valueObject(index)
        else {
          if (isSnippet) value(s"$index", raw.options)
          else value("", raw.options)
        }

      YMapEntry(raw.newText, node)
    }

    def valueObject(index: Integer): YNode = {
      val list = if (raw.sons.nonEmpty) {
        snippet = true
        raw.sons.zipWithIndex.map(t => new AstRawBuilder(t._1, true).emitKey(t._2 + 1)).toIndexedSeq
      } else if (isSnippet) IndexedSeq(YMapEntry(s"$index", ""))
      else IndexedSeq.empty
      val n = YNode(YMap(list, ""))

      if (raw.options.isArray) {
        YNode(YSequence(n))
      } else n
    }

    def value(text: String, options: SuggestionOptions): YNode = {
      val node: YNode =
        if (options.isObject) YMap.empty
        else plainValue(text, options)

      if (options.isArray) YNode(YSequence(node))
      else node
    }

    def plainValue(text: String, options: SuggestionOptions): YNode = {
      val yType =
        if (options.rangeKing == NumberScalarRange) YType.Int
        else if (options.rangeKing == BoolScalarRange) YType.Bool
        else YType.Str

      YNode(YScalar(text), yType)
    }
  }
}
