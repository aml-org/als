package org.mulesoft.als.suggestions

import org.mulesoft.als.suggestions.implementation.Suggestion
import org.mulesoft.lsp.edit.TextEdit
import org.mulesoft.lsp.feature.completion.InsertTextFormat

case class RawSuggestion(newText: String,
                         displayText: String,
                         description: String,
                         textEdits: Seq[TextEdit],
                         isKey: Boolean,
                         whiteSpacesEnding: String,
                         isSnippet: Boolean = false) {

  implicit def bool2InsertTextFormat(v: Boolean): InsertTextFormat.Value =
    if (v) InsertTextFormat.Snippet
    else InsertTextFormat.PlainText

  def toSuggestion(linePrefix: String): Suggestion =
    new Suggestion(newText, description, displayText, linePrefix, None)
      .withTrailingWhitespace(whiteSpacesEnding)
      .withInsertTextFormat(isSnippet)
}

object RawSuggestion {
  def forKey(value: String): RawSuggestion = {
    apply(value, "", isAKey = true)
  }

  def apply(value: String, isAKey: Boolean): RawSuggestion = {
    apply(value, "", isAKey)
  }

  def apply(value: String, ws: String, isAKey: Boolean): RawSuggestion = {
    new RawSuggestion(value, value, value, Seq(), isAKey, ws)
  }
}
