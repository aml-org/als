package org.mulesoft.als.server.modules.diagnostic

import amf.core.client.common.validation.SeverityLevels
import org.mulesoft.lsp.feature.diagnostic.DiagnosticSeverity

object ValidationSeverity extends Enumeration {
  type ValidationSeverity = Value

  val Error: Value       = Value(1, "Error")
  val Warning: Value     = Value(2, "Warning")
  val Information: Value = Value(3, "Information")
  val Hint: Value        = Value(4, "Hint")

  def apply(level: String): ValidationSeverity = level match {
    case SeverityLevels.VIOLATION => Error
    case SeverityLevels.WARNING   => Warning
    case SeverityLevels.INFO      => Information
    case _                        => Hint
  }

  def toDiagnosticSeverity(severity: ValidationSeverity): DiagnosticSeverity.Value = severity match {
    case Error       => DiagnosticSeverity.Error
    case Warning     => DiagnosticSeverity.Warning
    case Information => DiagnosticSeverity.Information
    case Hint        => DiagnosticSeverity.Hint
  }
}
