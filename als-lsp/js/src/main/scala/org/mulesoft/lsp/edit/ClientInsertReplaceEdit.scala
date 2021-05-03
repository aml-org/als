package org.mulesoft.lsp.edit

import org.mulesoft.lsp.convert.LspConvertersSharedToClient.ClientRangeConverter
import org.mulesoft.lsp.feature.common.ClientRange

import scala.scalajs.js
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientInsertReplaceEdit extends js.Object {
  def newText: String      = js.native
  def insert: ClientRange  = js.native
  def replace: ClientRange = js.native
}

object ClientInsertReplaceEdit {
  def apply(internal: InsertReplaceEdit): ClientInsertReplaceEdit =
    js.Dynamic
      .literal(newText = internal.newText, insert = internal.insert.toClient, replace = internal.replace.toClient)
      .asInstanceOf[ClientInsertReplaceEdit]
}

// $COVERAGE-ON$
