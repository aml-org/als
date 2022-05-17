package org.mulesoft.lsp.feature.completion

/** Completion options.
  *
  * @param resolveProvider
  *   The server provides support to resolve additional information for a completion item.
  * @param triggerCharacters
  *   The characters that trigger completion automatically.
  */
case class CompletionOptions(resolveProvider: Option[Boolean] = None, triggerCharacters: Option[Set[Char]] = None)
