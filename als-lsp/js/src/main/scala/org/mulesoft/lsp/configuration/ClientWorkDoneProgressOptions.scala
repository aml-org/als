package org.mulesoft.lsp.configuration

import scala.scalajs.js
import scala.scalajs.js.JSConverters.JSRichOption

// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientWorkDoneProgressOptions extends js.Object {
  def workDoneProgress: js.UndefOr[Boolean] = js.native
}

object ClientWorkDoneProgressOptions {
  def apply(internal: WorkDoneProgressOptions): ClientWorkDoneProgressOptions =
    js.Dynamic
      .literal(workDoneProgress = internal.workDoneProgress.orUndefined)
      .asInstanceOf[ClientWorkDoneProgressOptions]
}

// $COVERAGE-ON$
