package org.mulesoft.als.actions.codeactions.plugins.declarations.resourcetype

import org.mulesoft.als.actions.codeactions.plugins.CodeActionKindTitle
import org.mulesoft.lsp.feature.codeactions.CodeActionKind
import org.mulesoft.lsp.feature.codeactions.CodeActionKind.CodeActionKind

trait ExtractResourceTypeKind extends CodeActionKindTitle {
  override final val kind: CodeActionKind = CodeActionKind.RefactorExtract
  override final val title                = "Extract resource type"
}
