package org.mulesoft.lsp.feature.diagnostic

/** Diagnostics notification are sent from the server to the client to signal results of validation runs.
  */

trait PublishDiagnosticsParams {

  /** The URI for which diagnostic information is reported.
    */
  val uri: String

  /** An array of diagnostic information items.
    */
  val diagnostics: Seq[Diagnostic]
}
