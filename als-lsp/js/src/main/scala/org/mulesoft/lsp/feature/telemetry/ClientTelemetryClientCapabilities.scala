package org.mulesoft.lsp.feature.telemetry

import scala.scalajs.js
import scala.scalajs.js.UndefOr
import js.JSConverters._
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientTelemetryClientCapabilities extends js.Object {
  def relatedInformation: UndefOr[Boolean] = js.native
}

object ClientTelemetryClientCapabilities {
  def apply(internal: TelemetryClientCapabilities): ClientTelemetryClientCapabilities =
    js.Dynamic
      .literal(relatedInformation = internal.relatedInformation.orUndefined)
      .asInstanceOf[ClientTelemetryClientCapabilities]
}

// $COVERAGE-ON$
