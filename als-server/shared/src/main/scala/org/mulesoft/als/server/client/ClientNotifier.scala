package org.mulesoft.als.server.client

import org.mulesoft.lsp.feature.diagnostic.PublishDiagnosticsParams

trait ClientNotifier {

  def notifyDiagnostic(params: PublishDiagnosticsParams): Unit

}
