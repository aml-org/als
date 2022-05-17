package org.mulesoft.lsp.feature.telemetry

/** Capabilities specific to `textDocument/publishDiagnostics`.
  *
  * @param relatedInformation
  *   Whether the clients accepts telemetry with related information.
  */
case class TelemetryClientCapabilities(relatedInformation: Option[Boolean])
