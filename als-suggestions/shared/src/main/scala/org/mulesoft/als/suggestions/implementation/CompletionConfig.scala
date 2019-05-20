package org.mulesoft.als.suggestions.implementation

import amf.core.remote.Platform
import org.mulesoft.als.common.DirectoryResolver
import org.mulesoft.als.suggestions.interfaces.{IASTProvider, ICompletionConfig, IEditorStateProvider}

class CompletionConfig(val directoryResolver: DirectoryResolver, val platform: Platform) extends ICompletionConfig {

  var _astProvider: Option[IASTProvider] = None

  var _editorStateProvider: Option[IEditorStateProvider] = None

  var _originalContent: Option[String] = None

  override def astProvider: Option[IASTProvider] = _astProvider

  override def editorStateProvider: Option[IEditorStateProvider] = _editorStateProvider

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
