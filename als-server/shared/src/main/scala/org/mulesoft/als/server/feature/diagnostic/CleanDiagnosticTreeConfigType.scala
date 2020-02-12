package org.mulesoft.als.server.feature.diagnostic

import org.mulesoft.lsp.ConfigType

case object CleanDiagnosticTreeConfigType
    extends ConfigType[CleanDiagnosticTreeClientCapabilities, CleanDiagnosticTreeOptions]
