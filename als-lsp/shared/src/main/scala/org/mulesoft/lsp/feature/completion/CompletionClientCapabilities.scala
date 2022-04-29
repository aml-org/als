package org.mulesoft.lsp.feature.completion

import org.mulesoft.lsp.feature.completion.CompletionItemKind.CompletionItemKind

/** Capabilities specific to the `textDocument/completion`
  *
  * @param dynamicRegistration
  *   Whether completion supports dynamic registration.
  * @param completionItem
  *   The client supports the following `CompletionItem` specific capabilities.
  * @param completionItemKind
  *   The client supports the following `CompletionItemKind` specific capabilities
  * @param contextSupport
  *   The client supports to send additional context information for a `textDocument/completion` request.
  */
case class CompletionClientCapabilities(
    dynamicRegistration: Option[Boolean] = None,
    completionItem: Option[CompletionItemClientCapabilities] = None,
    completionItemKind: Option[CompletionItemKindClientCapabilities] = None,
    contextSupport: Option[Boolean]
)

/** @param snippetSupport
  *   The client supports snippets as insert text.
  *
  * A snippet can define tab stops and placeholders with `$1`, `$2` and `${3:foo}`. `$0` defines the final tab stop, it
  * defaults to the end of the snippet. Placeholders with equal identifiers are linked, that is typing in one will
  * update others too.
  * @param commitCharactersSupport
  *   The client supports commit characters on a completion item.
  * @param deprecatedSupport
  *   The client supports the deprecated property on a completion item.
  * @param preselectSupport
  *   The client supports the preselect property on a completion item.
  */

case class CompletionItemClientCapabilities(
    snippetSupport: Option[Boolean],
    commitCharactersSupport: Option[Boolean],
    deprecatedSupport: Option[Boolean],
    preselectSupport: Option[Boolean]
)

/** @param valueSet
  *   The completion item kind values the client supports. When this property exists the client also guarantees that it
  *   will handle values outside its set gracefully and falls back to a default value when unknown.
  *
  * If this property is not present the client only supports the completion items kinds from `Text` to `Reference` as
  * defined in the initial version of the protocol.
  */

case class CompletionItemKindClientCapabilities(valueSet: Set[CompletionItemKind])
