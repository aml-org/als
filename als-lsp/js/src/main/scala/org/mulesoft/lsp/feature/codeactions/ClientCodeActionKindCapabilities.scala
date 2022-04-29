package org.mulesoft.lsp.feature.codeactions

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientCodeActionKindCapabilities extends js.Object {
  def valueSet: js.Array[String] = js.native
}

object ClientCodeActionKindCapabilities {
  def apply(internal: CodeActionKindCapabilities): ClientCodeActionKindCapabilities =
    js.Dynamic
      .literal(valueSet = internal.valueSet.toJSArray)
      .asInstanceOf[ClientCodeActionKindCapabilities]
}
// $COVERAGE-ON$
