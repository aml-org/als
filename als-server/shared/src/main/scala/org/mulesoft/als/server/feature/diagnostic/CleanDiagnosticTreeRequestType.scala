package org.mulesoft.als.server.feature.diagnostic

import org.mulesoft.als.server.modules.diagnostic.AlsPublishDiagnosticsParams
import org.mulesoft.lsp.feature.RequestType

case object CleanDiagnosticTreeRequestType
    extends RequestType[CleanDiagnosticTreeParams, Seq[AlsPublishDiagnosticsParams]]
