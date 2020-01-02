package org.mulesoft.als.client.lsp.feature.telemetry

import org.mulesoft.lsp.feature.telemetry.TelemetryClientCapabilities

import scala.scalajs.js
import scala.scalajs.js.UndefOr
import js.JSConverters._

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
