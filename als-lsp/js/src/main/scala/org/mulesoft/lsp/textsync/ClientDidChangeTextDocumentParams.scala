package org.mulesoft.lsp.textsync

import org.mulesoft.lsp.feature.common.ClientVersionedTextDocumentIdentifier
import org.mulesoft.lsp.convert.LspConvertersSharedToClient.{
  ClientVersionedTextDocumentIdentifierConverter,
  ClientTextDocumentContentChangeEventConverter
}
import scala.scalajs.js
import scala.scalajs.js.JSConverters._
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientDidChangeTextDocumentParams extends js.Object {
  def textDocument: ClientVersionedTextDocumentIdentifier            = js.native
  def contentChanges: js.Array[ClientTextDocumentContentChangeEvent] = js.native
}

object ClientDidChangeTextDocumentParams {
  def apply(internal: DidChangeTextDocumentParams): ClientDidChangeTextDocumentParams =
    js.Dynamic
      .literal(
        textDocument = internal.textDocument.toClient,
        contentChanges = internal.contentChanges.map(_.toClient).toJSArray
      )
      .asInstanceOf[ClientDidChangeTextDocumentParams]
}

// $COVERAGE-ON$
