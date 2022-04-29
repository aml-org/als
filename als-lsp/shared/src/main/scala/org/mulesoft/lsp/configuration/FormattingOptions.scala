package org.mulesoft.lsp.configuration

/** Value-object describing what options formatting should use.
  *
  * @param tabSize
  *   Size of a tab in spaces.
  * @param insertSpaces
  *   Prefer spaces over tabs.
  * @param trimTrailingWhitespace
  *   Trim trailing whitespace on a line.
  * @param insertFinalNewline
  *   Insert a newline character at the end of the file if one does not exist.
  * @param trimFinalNewlines
  *   Trim all newlines after the final newline at the end of the file.
  */
case class FormattingOptions(
    override val tabSize: Int,
    override val insertSpaces: Boolean,
    private val trimTrailingWhitespace: Option[Boolean] = None,
    private val insertFinalNewline: Option[Boolean] = None,
    private val trimFinalNewlines: Option[Boolean] = None
) extends FormatOptions {
  override def getTrimTrailingWhitespace: Boolean =
    this.trimTrailingWhitespace.getOrElse(DefaultFormattingOptions.getTrimTrailingWhitespace)

  override def getInsertFinalNewline: Boolean =
    this.insertFinalNewline.getOrElse(DefaultFormattingOptions.getInsertFinalNewline)

  override def getTrimFinalNewlines: Boolean =
    this.trimFinalNewlines.getOrElse(DefaultFormattingOptions.getTrimFinalNewlines)
}

trait FormatOptions {
  val tabSize: Int
  val insertSpaces: Boolean
  def getTrimTrailingWhitespace: Boolean
  def getInsertFinalNewline: Boolean
  def getTrimFinalNewlines: Boolean
}

object DefaultFormattingOptions extends FormatOptions {
  override val tabSize: Int                       = 2
  override val insertSpaces: Boolean              = true
  override def getTrimTrailingWhitespace: Boolean = true
  override def getInsertFinalNewline: Boolean     = false
  override def getTrimFinalNewlines: Boolean      = true
}
