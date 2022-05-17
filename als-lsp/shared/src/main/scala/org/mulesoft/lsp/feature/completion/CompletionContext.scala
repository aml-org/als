package org.mulesoft.lsp.feature.completion

import org.mulesoft.lsp.feature.completion.CompletionTriggerKind.CompletionTriggerKind

/** Contains additional information about the context in which a completion request is triggered.
  *
  * @param triggerKind
  *   How the completion was triggered.
  * @param triggerCharacter
  *   The trigger character that has trigger code complete. Is None if `triggerKind !==
  *   CompletionTriggerKind.TriggerCharacter`
  */
case class CompletionContext(triggerKind: CompletionTriggerKind, triggerCharacter: Option[Char])
