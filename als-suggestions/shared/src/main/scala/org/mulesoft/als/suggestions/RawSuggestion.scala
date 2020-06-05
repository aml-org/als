package org.mulesoft.als.suggestions

import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.lsp.edit.TextEdit
import org.mulesoft.lsp.feature.completion.InsertTextFormat

case class RawSuggestion(newText: String,
                         displayText: String,
                         description: String,
                         textEdits: Seq[TextEdit],
                         category: String = "unknown",
                         range: Option[PositionRange] = None,
                         options: SuggestionStructure = SuggestionStructure(),
                         children: Seq[RawSuggestion] = Nil) {

  def withStringKey: RawSuggestion =
    RawSuggestion(
      this.newText,
      this.displayText,
      this.description,
      this.textEdits,
      this.category,
      this.range,
      SuggestionStructure(this.options.rangeKind,
                          this.options.isKey,
                          StringScalarRange,
                          this.options.isMandatory,
                          this.options.isTopLevel),
      this.children
    )

  def withPositionRange(positionRange: Option[PositionRange]): RawSuggestion =
    RawSuggestion(
      this.newText,
      this.displayText,
      this.description,
      this.textEdits,
      this.category,
      positionRange,
      this.options,
      this.children
    )

  implicit def bool2InsertTextFormat(v: Boolean): InsertTextFormat.Value =
    if (v) InsertTextFormat.Snippet
    else InsertTextFormat.PlainText
}

object RawSuggestion {
  def arrayProp(text: String, category: String): RawSuggestion =
    RawSuggestion(text, text, text, Nil, category, options = SuggestionStructure(rangeKind = ArrayRange, isKey = true))

  def plain(text: String, description: String): RawSuggestion =
    RawSuggestion(text, text, description, Nil, options = SuggestionStructure(rangeKind = PlainText))

  def plain(text: String, range: PositionRange): RawSuggestion =
    RawSuggestion(text, text, text, Nil, range = Some(range), options = SuggestionStructure(rangeKind = PlainText))

  def forBool(value: String, category: String = "unknown"): RawSuggestion = {
    apply(value, value, value, Nil, category = category, options = SuggestionStructure(rangeKind = BoolScalarRange))
  }

  def forBoolKey(value: String, category: String = "unknown"): RawSuggestion = {
    apply(value,
          value,
          value,
          Nil,
          category = category,
          options = SuggestionStructure(rangeKind = BoolScalarRange, isKey = true))
  }

  def forKey(value: String, mandatory: Boolean): RawSuggestion = {
    apply(value, isAKey = true, "unknown", mandatory)
  }

  def forKey(value: String, category: String, mandatory: Boolean): RawSuggestion = {
    apply(value, isAKey = true, category = category, mandatory = mandatory)
  }

  def apply(value: String, isAKey: Boolean): RawSuggestion = {
    apply(value, isAKey, "unknown", mandatory = false)
  }

  def apply(value: String, isAKey: Boolean, category: String, mandatory: Boolean): RawSuggestion = {
    new RawSuggestion(value,
                      value,
                      value,
                      Seq(),
                      category,
                      options = SuggestionStructure(isKey = isAKey, isMandatory = mandatory))
  }

  def apply(value: String, displayText: String, isAKey: Boolean, category: String, mandatory: Boolean): RawSuggestion = {
    new RawSuggestion(value,
                      displayText,
                      value,
                      Seq(),
                      category,
                      options = SuggestionStructure(isKey = isAKey, isMandatory = mandatory))
  }

  def forObject(value: String, category: String, mandatory: Boolean = false): RawSuggestion = {
    new RawSuggestion(value,
                      value,
                      value,
                      Seq(),
                      category,
                      options = SuggestionStructure(isKey = true, rangeKind = ObjectRange, isMandatory = mandatory))
  }

  def keyOfArray(text: String, category: String): RawSuggestion = {
    new RawSuggestion(text, text, text, Nil, category, None, SuggestionStructure(ArrayRange, isKey = true))
  }

  def valueInArray(text: String, description: String, category: String, isKey: Boolean): RawSuggestion = {
    new RawSuggestion(text, text, text, Nil, category, None, SuggestionStructure(ArrayRange, isKey = isKey))
  }
}
