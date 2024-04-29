package org.mulesoft.lsp.feature.documentsymbol

import org.mulesoft.lsp.feature.documentsymbol.SymbolKind.SymbolKind

/** Capabilities specific to the `textDocument/references`
  *
  * @param dynamicRegistration
  *   Whether references supports dynamic registration.
  * @param symbolKind
  *   Specific capabilities for the `SymbolKind`.
  * @param hierarchicalDocumentSymbolSupport
  *   The client supports hierarchical document symbols.
  */

case class DocumentSymbolClientCapabilities(
    dynamicRegistration: Option[Boolean],
    symbolKind: Option[SymbolKindClientCapabilities],
    hierarchicalDocumentSymbolSupport: Option[Boolean]
)

/** Specific capabilities for the `SymbolKind`.
  *
  * @param valueSet
  *   The symbol kind values the client supports. When this property exists the client also guarantees that it will
  *   handle values outside its set gracefully and falls back to a default value when unknown.
  *
  * If this property is not present the client only supports the symbol kinds from `File` to `Array` as defined in the
  * initial version of the protocol.
  */
case class SymbolKindClientCapabilities(valueSet: Set[SymbolKind])
