package org.mulesoft.als.server.protocol.diagnostic

import org.mulesoft.als.server.feature.diagnostic.CustomValidationClientCapabilities

import scala.scalajs.js
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientCustomValidationClientCapabilities extends js.Object {
  def enabled: Boolean = js.native
}

object ClientCustomValidationClientCapabilities {
  def apply(internal: CustomValidationClientCapabilities): ClientCustomValidationClientCapabilities =
    js.Dynamic
      .literal(
        enabled = internal.enabled,
      )
      .asInstanceOf[ClientCustomValidationClientCapabilities]
}

// $COVERAGE-ON$