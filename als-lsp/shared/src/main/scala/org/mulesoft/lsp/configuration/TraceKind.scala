package org.mulesoft.lsp.configuration

case object TraceKind extends Enumeration {
  type TraceKind = Value

  val Off: Value      = Value("off")
  val Messages: Value = Value("messages")
  val Verbose: Value  = Value("verbose")
}
