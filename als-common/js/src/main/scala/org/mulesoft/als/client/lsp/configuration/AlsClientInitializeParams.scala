package org.mulesoft.als.client.lsp.configuration

import org.mulesoft.lsp.configuration.{AlsClientCapabilities => InternalClientCapabilities}
import org.mulesoft.lsp.feature.serialization.SerializationClientCapabilities

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportTopLevel

// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS
/**
  * Client Capabilities to inform als
  * */
@JSExportTopLevel("ClientAlsClientCapabilities")
class ClientAlsClientCapabilities(val serialization: js.UndefOr[ClientSerializationClientCapabilities],
                                  val cleanDiagnosticTree: js.UndefOr[ClientCleanDiagnosticTreeClientCapabilities])

@JSExportTopLevel("ClientCleanDiagnosticTreeClientCapabilities")
class ClientCleanDiagnosticTreeClientCapabilities(val enableCleanDiagnostic: Boolean)

@JSExportTopLevel("ClientSerializationClientCapabilities")
class ClientSerializationClientCapabilities(val acceptsNotification: Boolean)

/**
  * Server capabilities to inform the client after receive client capabilities.
  */
@JSExportTopLevel("ClientAlsServerCapabilities")
class ClientAlsServerCapabilities(val serialization: js.UndefOr[ClientSerializationServerOptions],
                                  val cleanDiagnostics: js.UndefOr[ClientCleanDiagnosticTreeServerOptions])

@JSExportTopLevel("ClientCleanDiagnosticTreeServerOptions")
class ClientCleanDiagnosticTreeServerOptions(val supported: Boolean)

@JSExportTopLevel("ClientSerializationServerOptions")
class ClientSerializationServerOptions(val supportsSerialization: Boolean)
// $COVERAGE-ON$
