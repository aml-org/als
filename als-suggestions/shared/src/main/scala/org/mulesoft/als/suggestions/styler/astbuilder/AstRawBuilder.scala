package org.mulesoft.als.suggestions.styler.astbuilder

import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.suggestions.{BoolScalarRange, NumberScalarRange, RawSuggestion, SuggestionStructure}
import org.yaml.model._

abstract class AstRawBuilder(raw: RawSuggestion, isSnippet: Boolean, yPartBranch: YPartBranch) {

  protected def newInstance: (RawSuggestion, Boolean) => AstRawBuilder
  protected var snippet: Boolean = false

  def forSnippet(): Unit =
    snippet = true

  def asSnippet: Boolean = snippet

  def ast: YPart

  def emitEntryValue(options: SuggestionStructure): YNode

  def onlyKey(key: String): YPart

  def emitRootKey: YPart =
    if (raw.newText.contains("$")) onlyKey(raw.newText)
    else emitKey()

  lazy val keyTag: YType = if (raw.options.keyRange == NumberScalarRange) YType.Int else YType.Str

  lazy val valueTag: YType = raw.options.rangeKind match {
    case NumberScalarRange => YType.Int
    case BoolScalarRange   => YType.Bool
    case _                 => YType.Str
  }

  private def valueNode(index: Int) = {
    if (raw.options.isObject) valueObject(index)
    else {
      if (isSnippet) value(s"$$$index", raw.options)
      else emitEntryValue(raw.options)
    }
  }

  private def emitKey(index: Int = 0): YMapEntry = YMapEntry(scalar(raw.newText, keyTag), valueNode(index))

  private def valueObject(index: Integer): YNode = {
    val list = if (raw.children.nonEmpty) {
      snippet = true
      raw.children.zipWithIndex
        .map(t => newInstance(t._1, true).emitKey(t._2 + 1))
        .toIndexedSeq
    } else if (isSnippet) IndexedSeq(YMapEntry("$" + index.toString, ""))
    else IndexedSeq.empty

    val n = YNode(YMap(list, ""))

    wrapArray(raw.options, n)
  }

  protected def value(text: String, options: SuggestionStructure): YNode = {
    val node: YNode =
      if (!options.isObject)
        plainValue(text)
      else if (text.isEmpty)
        YMap.empty
      else YMap(IndexedSeq(YMapEntry(raw.newText, "")), "")
    wrapArray(options, node)
  }

  private def wrapArray(options: SuggestionStructure, node: YNode): YNode =
    if (options.isArray && (options.isKey || !yPartBranch.isInArray)) YNode(YSequence(node))
    else node

  private def plainValue(text: String): YNode = {
    val yType = if (valueTag == YType.Str && text.isEmpty) YType.Empty else valueTag
    scalar(text, yType)
  }

  private def scalar(text: String, yType: YType) =
    YNode(if (raw.options.nonPlain) YScalar.nonPlain(text) else YScalar(text), yType)
}
