package org.mulesoft.als.client.lsp.edit

import org.mulesoft.als.client.lsp.common.ClientRange
import org.mulesoft.lsp.edit.TextEdit
import org.mulesoft.als.client.convert.LspConvertersSharedToClient._

import scala.scalajs.js
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientTextEdit extends js.Object {
  def range: ClientRange = js.native
  def newText: String    = js.native
}

object ClientTextEdit {
  def apply(internal: TextEdit): ClientTextEdit =
    js.Dynamic
      .literal(range = internal.range.toClient, newText = internal.newText)
      .asInstanceOf[ClientTextEdit]
}

// $COVERAGE-ON$