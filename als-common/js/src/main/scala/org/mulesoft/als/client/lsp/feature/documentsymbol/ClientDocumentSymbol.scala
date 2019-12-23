package org.mulesoft.als.client.lsp.feature.documentsymbol

import org.mulesoft.lsp.feature.documentsymbol.SymbolKind.SymbolKind
import org.mulesoft.lsp.common.Range

import scala.scalajs.js

@js.native
trait ClientDocumentSymbol extends js.Object {
  def name: String = js.native

  def kind: SymbolKind = js.native

  def range: Range = js.native

  def selectionRange: Range = js.native

  def children: js.Array[ClientDocumentSymbol] = js.native

  def detail: js.UndefOr[String] = js.native

  def deprecated: js.UndefOr[Boolean] = js.native
}