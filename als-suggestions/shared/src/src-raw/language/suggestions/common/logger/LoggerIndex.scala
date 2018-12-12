package org.mulesoft.als.suggestions.common.logger

import org.mulesoft.als.suggestions.common.logger.ILoggerSettings;
import org.mulesoft.als.suggestions.common.logger.ILogger;
import org.mulesoft.als.suggestions.common.logger.EmptyLogger;

class LoggerIndex{

sealed abstract class MessageSeverity
object MessageSeverity {
   case object DEBUG_DETAIL extends MessageSeverity
  case object DEBUG extends MessageSeverity
  case object DEBUG_OVERVIEW extends MessageSeverity
  case object WARNING extends MessageSeverity
  case object ERROR extends MessageSeverity 
}
var _logger: ILogger = null
def setLogger(logger: ILogger) = {
 (_logger=logger)
 
}
def getLogger(): ILogger = {
 if (_logger)
return _logger
return new EmptyLogger()
 
}


}
