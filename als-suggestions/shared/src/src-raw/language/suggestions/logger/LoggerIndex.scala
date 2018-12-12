package org.mulesoft.als.suggestions.logger

import org.mulesoft.als.suggestions.logger.ILoggerSettings;
import org.mulesoft.als.suggestions.logger.ILogger;
import org.mulesoft.als.suggestions.logger.EmptyLogger;

class LoggerIndex{

sealed abstract class MessageSeverity
object MessageSeverity {
   case object DEBUG_DETAIL extends MessageSeverity
  case object DEBUG extends MessageSeverity
  case object DEBUG_OVERVIEW extends MessageSeverity
  case object WARNING extends MessageSeverity
  case object ERROR extends MessageSeverity 
}


}
