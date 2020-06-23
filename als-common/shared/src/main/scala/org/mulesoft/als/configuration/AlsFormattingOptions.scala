package org.mulesoft.als.configuration

/**
  * Value-object describing what options formatting should use.
  *
  * @param indentationSize Size of a tab in spaces.
  * @param insertSpaces Prefer spaces over tabs.
  * @param trimTrailingWhitespace Trim trailing whitespace on a line.
  * @param insertFinalNewline Insert a newline character at the end of the file if one does not exist.
  * @param trimFinalNewlines Trim all newlines after the final newline at the end of the file.
  */
case class AlsFormattingOptions(override val indentationSize: Int,
                                override val insertSpaces: Boolean,
                                private val trimTrailingWhitespace: Option[Boolean] = None,
                                private val insertFinalNewline: Option[Boolean] = None,
                                private val trimFinalNewlines: Option[Boolean] = None)
    extends AlsFormatOptions {
  override def getTrimTrailingWhitespace: Boolean =
    this.trimTrailingWhitespace.getOrElse(DefaultAlsFormattingOptions.getTrimTrailingWhitespace)

  override def getInsertFinalNewline: Boolean =
    this.insertFinalNewline.getOrElse(DefaultAlsFormattingOptions.getInsertFinalNewline)

  override def getTrimFinalNewlines: Boolean =
    this.trimFinalNewlines.getOrElse(DefaultAlsFormattingOptions.getTrimFinalNewlines)
}

trait AlsFormatOptions {
  val indentationSize: Int
  val insertSpaces: Boolean
  def getTrimTrailingWhitespace: Boolean
  def getInsertFinalNewline: Boolean
  def getTrimFinalNewlines: Boolean
}

object DefaultAlsFormattingOptions extends AlsFormatOptions {
  override val indentationSize: Int               = 2
  override val insertSpaces: Boolean              = true
  override def getTrimTrailingWhitespace: Boolean = true
  override def getInsertFinalNewline: Boolean     = false
  override def getTrimFinalNewlines: Boolean      = true
}
