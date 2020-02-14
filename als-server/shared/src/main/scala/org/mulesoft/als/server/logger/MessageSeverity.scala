package org.mulesoft.als.server.logger

/**
  * Message severity.
  */
object MessageSeverity extends Enumeration {
  type MessageSeverity = Value
  val DEBUG, WARNING, ERROR = Value
}