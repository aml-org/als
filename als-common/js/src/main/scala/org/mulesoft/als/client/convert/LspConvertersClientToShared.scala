package org.mulesoft.als.client.convert

import org.mulesoft.als.client.lsp.feature.completion.ClientCompletionItem
import org.mulesoft.als.client.lsp.feature.documentsymbol.ClientSymbolKindClientCapabilities
import org.mulesoft.lsp.feature.completion.CompletionItem
import org.mulesoft.lsp.feature.documentsymbol.{SymbolKind, SymbolKindClientCapabilities}

import scala.language.implicitConversions

object LspConvertersClientToShared {
  //
  //  implicit def completionItemToShared(client: ClientCompletionItem): CompletionItem = {
  //    CompletionItem(client.label, )
  //  }

  implicit def symbolKindClientCapabilitiesToShared(
      client: ClientSymbolKindClientCapabilities): SymbolKindClientCapabilities = {
    SymbolKindClientCapabilities(client.valueSet.map(i => SymbolKind.File).toSet) // todo: fixme
  }

}
