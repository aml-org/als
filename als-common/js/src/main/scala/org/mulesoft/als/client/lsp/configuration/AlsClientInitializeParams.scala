package org.mulesoft.als.client.lsp.configuration

import org.mulesoft.lsp.configuration.{AlsClientCapabilities => InternalClientCapabilities}
import org.mulesoft.lsp.feature.serialization.SerializationClientCapabilities

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportTopLevel

// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@JSExportTopLevel("ClientAlsClientCapabilities")
class ClientAlsClientCapabilities(val serialization: js.UndefOr[ClientSerializationClientCapabilities])

@JSExportTopLevel("ClientSerializationClientCapabilities")
class ClientSerializationClientCapabilities(val acceptsNotification: Boolean)

// $COVERAGE-ON$
