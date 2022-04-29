package org.mulesoft.lsp.feature.diagnostic

/** Capabilities specific to `textDocument/publishDiagnostics`.
  *
  * @param relatedInformation
  *   Whether the clients accepts diagnostics with related information.
  */

case class DiagnosticClientCapabilities(relatedInformation: Option[Boolean])
