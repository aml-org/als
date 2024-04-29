package org.mulesoft.lsp.feature.documentsymbol

import org.mulesoft.lsp.convert.LspConvertersSharedToClient._
import org.mulesoft.lsp.feature.common.ClientRange

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientDocumentSymbol extends js.Object {
  def name: String = js.native

  def kind: Int = js.native

  def range: ClientRange = js.native

  def selectionRange: ClientRange = js.native

  def children: js.Array[ClientDocumentSymbol] = js.native

  def detail: js.UndefOr[String] = js.native

  def deprecated: js.UndefOr[Boolean] = js.native
}

object ClientDocumentSymbol {
  def apply(internal: DocumentSymbol): ClientDocumentSymbol =
    js.Dynamic
      .literal(
        name = internal.name,
        kind = internal.kind.id,
        range = internal.range.toClient,
        selectionRange = internal.selectionRange.toClient,
        children = internal.children.map(_.toClient).toJSArray,
        detail = internal.detail.orUndefined,
        deprecated = internal.deprecated.orUndefined
      )
      .asInstanceOf[ClientDocumentSymbol]
}

// $COVERAGE-ON$
