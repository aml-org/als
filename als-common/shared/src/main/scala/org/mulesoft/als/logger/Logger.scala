package org.mulesoft.als.logger

import org.mulesoft.als.logger.MessageSeverity.MessageSeverity

import scala.scalajs.js.annotation.JSExportAll

@JSExportAll
trait Logger {

  /**
    * Logs a message
    *
    * @param message      - message text
    * @param severity     - message severity
    * @param component    - component name
    * @param subComponent - sub-component name
    */
  def log(message: String, severity: MessageSeverity, component: String, subComponent: String): Unit

  /**
    * Logs a DEBUG severity message.
    *
    * @param message      - message text
    * @param component    - component name
    * @param subComponent - sub-component name
    */
  def debug(message: String, component: String, subComponent: String): Unit

  /**
    * Logs a WARNING severity message.
    *
    * @param message      - message text
    * @param component    - component name
    * @param subComponent - sub-component name
    */
  def warning(message: String, component: String, subComponent: String): Unit

  /**
    * Logs an ERROR severity message.
    *
    * @param message      - message text
    * @param component    - component name
    * @param subComponent - sub-component name
    */
  def error(message: String, component: String, subComponent: String): Unit
}
