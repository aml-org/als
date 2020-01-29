package org.mulesoft.lsp.feature.diagnostic

import org.mulesoft.lsp.feature.RequestType

case object CleanDiagnosticTreeRequestType
    extends RequestType[CleanDiagnosticTreeParams, Seq[PublishDiagnosticsParams]]
