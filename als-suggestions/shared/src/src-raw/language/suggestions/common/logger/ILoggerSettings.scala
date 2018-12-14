package org.mulesoft.als.suggestions.common.logger

import org.mulesoft.als.suggestions.common.logger.ILoggerSettings;
import org.mulesoft.als.suggestions.common.logger.ILogger;
import org.mulesoft.als.suggestions.common.logger.EmptyLogger;

trait ILoggerSettings {
  var disabled: Boolean
  var allowedComponents: Array[String]
  var deniedComponents: Array[String]
  var maxSeverity: MessageSeverity
  var maxMessageLength: Int
}
