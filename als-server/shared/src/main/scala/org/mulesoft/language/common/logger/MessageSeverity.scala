package org.mulesoft.language.common.logger

/**
  * Message severity.
  */
object MessageSeverity extends Enumeration {
  type MessageSeverity = Value
  val DEBUG_DETAIL, DEBUG, DEBUG_OVERVIEW, WARNING, ERROR = Value
}
