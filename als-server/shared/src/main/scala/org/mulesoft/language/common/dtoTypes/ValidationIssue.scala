package org.mulesoft.language.common.dtoTypes

import common.dtoTypes.PositionRange
import org.mulesoft.language.common.dtoTypes.ValidationSeverity.ValidationSeverity

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
                           trace: Seq[ValidationIssue])
