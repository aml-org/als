package org.mulesoft.als.configuration

/**
  * Value-object describing what options formatting should use.
  * @param tabSize Size of a tab in spaces.
  * @param insertSpaces Prefer spaces over tabs.
  * @param trimTrailingWhitespace Trim trailing whitespace on a line.
  * @param insertFinalNewline Insert a newline character at the end of the file if one does not exist.
  * @param trimFinalNewlines Trim all newlines after the final newline at the end of the file.
  */
case class AlsFormattingOptions(tabSize: Int,
                                insertSpaces: Boolean,
                                trimTrailingWhitespace: Option[Boolean] = None,
                                insertFinalNewline: Option[Boolean] = None,
                                trimFinalNewlines: Option[Boolean] = None)

object DefaultAlsFormattingOptions extends AlsFormattingOptions(2, true)