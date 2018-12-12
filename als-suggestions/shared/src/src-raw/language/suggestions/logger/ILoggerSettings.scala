package org.mulesoft.als.suggestions.logger

import org.mulesoft.als.suggestions.logger.ILoggerSettings;
import org.mulesoft.als.suggestions.logger.ILogger;
import org.mulesoft.als.suggestions.logger.EmptyLogger;

trait ILoggerSettings {
  var disabled: Boolean
  var allowedComponents: Array[String]
  var deniedComponents: Array[String]
  var maxSeverity: MessageSeverity
  var maxMessageLength: Int
}
