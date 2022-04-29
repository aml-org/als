package org.mulesoft.lsp.feature.diagnostic

case object DiagnosticSeverity extends Enumeration {
  type DiagnosticSeverity = Value

  /** Reports an error.
    */
  val Error: Value = Value(1)

  /** Reports a warning.
    */
  val Warning: Value = Value(2)

  /** Reports an information.
    */
  val Information: Value = Value(3)

  /** Reports a hint.
    */
  val Hint: Value = Value(4)
}
