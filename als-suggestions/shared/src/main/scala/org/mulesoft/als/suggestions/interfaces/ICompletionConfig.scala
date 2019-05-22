package org.mulesoft.als.suggestions.interfaces

import amf.core.remote.Platform
import org.mulesoft.als.common.DirectoryResolver

trait ICompletionConfig {

  def astProvider: Option[IASTProvider]

  def editorStateProvider: Option[IEditorStateProvider]

  def directoryResolver: DirectoryResolver

  def platform: Platform

  def originalContent: Option[String]

  def withAstProvider(obj: IASTProvider): ICompletionConfig

  def withEditorStateProvider(obj: IEditorStateProvider): ICompletionConfig

  def withOriginalContent(str: String): ICompletionConfig
}
