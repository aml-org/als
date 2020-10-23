package org.mulesoft.als.actions.codeactions.plugins.base

import org.mulesoft.als.actions.codeactions.plugins.CodeActionKindTitle

trait CodeActionFactory extends CodeActionKindTitle {
  def apply(params: CodeActionRequestParams): CodeActionResponsePlugin
}
