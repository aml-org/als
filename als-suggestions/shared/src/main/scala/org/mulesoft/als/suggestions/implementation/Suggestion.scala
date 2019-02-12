package org.mulesoft.als.suggestions.implementation

import org.mulesoft.als.suggestions.interfaces.ISuggestion

class Suggestion(_text: String, _description: String, _displayText: String, _prefix: String) extends ISuggestion {

  private var categoryOpt: Option[String] = None

  private var _trailingWhitespace: String = ""

  override def text: String = _text

  override def description: String = _description

  override def displayText: String = _displayText

  override def prefix: String = _prefix

  override def category: String = categoryOpt.getOrElse("unknown")

  override def trailingWhitespace: String = _trailingWhitespace

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
  def apply(_text: String, _description: String, _displayText: String, _prefix: String): Suggestion =
    // Dots are not considered as part of the word to be replaced
    if (_prefix.contains("."))
      if (_prefix.endsWith("."))
        new Suggestion(_text.split('.').last, _description, _displayText.split('.').last, "")
      else new Suggestion(_text.split('.').last, _description, _displayText.split('.').last, _prefix.split('.').last)
    else new Suggestion(_text, _description, _displayText, _prefix)
}
