package org.mulesoft.als.actions.codeactions.plugins.vocabulary

import org.mulesoft.als.actions.codeactions.plugins.CodeActionKindTitle
import org.mulesoft.lsp.feature.codeactions.CodeActionKind
import org.mulesoft.lsp.feature.codeactions.CodeActionKind.CodeActionKind

trait SynthesizeVocabularyKind extends CodeActionKindTitle {
  override final val kind: CodeActionKind = CodeActionKind.RefactorExtract
  override final val title                = "Synthesize vocabulary for missing terms"
}
