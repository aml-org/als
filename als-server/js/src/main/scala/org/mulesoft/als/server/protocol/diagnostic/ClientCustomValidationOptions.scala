package org.mulesoft.als.server.protocol.diagnostic

import org.mulesoft.als.server.feature.diagnostic.CustomValidationOptions

import scala.scalajs.js
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientCustomValidationOptions extends js.Object {
  def enabled: Boolean = js.native
}

object ClientCustomValidationOptions {
  def apply(internal: CustomValidationOptions): ClientCustomValidationOptions =
    js.Dynamic
      .literal(
        enabled = internal.enabled
      )
      .asInstanceOf[ClientCustomValidationOptions]
}

// $COVERAGE-ON$
