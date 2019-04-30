package org.mulesoft.als.server.logger

import org.mulesoft.als.server.logger.MessageSeverity.MessageSeverity

/**
  * Abstract implementation of logger that only needs internalLog method to be implemented.
  */
trait AbstractLogger extends Logger {
  private val MaxLength = 400

  protected val settings: Option[LoggerSettings]

  protected def executeLogging(message: String, severity: MessageSeverity): Unit

  /**
    * Logs a message
    *
    * @param message      - message text
    * @param severity     - message severity
    * @param component    - component name
    * @param subComponent - sub-component name
    */
  override def log(message: String, severity: MessageSeverity, component: String, subComponent: String): Unit = {
    val filtered = this.filterLogMessage(LogMessage(message, severity, component, subComponent))

    filtered.foreach(logMessage => {
      val toLog = f" ${logMessage.severity} ${logMessage.component}:${logMessage.subComponent}    ${logMessage.content}"
      this.executeLogging(toLog, logMessage.severity)
    })
  }

  /**
    * Logs a DEBUG severity message.
    *
    * @param message      - message text
    * @param component    - component name
    * @param subComponent - sub-component name
    */
  override def debug(message: String, component: String, subComponent: String): Unit = {
    log(message, MessageSeverity.DEBUG, component, subComponent)
  }

  /**
    * Logs a DEBUG_DETAIL severity message.
    *
    * @param message      - message text
    * @param component    - component name
    * @param subComponent - sub-component name
    */
  override def debugDetail(message: String, component: String, subComponent: String): Unit = {
    log(message, MessageSeverity.DEBUG_DETAIL, component, subComponent)
  }

  // $COVERAGE-OFF$
  /**
    * Logs a DEBUG_OVERVIEW severity message.
    *
    * @param message      - message text
    * @param component    - component name
    * @param subComponent - sub-component name
    */
  override def debugOverview(message: String, component: String, subComponent: String): Unit = {
    log(message, MessageSeverity.DEBUG_OVERVIEW, component, subComponent)
  }

  /**
    * Logs a WARNING severity message.
    *
    * @param message      - message text
    * @param component    - component name
    * @param subComponent - sub-component name
    */
  override def warning(message: String, component: String, subComponent: String): Unit = {
    log(message, MessageSeverity.WARNING, component, subComponent)
  }

  /**
    * Logs an ERROR severity message.
    *
    * @param message      - message text
    * @param component    - component name
    * @param subComponent - sub-component name
    */
  override def error(message: String, component: String, subComponent: String): Unit = {
    log(message, MessageSeverity.ERROR, component, subComponent)
  }

  /**
    * Sets logger configuration, both for the server and for the client.
    *
    * @param settings - logger settings object
    */
  def withSettings(settings: LoggerSettings): this.type

  private def filterLogMessage(message: LogMessage): Option[LogMessage] = {
    settings match {
      case Some(settingsValue) =>
        settingsValue.disabled match {
          case Some(true) => None
          case _ =>
            settingsValue.allowedComponents match {
              case Some(allowedComponents) if !allowedComponents.contains(message.component) => None
              case _ =>
                settingsValue.deniedComponents match {
                  case Some(deniedComponents) if deniedComponents.contains(message.component) => None
                  case _ =>
                    settingsValue.maxSeverity match {
                      case Some(maxSeverity) if message.severity.id < maxSeverity.id => None
                      case _ =>
                        val resultMessage = settingsValue.maxMessageLength match {
                          case Some(maxLength) if message.content.length > maxLength => message.content.substring(0, maxLength)
                          case _ => message.content
                        }

                        Some(LogMessage(resultMessage, message.severity, message.component, message.subComponent))
                    }
                }
            }
        }
      case _ =>
        val croppedContent = if (message.content.length > MaxLength)
            message.content.substring(0, MaxLength)
          else
            message.content

        Some(message.copy(content = croppedContent))
    }
  }
}
