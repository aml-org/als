package org.mulesoft.als.server.feature.diagnostic

import org.mulesoft.lsp.feature.RequestType
import org.mulesoft.lsp.feature.diagnostic.PublishDiagnosticsParams

case object CleanDiagnosticTreeRequestType
    extends RequestType[CleanDiagnosticTreeParams, Seq[PublishDiagnosticsParams]]
