package org.mulesoft.lsp.feature.completion

/** Represents a collection of [completion items](#CompletionItem) to be presented in the editor.
  *
  * @param items
  *   The completion items.
  * @param isIncomplete
  *   This list it not complete. Further typing should result in recomputing this list.
  */
case class CompletionList(items: Seq[CompletionItem], isIncomplete: Boolean)
