package org.mulesoft.language.outline.common.commonInterfaces

/**
  * Provider of the active editor
  */
trait IEditorProvider {
  def getCurrentEditor: IAbstractTextEditor
}
