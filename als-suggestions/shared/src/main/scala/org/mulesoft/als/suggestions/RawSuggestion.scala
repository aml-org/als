package org.mulesoft.als.suggestions

import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.lsp.edit.TextEdit

case class RawSuggestion(newText: String,
                         displayText: String,
                         description: String,
                         textEdits: Seq[TextEdit],
                         category: String = "unknown",
                         range: Option[PositionRange] = None,
                         options: SuggestionStructure = SuggestionStructure(),
                         children: Seq[RawSuggestion] = Seq.empty) {

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

  def withChildren(children: Seq[RawSuggestion]): RawSuggestion =
    RawSuggestion(
      this.newText,
      this.displayText,
      this.description,
      this.textEdits,
      this.category,
      this.range,
      this.options,
      children
    )
}

object RawSuggestion {
  def arrayProp(text: String, category: String): RawSuggestion =
    RawSuggestion(text,
                  text,
                  text,
                  Seq.empty,
                  category,
                  options = SuggestionStructure(rangeKind = ArrayRange, isKey = true))

  def plain(text: String, description: String): RawSuggestion =
    RawSuggestion(text, text, description, Seq.empty, options = SuggestionStructure(rangeKind = PlainText))

  def plain(text: String, range: PositionRange): RawSuggestion =
    RawSuggestion(text,
                  text,
                  text,
                  Seq.empty,
                  range = Some(range),
                  options = SuggestionStructure(rangeKind = PlainText))

  def forBool(value: String, category: String = "unknown"): RawSuggestion =
    apply(value,
          value,
          value,
          Seq.empty,
          category = category,
          options = SuggestionStructure(rangeKind = BoolScalarRange))

  def forBoolKey(value: String, category: String = "unknown"): RawSuggestion =
    apply(value,
          value,
          value,
          Seq.empty,
          category = category,
          options = SuggestionStructure(rangeKind = BoolScalarRange, isKey = true))

  def forKey(value: String, mandatory: Boolean): RawSuggestion =
    apply(value, isAKey = true, "unknown", mandatory)

  def forKey(value: String,
             category: String,
             mandatory: Boolean,
             displayText: Option[String] = None,
             children: Seq[RawSuggestion] = Seq.empty): RawSuggestion =
    apply(value, isAKey = true, category = category, mandatory = mandatory, displayText, children)

  def apply(value: String, isAKey: Boolean): RawSuggestion =
    apply(value, isAKey, "unknown", mandatory = false)

  def apply(value: String,
            isAKey: Boolean,
            category: String,
            mandatory: Boolean,
            displayText: Option[String],
            children: Seq[RawSuggestion]): RawSuggestion =
    new RawSuggestion(value,
                      displayText.getOrElse(value),
                      value,
                      Seq.empty,
                      category,
                      options = SuggestionStructure(isKey = isAKey, isMandatory = mandatory),
                      children = children)

  def apply(value: String, isAKey: Boolean, category: String, mandatory: Boolean): RawSuggestion =
    new RawSuggestion(value,
                      value,
                      value,
                      Seq.empty,
                      category,
                      options = SuggestionStructure(isKey = isAKey, isMandatory = mandatory))

  def apply(value: String, displayText: String, isAKey: Boolean, category: String, mandatory: Boolean): RawSuggestion =
    new RawSuggestion(value,
                      displayText,
                      value,
                      Seq.empty,
                      category,
                      options = SuggestionStructure(isKey = isAKey, isMandatory = mandatory))

  def forObject(value: String,
                category: String,
                mandatory: Boolean = false,
                displayText: Option[String] = None,
                children: Seq[RawSuggestion] = Seq.empty): RawSuggestion =
    new RawSuggestion(
      value,
      displayText.getOrElse(value),
      value,
      Seq.empty,
      category,
      options = SuggestionStructure(isKey = true, rangeKind = ObjectRange, isMandatory = mandatory),
      children = children
    )

  def keyOfArray(text: String, category: String): RawSuggestion =
    new RawSuggestion(text, text, text, Seq.empty, category, None, SuggestionStructure(ArrayRange, isKey = true))

  def keyOfArray(text: String,
                 category: String,
                 displayText: Option[String] = None,
                 children: Seq[RawSuggestion] = Seq.empty): RawSuggestion =
    new RawSuggestion(text,
                      displayText.getOrElse(text),
                      text,
                      Seq.empty,
                      category,
                      None,
                      SuggestionStructure(ArrayRange, isKey = true),
                      children)

  def valueInArray(text: String, description: String, category: String, isKey: Boolean): RawSuggestion =
    new RawSuggestion(text, text, text, Seq.empty, category, None, SuggestionStructure(ArrayRange, isKey = isKey))

  def withNamedKey(children: Seq[RawSuggestion], category: String, prefix: String, name: String): RawSuggestion =
    RawSuggestion
      .forObject("", category, mandatory = true, Some(s"$prefix $name"), children)
}
