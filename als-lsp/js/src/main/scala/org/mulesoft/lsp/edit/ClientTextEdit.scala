package org.mulesoft.lsp.edit

import org.mulesoft.lsp.convert.LspConvertersSharedToClient._
import org.mulesoft.lsp.feature.common.ClientRange

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
