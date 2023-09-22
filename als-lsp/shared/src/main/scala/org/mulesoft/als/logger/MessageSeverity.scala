package org.mulesoft.als.logger

/** Message severity.
  */
object MessageSeverity extends Enumeration {
  type MessageSeverity = Value
  val DEBUG, WARNING, ERROR = Value
}
