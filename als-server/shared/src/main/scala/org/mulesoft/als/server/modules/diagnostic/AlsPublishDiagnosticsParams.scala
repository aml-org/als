package org.mulesoft.als.server.modules.diagnostic

import amf.core.client.common.validation.ProfileName
import org.mulesoft.lsp.feature.diagnostic.{Diagnostic, PublishDiagnosticsParams}

case class AlsPublishDiagnosticsParams(uri: String, diagnostics: Seq[Diagnostic], profile: ProfileName)
    extends PublishDiagnosticsParams
