package org.mulesoft.lsp.configuration

import scala.scalajs.js
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientFormattingOptions extends js.Object {
  def tabSize: Int = js.native
  @deprecated
  def insertSpaces: js.UndefOr[Boolean] = js.native
  def preferSpaces: js.UndefOr[Boolean] = js.native
}

object ClientFormattingOptions {
  def apply(internal: FormattingOptions): ClientFormattingOptions = {
    js.Dynamic
      .literal(
        tabSize = internal.indentationSize,
        preferSpaces = internal.insertSpaces,
        insertSpaces = internal.insertSpaces // deprecated
      )
      .asInstanceOf[ClientFormattingOptions]
  }
}

// $COVERAGE-ON$
