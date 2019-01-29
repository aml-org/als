package org.mulesoft.language.server.modules.editorManager

import org.mulesoft.language.common.logger.ILogger
import org.mulesoft.language.server.modules.commonInterfaces.{IAbstractTextEditorWithCursor, IEditorTextBuffer, IPoint}

/**
  * Info regarding single text editor.
  */
class TextEditorInfo(private val uri: String,
                     var version: Int,
                     _text: String,
                     val language: String,
                     val syntax: String,
                     //                      private val editorManager: EditorManager,
                     private val logger: ILogger)
  extends IAbstractTextEditorWithCursor {

  val _buffer: TextBufferInfo = new TextBufferInfo(uri, logger)
  var _cursorPosition: Int = 0

  this._buffer.setText(_text)

  def cursorBufferPosition: IPoint = {

    this._buffer.positionForCharacterIndex(this.cursorPosition)
  }

  def cursorPosition: Int = {
    this._cursorPosition
  }

  def text: String = {

    this._buffer.getText()
  }

  def buffer: IEditorTextBuffer = {

    this._buffer
  }

  def path: String = {

    this.uri
  }

  def text_=(text: String): Unit = {

    this._buffer.setText(text)
  }

  def setCursorPosition(position: Int): Unit = {

    this._cursorPosition = position
  }
}
