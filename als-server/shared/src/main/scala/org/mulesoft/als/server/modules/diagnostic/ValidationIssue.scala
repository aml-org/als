package org.mulesoft.als.server.modules.diagnostic

import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.als.convert.LspRangeConverter.toLspRange
import org.mulesoft.als.server.modules.diagnostic.ValidationSeverity.ValidationSeverity
import org.mulesoft.lsp.feature.diagnostic.{Diagnostic, DiagnosticRelatedInformation}

/** Validation issue: error or warning
  *
  * @param code
  *   Error code.
  * @param `type`
  *   Error type.
  * @param filePath
  *   Document uri. Legacy: to be renamed to uri.
  * @param text
  *   Issue human-readable text.
  * @param range
  *   Range producing the issue.
  * @param trace
  *   Stacktrace
  */
case class ValidationIssue(
    code: String,
    `type`: ValidationSeverity,
    filePath: String,
    text: String,
    range: PositionRange,
    trace: Seq[DiagnosticRelatedInformation]
) {
  lazy val diagnostic: Diagnostic = Diagnostic(
    toLspRange(range),
    text,
    Some(ValidationSeverity.toDiagnosticSeverity(`type`)),
    if (code.isEmpty) None else Some(code),
    None,
    None,
    Some(trace)
  )

  override def equals(obj: Any): Boolean = {
    obj match {
      case vi: ValidationIssue =>
        vi.`type` == this.`type` && vi.range == this.range && vi.text == this.text && vi.code == this.code
      case _ => false
    }
  }

  override def hashCode(): Int = super.hashCode()
}
