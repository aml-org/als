// $COVERAGE-OFF$
package org.mulesoft.language.common.logger

/**
  * Logger that logs nothing
  */
class EmptyLogger extends AbstractLogger {

  protected def internalLog(msg: String, severity: MessageSeverity.Value): Unit = {}
}
// $COVERAGE-ON$
