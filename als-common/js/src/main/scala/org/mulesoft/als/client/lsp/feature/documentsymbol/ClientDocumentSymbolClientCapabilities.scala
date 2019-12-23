package org.mulesoft.als.client.lsp.feature.documentsymbol

import scala.scalajs.js

@js.native
trait ClientDocumentSymbolClientCapabilities extends js.Object {
  def dynamicRegistration: js.UndefOr[Boolean]                   = js.native
  def symbolKind: js.UndefOr[ClientSymbolKindClientCapabilities] = js.native
  def hierarchicalDocumentSymbolSupport: js.UndefOr[Boolean]     = js.native
}

@js.native
trait ClientSymbolKindClientCapabilities extends js.Object {
  def valueSet: js.Array[Int] = js.native
}
