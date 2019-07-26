package org.mulesoft.als.suggestions

import org.mulesoft.als.suggestions.implementation.Suggestion
import org.mulesoft.lsp.edit.TextEdit

case class RawSuggestion(newText: String,
                         displayText: String,
                         description: String,
                         textEdits: Seq[TextEdit],
                         isKey: Boolean,
                         whiteSpacesEnding: String) {

  def toSuggestion(linePrefix: String): Suggestion =
    new Suggestion(newText, description, displayText, linePrefix, None).withTrailingWhitespace(whiteSpacesEnding)
}

object RawSuggestion {
  def apply(value: String, isAKey: Boolean): RawSuggestion = {
    apply(value, "", isAKey)
  }

  def apply(value: String, ws: String, isAKey: Boolean): RawSuggestion = {
    new RawSuggestion(value, value, value, Seq(), isAKey, ws)
  }
}
