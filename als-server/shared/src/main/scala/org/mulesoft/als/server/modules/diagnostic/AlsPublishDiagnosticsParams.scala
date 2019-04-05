package org.mulesoft.als.server.modules.diagnostic

import org.mulesoft.lsp.feature.diagnostic.{Diagnostic, PublishDiagnosticsParams}

case class AlsPublishDiagnosticsParams(uri: String, diagnostics: Seq[Diagnostic]) extends PublishDiagnosticsParams
