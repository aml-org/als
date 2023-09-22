package org.mulesoft.als.logger

import org.mulesoft.als.logger.MessageSeverity.MessageSeverity

/** Abstract implementation of logger that only needs internalLog method to be implemented.
  */
trait AbstractLogger extends Logger {
  private val MaxLength = 400

  protected val settings: Option[LoggerSettings]

  protected def executeLogging(message: String, severity: MessageSeverity): Unit

  /** Logs a message
    *
    * @param message
    *   \- message text
    * @param severity
    *   \- message severity
    * @param component
    *   \- component name
    * @param subComponent
    *   \- sub-component name
    */
  override def log(message: String, severity: MessageSeverity, component: String, subComponent: String): Unit = {
    val filtered = this.filterLogMessage(LogMessage(Option(message).getOrElse(""), severity, component, subComponent))

    filtered.foreach(logMessage => {
      val toLog =
        f"[${logMessage.severity}] - ${logMessage.component}:${logMessage.subComponent} - ${logMessage.content}"
      this.executeLogging(toLog, logMessage.severity)
    })
  }

  /** Logs a DEBUG severity message.
    *
    * @param message
    *   \- message text
    * @param component
    *   \- component name
    * @param subComponent
    *   \- sub-component name
    */
  override def debug(message: String, component: String, subComponent: String): Unit = {
    log(message, MessageSeverity.DEBUG, component, subComponent)
  }

  /** Logs a WARNING severity message.
    *
    * @param message
    *   \- message text
    * @param component
    *   \- component name
    * @param subComponent
    *   \- sub-component name
    */
  override def warning(message: String, component: String, subComponent: String): Unit = {
    log(message, MessageSeverity.WARNING, component, subComponent)
  }

  /** Logs an ERROR severity message.
    *
    * @param message
    *   \- message text
    * @param component
    *   \- component name
    * @param subComponent
    *   \- sub-component name
    */
  override def error(message: String, component: String, subComponent: String): Unit = {
    log(message, MessageSeverity.ERROR, component, subComponent)
  }

  /** Sets logger configuration, both for the server and for the client.
    *
    * @param settings
    *   \- logger settings object
    */
  def withSettings(settings: LoggerSettings): this.type

  private def allowedComponent(settings: LoggerSettings, component: String): Boolean =
    allowList(settings, component) &&
      blockList(settings, component)

  private def blockList(settings: LoggerSettings, component: String): Boolean =
    settings.deniedComponents.forall(!_.contains(component))

  private def allowList(settings: LoggerSettings, component: String): Boolean =
    settings.allowedComponents.forall(_.contains(component))

  private def belowSeverity(settings: LoggerSettings, severity: MessageSeverity): Boolean =
    settings.maxSeverity.forall(severity.id < _.id)

  private def filterLogMessage(message: LogMessage): Option[LogMessage] = {
    val maxLength: Option[Int] = settings match {
      case Some(settingsValue) if passesLogFilter(message, settingsValue) =>
        settingsValue.maxMessageLength
      case None => Some(MaxLength) // default
      case _    => None            // there are settings, but the message is not compliant with the restrictions

    }
    maxLength.map { ml =>
      message.copy(content = cropMessage(message, ml))
    }

  }

  private def passesLogFilter(message: LogMessage, settingsValue: LoggerSettings) = {
    !settingsValue.disabled.contains(true) && allowedComponent(settingsValue, message.component) && !belowSeverity(
      settingsValue,
      message.severity
    )
  }

  private def cropMessage(message: LogMessage, ml: Int) =
    if (message.content.length > ml)
      message.content.substring(0, ml)
    else
      message.content
}
