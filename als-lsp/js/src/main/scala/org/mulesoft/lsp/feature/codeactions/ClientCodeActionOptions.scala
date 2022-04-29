package org.mulesoft.lsp.feature.codeactions

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientCodeActionOptions extends js.Object {
  def codeActionKinds: js.UndefOr[js.Array[String]] = js.native
}

object ClientCodeActionOptions {
  def apply(internal: CodeActionOptions): ClientCodeActionOptions =
    js.Dynamic
      .literal(
        codeActionKinds = internal.codeActionKinds.map(_.map(_.toString).toJSArray).orUndefined
      )
      .asInstanceOf[ClientCodeActionOptions]
}

// $COVERAGE-ON$
