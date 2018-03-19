package org.mulesoft.language.server.server.modules.commonInterfaces


trait IAbstractTextEditor {

  /**
    * Complete text of the document opened in the editor.
    */
  var text: String

  /**
    * Text buffer for the editor.
    */
  def buffer: IEditorTextBuffer

  /**
    * Gets file path.
    */
  def path: String

  /**
    * Returns document version, if any.
    */
  def version: Int
}
