package org.mulesoft.language.common.dtoTypes

import amf.core.validation.SeverityLevels

object ValidationSeverity extends Enumeration {
  type ValidationSeverity = Value

  val Error: Value = Value(1, "Error")
  val Warning: Value = Value(2, "Warning")
  val Information: Value = Value(3, "Information")
  val Hint: Value = Value(4, "Hint")

  def apply(level: String): ValidationSeverity = level match {
    case SeverityLevels.VIOLATION => Error
    case SeverityLevels.WARNING => Warning
    case SeverityLevels.INFO => Information
    case _ => Hint
  }
}
