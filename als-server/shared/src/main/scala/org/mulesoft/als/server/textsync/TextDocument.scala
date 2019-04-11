package org.mulesoft.als.server.textsync

import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.modules.common.interfaces.{IAbstractTextEditorWithCursor, IEditorTextBuffer, IPoint}

/**
  * Info regarding single text editor.
  */
class TextDocument(private val uri: String,
                   var version: Int,
                   _text: String,
                   val language: String,
                   val syntax: String,
                   private val logger: Logger)

  extends IAbstractTextEditorWithCursor {

  val _buffer: TextBufferInfo = new TextBufferInfo(uri, logger)
  var _cursorPosition: Int = 0

  this._buffer.setText(_text)

  def cursorBufferPosition: IPoint = _buffer.positionForCharacterIndex(this.cursorPosition)

  def cursorPosition: Int = _cursorPosition

  def text: String = _buffer.getText()

  def buffer: IEditorTextBuffer = _buffer

  def path: String = uri

  def text_=(text: String): Unit = _buffer.setText(text)

  def setCursorPosition(position: Int): Unit = _cursorPosition = position
}
