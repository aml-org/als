package org.mulesoft.lsp.configuration

import org.mulesoft.lsp.feature.diagnostic.CleanDiagnosticTreeOptions
import org.mulesoft.lsp.feature.serialization.SerializationServerOptions

case class AlsServerCapabilities(serialization: Option[SerializationServerOptions],
                                 cleanDiagnostics: Option[CleanDiagnosticTreeOptions])
