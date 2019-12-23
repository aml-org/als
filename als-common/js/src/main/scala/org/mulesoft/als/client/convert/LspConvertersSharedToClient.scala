package org.mulesoft.als.client.convert

import org.mulesoft.als.client.lsp.feature.documentsymbol.ClientSymbolKindClientCapabilities
import org.mulesoft.lsp.feature.documentsymbol.SymbolKindClientCapabilities

import scala.language.implicitConversions
import scala.scalajs.js
import scala.scalajs.js.JSConverters._

object LspConvertersSharedToClient {

  implicit def symbolKindClientCapabilitiesToShared(
      shared: SymbolKindClientCapabilities): ClientSymbolKindClientCapabilities = {
    new ClientSymbolKindClientCapabilities {
      override def valueSet: js.Array[Int] = shared.valueSet.map(_.id).toJSArray
    }
  }
}
