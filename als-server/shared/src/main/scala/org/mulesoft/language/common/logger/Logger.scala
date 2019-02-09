package org.mulesoft.language.common.logger

import org.mulesoft.language.common.logger.MessageSeverity.MessageSeverity

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
    * Logs a DEBUG_DETAIL severity message.
    *
    * @param message      - message text
    * @param component    - component name
    * @param subComponent - sub-component name
    */
  def debugDetail(message: String, component: String, subComponent: String): Unit

  /**
    * Logs a DEBUG_OVERVIEW severity message.
    *
    * @param message      - message text
    * @param component    - component name
    * @param subComponent - sub-component name
    */
  def debugOverview(message: String, component: String, subComponent: String): Unit

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

  /**
    * Sets logger configuration, both for the server and for the client.
    *
    * @param settings - logger settings object
    */
  def withSettings(settings: LoggerSettings): this.type
}
