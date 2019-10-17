package org.mulesoft.als.suggestions

import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.lsp.edit.TextEdit
import org.mulesoft.lsp.feature.completion.InsertTextFormat

case class SuggestionOptions(rangeKing: RangeKind = StringScalarRange,
                             isKey: Boolean = false,
                             keyRange: ScalarRange = StringScalarRange) {

  def scalarProperty: Boolean = rangeKing.isInstanceOf[ScalarRange]

  def isArray: Boolean = rangeKing == ArrayRange

  def isObject: Boolean = rangeKing == ObjectRange
}

trait RangeKind

object ObjectRange extends RangeKind
object ArrayRange  extends RangeKind
trait ScalarRange  extends RangeKind

object StringScalarRange extends ScalarRange
object NumberScalarRange extends ScalarRange
object BoolScalarRange   extends ScalarRange
object PlainText         extends RangeKind

case class RawSuggestion(newText: String,
                         displayText: String,
                         description: String,
                         textEdits: Seq[TextEdit],
                         category: String = "unknown",
                         range: Option[PositionRange] = None,
                         options: SuggestionOptions = SuggestionOptions(),
                         sons: Seq[RawSuggestion] = Nil) {

  implicit def bool2InsertTextFormat(v: Boolean): InsertTextFormat.Value =
    if (v) InsertTextFormat.Snippet
    else InsertTextFormat.PlainText
}

object RawSuggestion {
  def arrayProp(text: String, category: String): RawSuggestion =
    RawSuggestion(text, text, text, Nil, category, options = SuggestionOptions(rangeKing = ArrayRange, isKey = true))

  def plain(text: String, description: String): RawSuggestion =
    RawSuggestion(text, text, description, Nil, options = SuggestionOptions(rangeKing = PlainText))

  def plain(text: String, range: PositionRange): RawSuggestion =
    RawSuggestion(text, text, text, Nil, range = Some(range), options = SuggestionOptions(rangeKing = PlainText))

  def forBool(value: String, category: String = "unknown"): RawSuggestion = {
    apply(value, value, value, Nil, category = category, options = SuggestionOptions(rangeKing = BoolScalarRange))
  }

  def forBoolKey(value: String, category: String = "unknown"): RawSuggestion = {
    apply(value,
          value,
          value,
          Nil,
          category = category,
          options = SuggestionOptions(rangeKing = BoolScalarRange, isKey = true))
  }

  def forKey(value: String): RawSuggestion = {
    apply(value, isAKey = true, "unknown")
  }

  def forKey(value: String, category: String): RawSuggestion = {
    apply(value, isAKey = true, category = category)
  }

  def apply(value: String, isAKey: Boolean): RawSuggestion = {
    apply(value, isAKey, "unknown")
  }

  def apply(value: String, isAKey: Boolean, range: PositionRange): RawSuggestion = {
    apply(value, "", isAKey, "unknown", Some(range))
  }

  def apply(value: String, isAKey: Boolean, category: String): RawSuggestion = {
    new RawSuggestion(value, value, value, Seq(), category, options = SuggestionOptions(isKey = isAKey))
  }

  def forObject(value: String, category: String): RawSuggestion = {
    new RawSuggestion(value,
                      value,
                      value,
                      Seq(),
                      category,
                      options = SuggestionOptions(isKey = true, rangeKing = ObjectRange))
  }

  def keyOfArray(text: String, category: String): RawSuggestion = {
    new RawSuggestion(text, text, text, Nil, category, None, SuggestionOptions(ArrayRange, isKey = true))
  }

  def valueInArray(text: String, description: String, category: String, isKey: Boolean): RawSuggestion = {
    new RawSuggestion(text, text, text, Nil, category, None, SuggestionOptions(ArrayRange, isKey = isKey))
  }

  def apply(value: String,
            ws: String,
            isAKey: Boolean,
            category: String,
            range: Option[PositionRange]): RawSuggestion = {
    new RawSuggestion(value, value, value, Seq(), category, range, SuggestionOptions(isKey = isAKey))
  }
}
