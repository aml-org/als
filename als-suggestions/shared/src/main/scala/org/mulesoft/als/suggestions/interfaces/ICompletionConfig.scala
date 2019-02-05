package org.mulesoft.als.suggestions.interfaces

import org.mulesoft.high.level.implementation.AlsPlatform

trait ICompletionConfig {

  def astProvider: Option[IASTProvider]

  def editorStateProvider: Option[IEditorStateProvider]

  def platform: AlsPlatform

  def originalContent: Option[String]

  def withAstProvider(obj: IASTProvider): ICompletionConfig

  def withEditorStateProvider(obj: IEditorStateProvider): ICompletionConfig

  def withOriginalContent(str: String): ICompletionConfig
}
