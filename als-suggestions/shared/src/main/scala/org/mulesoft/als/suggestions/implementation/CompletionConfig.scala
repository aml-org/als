package org.mulesoft.als.suggestions.implementation

import org.mulesoft.als.suggestions.interfaces.{
  IASTProvider,
  ICompletionConfig,
  IEditorStateProvider,
  IExtendedFSProvider
}
import org.mulesoft.high.level.implementation.AlsPlatform

class CompletionConfig(_platform: AlsPlatform) extends ICompletionConfig {

  var _astProvider: Option[IASTProvider] = None

  var _editorStateProvider: Option[IEditorStateProvider] = None

  var _originalContent: Option[String] = None

  override def astProvider: Option[IASTProvider] = _astProvider

  override def editorStateProvider: Option[IEditorStateProvider] = _editorStateProvider

  override def platform: AlsPlatform = _platform

  override def originalContent: Option[String] = _originalContent

  override def withAstProvider(obj: IASTProvider): CompletionConfig = {
    _astProvider = Option(obj)
    this
  }

  override def withEditorStateProvider(obj: IEditorStateProvider): CompletionConfig = {
    _editorStateProvider = Option(obj)
    this
  }

  override def withOriginalContent(str: String): CompletionConfig = {
    _originalContent = Option(str)
    this
  }
}
