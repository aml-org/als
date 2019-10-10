package org.mulesoft.als.suggestions

import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.lsp.edit.TextEdit
import org.mulesoft.lsp.feature.completion.InsertTextFormat

case class SuggestionOptions(rangeKing: RangeKind, isKey: Boolean = false) {

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

case class RawSuggestion(newText: String,
                         displayText: String,
                         description: String,
                         textEdits: Seq[TextEdit],
                         whiteSpacesEnding: String,
                         category: String = "unknown",
                         range: Option[PositionRange] = None,
                         options: SuggestionOptions = SuggestionOptions(),
                         sons: Seq[RawSuggestion] = Nil) {

  implicit def bool2InsertTextFormat(v: Boolean): InsertTextFormat.Value =
    if (v) InsertTextFormat.Snippet
    else InsertTextFormat.PlainText
}

object RawSuggestion {
  def forKey(value: String): RawSuggestion = {
    apply(value, "", isAKey = true, "unknown")
  }

  def forKey(value: String, category: String): RawSuggestion = {
    apply(value, "", isAKey = true, category = category)
  }

  def apply(value: String, isAKey: Boolean): RawSuggestion = {
    apply(value, "", isAKey, "unknown")
  }

  def apply(value: String, isAKey: Boolean, range: PositionRange): RawSuggestion = {
    apply(value, "", isAKey, "unknown", Some(range))
  }

  def apply(value: String, ws: String, isAKey: Boolean, category: String): RawSuggestion = {
    new RawSuggestion(value, value, value, Seq(), ws, category, options = SuggestionOptions(isKey = isAKey))
  }

  def keyOfArray(text: String, ws: String, category: String): RawSuggestion = {
    new RawSuggestion(text, text, text, Nil, ws, category, None, SuggestionOptions(arrayProperty = true, isKey = true))
  }

  def valueInArray(text: String, ws: String, description: String, category: String, isKey: Boolean): RawSuggestion = {
    new RawSuggestion(text, text, text, Nil, ws, category, None, SuggestionOptions(arrayItem = true, isKey = isKey))
  }

  def apply(value: String,
            ws: String,
            isAKey: Boolean,
            category: String,
            range: Option[PositionRange]): RawSuggestion = {
    new RawSuggestion(value, value, value, Seq(), ws, category, range, SuggestionOptions(isKey = isAKey))
  }
}
