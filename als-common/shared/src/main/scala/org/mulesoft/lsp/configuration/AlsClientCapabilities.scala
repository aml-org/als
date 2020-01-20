package org.mulesoft.lsp.configuration

import org.mulesoft.lsp.feature.diagnostic.CleanDiagnosticTreeClientCapabilities
import org.mulesoft.lsp.feature.serialization.SerializationClientCapabilities

case class AlsClientCapabilities(serialization: Option[SerializationClientCapabilities],
                                 cleanDiagnosticTree: Option[CleanDiagnosticTreeClientCapabilities])
