package org.mulesoft.als.suggestions.implementation

import org.mulesoft.als.suggestions.interfaces.{IASTProvider, ICompletionConfig, IEditorStateProvider, IExtendedFSProvider}

class CompletionConfig extends ICompletionConfig {

  var _astProvider: Option[IASTProvider] = None

  var _editorStateProvider: Option[IEditorStateProvider] = None

  var _fsProvider: Option[IExtendedFSProvider] = None

  var _originalContent: Option[String] = None

  override def astProvider: Option[IASTProvider] = _astProvider

  override def editorStateProvider: Option[IEditorStateProvider] = _editorStateProvider

  override def fsProvider: Option[IExtendedFSProvider] = _fsProvider

  override def originalContent: Option[String] = _originalContent

  override def withAstProvider(obj: IASTProvider): CompletionConfig = {
    _astProvider = Option(obj)
    this
  }

  override def withEditorStateProvider(obj: IEditorStateProvider): CompletionConfig = {
    _editorStateProvider = Option(obj)
    this
  }

  override def withFsProvider(obj: IExtendedFSProvider): CompletionConfig = {
    _fsProvider = Option(obj)
    this
  }

  override def withOriginalContent(str: String): CompletionConfig = {
    _originalContent = Option(str)
    this
  }
}
