package org.mulesoft.lsp.feature.telemetry

case class TelemetryMessage(event: String, messageType: Int, message: String, time: Long)
