package org.mulesoft.als.server.protocol.configuration

import org.mulesoft.als.configuration.AlsFormattingOptions

import scala.scalajs.js
import org.mulesoft.lsp.convert.LspConvertersSharedToClient._
import org.mulesoft.als.server.protocol.convert.LspConvertersSharedToClient._
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientAlsFormattingOptions extends js.Object {
  def tabSize: Int          = js.native
  def insertSpaces: Boolean = js.native
}

object ClientAlsFormattingOptions {
  def apply(internal: AlsFormattingOptions): ClientAlsFormattingOptions = {
    js.Dynamic
      .literal(
        tabSize = internal.indentationSize,
        insertSpaces = internal.insertSpaces
      )
      .asInstanceOf[ClientAlsFormattingOptions]
  }
}

// $COVERAGE-ON$
