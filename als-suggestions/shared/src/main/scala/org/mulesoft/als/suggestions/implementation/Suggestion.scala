package org.mulesoft.als.suggestions.implementation

import common.dtoTypes.PositionRange
import org.mulesoft.als.suggestions.interfaces.{Suggestion => SuggestionInterface}

class Suggestion(_text: String,
                 _description: String,
                 _displayText: String,
                 _prefix: String,
                 _range: Option[PositionRange])
    extends SuggestionInterface {

  private var categoryOpt: Option[String] = None

  private var _trailingWhitespace: String = ""

  override def text: String = _text

  override def description: String = _description

  override def displayText: String = _displayText

  override def prefix: String = _prefix

  override def category: String = categoryOpt.getOrElse(description)

  override def trailingWhitespace: String = _trailingWhitespace

  override def range: Option[PositionRange] = _range

  def withCategory(cat: String): Suggestion = {
    categoryOpt = Option(cat)
    this
  }

  def withTrailingWhitespace(ws: String): Suggestion = {
    _trailingWhitespace = ws
    this
  }

  override def toString: String = text
}

object Suggestion {
  def apply(_text: String,
            _description: String,
            _displayText: String,
            _prefix: String,
            _range: Option[PositionRange] = None): Suggestion = {
    // Dots and dashes are not considered as part of the word to be replaced
    val index =
      _prefix.lastIndexOf(".").max(_prefix.lastIndexOf("/"))
    if (index > 0 && _text.startsWith(_prefix))
      if (index == _prefix.size)
        new Suggestion(_text.substring(index), _description, _displayText.split('.').last, "", _range)
      else
        new Suggestion(_text.substring(index + 1),
                       _description,
                       _displayText.substring(index + 1),
                       _prefix.substring(index + 1),
                       _range)
    else new Suggestion(_text, _description, _displayText, _prefix, _range)
  }
}
