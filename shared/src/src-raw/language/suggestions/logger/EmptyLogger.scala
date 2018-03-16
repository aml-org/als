package org.mulesoft.als.suggestions.logger

import org.mulesoft.als.suggestions.logger.ILoggerSettings;
import org.mulesoft.als.suggestions.logger.ILogger;
import org.mulesoft.als.suggestions.logger.EmptyLogger;

class EmptyLogger extends ILogger {
  def log(message: String, severity: MessageSeverity, component: String, subcomponent: String): Unit = {
}
  def debug(message: String, component: String, subcomponent: String): Unit = {
}
  def debugDetail(message: String, component: String, subcomponent: String): Unit = {
}
  def debugOverview(message: String, component: String, subcomponent: String): Unit = {
}
  def warning(message: String, component: String, subcomponent: String): Unit = {
}
  def error(message: String, component: String, subcomponent: String): Unit = {
}
  def setLoggerConfiguration(loggerSettings: ILoggerSettings) = {
}
}
