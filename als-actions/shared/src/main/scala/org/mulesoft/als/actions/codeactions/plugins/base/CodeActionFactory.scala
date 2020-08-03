package org.mulesoft.als.actions.codeactions.plugins.base

import org.mulesoft.lsp.feature.codeactions.CodeActionKind.CodeActionKind

trait CodeActionFactory {
  val kind: CodeActionKind
  def apply(params: CodeActionRequestParams): CodeActionResponsePlugin
}
