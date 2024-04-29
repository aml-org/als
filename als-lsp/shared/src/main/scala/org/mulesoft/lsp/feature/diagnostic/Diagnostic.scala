package org.mulesoft.lsp.feature.diagnostic

import org.mulesoft.lsp.feature.common.Range
import org.mulesoft.lsp.feature.diagnostic.DiagnosticSeverity.DiagnosticSeverity

/** Represents a diagnostic, such as a compiler error or warning. Diagnostic objects are only valid in the scope of a
  * resource.
  *
  * @param range
  *   The range at which the message applies.
  * @param message
  *   The diagnostic's severity. Can be omitted. If omitted it is up to the client to interpret diagnostics as error,
  *   warning, info or hint.
  * @param severity
  *   The diagnostic's code, which might appear in the user interface.
  * @param code
  *   A human-readable string describing the source of this diagnostic, e.g. 'typescript' or 'super lint'.
  * @param source
  *   The diagnostic's message.
  * @param relatedInformation
  *   An array of related diagnostic information, e.g. when symbol-names within a scope collide all definitions can be
  *   marked via this property.
  */
case class Diagnostic(
    range: Range,
    message: String,
    severity: Option[DiagnosticSeverity] = None,
    code: Option[String] = None,
    codeDescription: Option[String] = None,
    source: Option[String] = None,
    relatedInformation: Option[Seq[DiagnosticRelatedInformation]] = None
)
