package org.mulesoft.language.common.logger


/**
  * Abstract implementation of logger that only needs internalLog method to be implemented.
  */
trait AbstractLogger extends ILogger {

  private var loggerSettings: Option[ILoggerSettings] = None;

  protected def internalLog(msg: String, severity: MessageSeverity.Value): Unit;

  /**
    * Logs a message
    * @param msg - message text
    * @param svrty - message severity
    * @param cmp - component name
    * @param subCmp - sub-component name
    */
  def log(msg: String, svrty: MessageSeverity.Value,
          cmp: String, subCmp: String): Unit = {

    val filtered = this.filterLogMessage( new LogMessage {
      var message = msg
      var severity = svrty
      var component = cmp
      var subcomponent = subCmp
    }, this.loggerSettings)

    filtered.foreach(logMessage => {

//      val now = Calendar.getInstance().getTime()
//      val hourFormat = new SimpleDateFormat("hh:mm:ss:SSS")
//      ${hourFormat.format(now)}
      val toLog = f" ${logMessage.severity} ${logMessage.component}:${logMessage.subcomponent}    ${logMessage.message}";
      this.internalLog(toLog, logMessage.severity)

    })
  }

  /**
    * Logs a DEBUG severity message.
    * @param message - message text
    * @param component - component name
    * @param subcomponent - sub-component name
    */
  def debug(message: String, component: String, subcomponent: String): Unit = {
    this.log(message, MessageSeverity.DEBUG, component, subcomponent);
  }

  /**
    * Logs a DEBUG_DETAIL severity message.
    * @param message - message text
    * @param component - component name
    * @param subcomponent - sub-component name
    */
  def debugDetail(message: String, component: String, subcomponent: String): Unit = {
    this.log(message, MessageSeverity.DEBUG_DETAIL, component, subcomponent);
  }

  /**
    * Logs a DEBUG_OVERVIEW severity message.
    * @param message - message text
    * @param component - component name
    * @param subcomponent - sub-component name
    */
  def debugOverview(message: String, component: String, subcomponent: String): Unit = {
    this.log(message, MessageSeverity.DEBUG_OVERVIEW, component, subcomponent);
  }

  /**
    * Logs a WARNING severity message.
    * @param message - message text
    * @param component - component name
    * @param subcomponent - sub-component name
    */
  def warning(message: String, component: String, subcomponent: String): Unit = {
    this.log(message, MessageSeverity.WARNING, component, subcomponent);
  }

  /**
    * Logs an ERROR severity message.
    * @param message - message text
    * @param component - component name
    * @param subcomponent - sub-component name
    */
  def error(message: String, component: String, subcomponent: String): Unit = {
    this.log(message, MessageSeverity.ERROR, component, subcomponent);
  }

  /**
    * Sets logger configuration, both for the server and for the client.
    * @param loggerSettings
    */
  def setLoggerConfiguration(loggerSettings: ILoggerSettings): Unit = {
    //TODO restore setting
    //this.loggerSettings = Option(loggerSettings);
  }

  private def filterLogMessage(msg: LogMessage, settings: Option[ILoggerSettings]): Option[LogMessage] = {

    settings match {
      case Some(settings) => {
        settings.disabled match {
          case Some(true) => None
          case _ => {

            settings.allowedComponents match {
              case Some(allowedComponents) if !allowedComponents.contains(msg.component) => None
              case _ => {

                settings.deniedComponents match {
                  case Some(deniedComponents) if deniedComponents.contains(msg.component) => None
                  case _ => {

                    settings.maxSeverity match {
                      case Some(maxSeverity) if msg.severity.id < maxSeverity.id => None
                      case _ => {

                        val resultMessage = settings.maxMessageLength match {
                          case Some(maxLength) if msg.message.length > maxLength => msg.message.substring(0, maxLength)
                          case _ => msg.message
                        }

                        Option(new LogMessage {
                          var message = resultMessage
                          var severity = msg.severity
                          var component = msg.component
                          var subcomponent = msg.subcomponent
                        })

                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
      case _ => {

        val maxLength = 400
        val resultMessage = if (msg.message.length > maxLength)
          msg.message.substring(0, maxLength)
        else
          msg.message

        Option(new LogMessage {
          var message = resultMessage
          var severity = msg.severity
          var component = msg.component
          var subcomponent = msg.subcomponent
        })
      }
    }

//    if (!settings.isDefined) {
//
//      None
//    } else if (settings.get.disabled.isDefined && settings.get.disabled.get) {
//
//      None
//    } else if (settings.get.allowedComponents.isDefined &&
//                !settings.get.allowedComponents.contains(msg.component)) {
//
//      None
//    } else if (settings.get.deniedComponents.isDefined &&
//                settings.get.deniedComponents.contains(msg.component)) {
//
//      None
//    } else if (settings.get.maxSeverity.isDefined &&
//                msg.severity.id < settings.get.maxSeverity.get.id) {
//
//      None
//    }
//
//    val resultMessage =
//      if (settings.get.maxMessageLength.isDefined &&
//                msg.message.length > settings.get.maxMessageLength.get) {
//        msg.message.substring(0, settings.get.maxMessageLength.get)
//      }
//      else {
//        msg.message
//      }
//
//    Option(new LogMessage {
//      var message = resultMessage
//      var severity = msg.severity
//      var component = msg.component
//      var subcomponent = msg.subcomponent
//    })

  }
}
