package org.mulesoft.lsp.edit

import org.mulesoft.lsp.convert.LspConvertersSharedToClient._
import org.mulesoft.lsp.feature.common.ClientVersionedTextDocumentIdentifier

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientTextDocumentEdit extends js.Object {
  def textDocument: ClientVersionedTextDocumentIdentifier = js.native
  def edits: js.Array[ClientTextEdit]                     = js.native
}

object ClientTextDocumentEdit {
  def apply(internal: TextDocumentEdit): ClientTextDocumentEdit =
    js.Dynamic
      .literal(textDocument = internal.textDocument.toClient, edits = internal.edits.map(_.toClient).toJSArray)
      .asInstanceOf[ClientTextDocumentEdit]
}

// $COVERAGE-ON$
