package org.mulesoft.lsp.feature.telemetry

case class TelemetryMessage(event: String, messageType: String, message: String, uri: String, time: Long, uuid: String)
