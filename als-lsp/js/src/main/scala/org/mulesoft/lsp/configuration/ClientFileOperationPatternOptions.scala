package org.mulesoft.lsp.configuration

import scala.scalajs.js
import scala.scalajs.js.JSConverters.JSRichOption
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientFileOperationPatternOptions extends js.Object {
  val ignoreCase: js.UndefOr[Boolean]
}
object ClientFileOperationPatternOptions {
  def apply(internal: FileOperationPatternOptions): ClientFileOperationPatternOptions =
    js.Dynamic
      .literal(
        ignoreCase = internal.ignoreCase.orUndefined
      )
      .asInstanceOf[ClientFileOperationPatternOptions]
}

// $COVERAGE-ON$
