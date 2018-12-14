package org.mulesoft.language.server.modules.commonInterfaces

/**
  * Text editor buffer.
  */
trait IEditorTextBuffer {

  /**
    * Gets position by the offset from the beginning of the document.
    * @param offset
    */
  def positionForCharacterIndex(offset: Int): IPoint

  /**
    * Gets offset from the beginning of the document by the position
    * @param position
    */
  def characterIndexForPosition(position: IPoint): Int

  /**
    * Gets a range for the row number.
    * @param row - row number
    * @param includeNewline - whether to include new line character(s).
    */
  def rangeForRow(row: Int, includeNewline: Boolean): IRange

  /**
    * Gets text in range.
    * @param range
    */
  def getTextInRange(range: IRange): String

  /**
    * Gets line number by offset
    * @param offset
    * @return
    */
  def lineByOffset(offset: Int): Int

  /**
    * Sets (replacing if needed) text in range
    * @param range - text range
    * @param text - text to set
    * @param normalizeLineEndings - whether to convert line endings to the ones standard for this document.
    */
  def setTextInRange(range: IRange, text: String, normalizeLineEndings: Boolean = false): Unit

  /**
    * Returns buffer text.
    */
  def getText(): String

  /**
    * Gets buffer end.
    */
  def getEndPosition(): IPoint
}
