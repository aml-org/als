package org.mulesoft.als.actions.codeactions.plugins.declarations.delete

import org.mulesoft.als.actions.codeactions.plugins.CodeActionKindTitle
import org.mulesoft.lsp.feature.codeactions.CodeActionKind
import org.mulesoft.lsp.feature.codeactions.CodeActionKind.CodeActionKind

trait DeleteDeclarationKind extends CodeActionKindTitle {
  override final val kind: CodeActionKind = CodeActionKind.Refactor
  override final val title                = "Delete declaration (Cascade)"
}
