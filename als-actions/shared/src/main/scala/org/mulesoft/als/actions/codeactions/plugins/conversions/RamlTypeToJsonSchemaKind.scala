package org.mulesoft.als.actions.codeactions.plugins.conversions

import org.mulesoft.als.actions.codeactions.plugins.CodeActionKindTitle
import org.mulesoft.lsp.feature.codeactions.CodeActionKind
import org.mulesoft.lsp.feature.codeactions.CodeActionKind.CodeActionKind

trait RamlTypeToJsonSchemaKind extends CodeActionKindTitle {
  override final val kind: CodeActionKind = CodeActionKind.Refactor
  override final val title                = "Convert to Json schema"
}
