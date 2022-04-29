package org.mulesoft.lsp.feature.completion

/** How a completion was triggered
  */
case object CompletionTriggerKind extends Enumeration {
  type CompletionTriggerKind = Value

  /** Completion was triggered by typing an identifier (24x7 code complete), manual invocation (e.g Ctrl+Space) or via
    * API.
    */
  val Invoked: Value = Value(1)

  /** Completion was triggered by a trigger character specified by the `triggerCharacters` properties of the
    * `CompletionRegistrationOptions`.
    */
  val TriggerCharacter: Value = Value(2)

  /** Completion was re-triggered as the current completion list is incomplete.
    */
  val TriggerForIncompleteCompletions: Value = Value(3)
}
