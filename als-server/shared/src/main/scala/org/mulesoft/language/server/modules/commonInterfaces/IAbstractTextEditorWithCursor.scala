package org.mulesoft.language.server.modules.commonInterfaces

/**
  * Abstract text editor, being able to provide current cursor posisiton in buffer terms.
  */
trait IAbstractTextEditorWithCursor extends IAbstractTextEditor {

  /**
    * Returns current cursor position
    */
  def cursorBufferPosition: IPoint

  /**
    * Current cursor position, integer, starting from 0
    */
  def cursorPosition: Int
}
