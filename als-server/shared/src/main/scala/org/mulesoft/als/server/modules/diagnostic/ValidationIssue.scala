package org.mulesoft.als.server.modules.diagnostic

import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.als.server.modules.common.LspConverter.toLspRange
import org.mulesoft.als.server.modules.diagnostic.ValidationSeverity.ValidationSeverity
import org.mulesoft.lsp.common.Location
import org.mulesoft.lsp.feature.diagnostic.{Diagnostic, DiagnosticRelatedInformation}

/**
  * Validation issue: error or warning
  *
  * @param code          Error code.
  * @param `type`        Error type.
  * @param filePath      Document uri. Legacy: to be renamed to uri.
  * @param text          Issue human-readable text.
  * @param range         Range producing the issue.
  * @param trace         Subsequent validation issues
  *
  */
case class ValidationIssue(code: String,
                           `type`: ValidationSeverity,
                           filePath: String,
                           text: String,
                           range: PositionRange,
                           trace: Seq[ValidationIssue]) {
  lazy val diagnostic: Diagnostic = Diagnostic(
    toLspRange(range),
    text,
    Some(ValidationSeverity.toDiagnosticSeverity(`type`)),
    None,
    Some(filePath),
    trace.flatMap(_.trace).map(_.diagnosticRelatedInformation)
  )

  lazy val diagnosticRelatedInformation: DiagnosticRelatedInformation =
    DiagnosticRelatedInformation(Location(filePath, toLspRange(range)), text)

  override def equals(obj: Any): Boolean = {
    obj match {
      case vi: ValidationIssue =>
        vi.`type` == this.`type` && vi.range == this.range && vi.text == this.text
      case _ => false
    }
  }
}
