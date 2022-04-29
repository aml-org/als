package org.mulesoft.lsp.feature.documentsymbol

import org.mulesoft.lsp.convert.LspConvertersSharedToClient._

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientDocumentSymbolClientCapabilities extends js.Object {
  def dynamicRegistration: js.UndefOr[Boolean]                   = js.native
  def symbolKind: js.UndefOr[ClientSymbolKindClientCapabilities] = js.native
  def hierarchicalDocumentSymbolSupport: js.UndefOr[Boolean]     = js.native
}

object ClientDocumentSymbolClientCapabilities {
  def apply(internal: DocumentSymbolClientCapabilities): ClientDocumentSymbolClientCapabilities =
    js.Dynamic
      .literal(
        dynamicRegistration = internal.dynamicRegistration.orUndefined,
        symbolKind = internal.symbolKind.map(_.toClient).orUndefined,
        hierarchicalDocumentSymbolSupport = internal.hierarchicalDocumentSymbolSupport.orUndefined
      )
      .asInstanceOf[ClientDocumentSymbolClientCapabilities]
}

@js.native
trait ClientSymbolKindClientCapabilities extends js.Object {
  def valueSet: js.Array[Int] = js.native
}

object ClientSymbolKindClientCapabilities {
  def apply(internal: SymbolKindClientCapabilities): ClientSymbolKindClientCapabilities =
    js.Dynamic
      .literal(valueSet = internal.valueSet.map(_.id).toJSArray)
      .asInstanceOf[ClientSymbolKindClientCapabilities]
}

// $COVERAGE-ON$
