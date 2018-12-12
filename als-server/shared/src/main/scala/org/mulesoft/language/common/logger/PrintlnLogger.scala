package org.mulesoft.language.common.logger

/**
  * Logger that prints to console.
  */
class PrintlnLogger extends AbstractLogger {

  protected def internalLog(msg: String, severity: MessageSeverity.Value): Unit = {
    println(msg)
  }
}

/**
  * Logger that prints to console.
  */
trait IPrintlnLogger extends AbstractLogger {

  protected def internalLog(msg: String, severity: MessageSeverity.Value): Unit = {
    println(msg)
  }
}