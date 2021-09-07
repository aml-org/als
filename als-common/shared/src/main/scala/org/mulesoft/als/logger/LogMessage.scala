package org.mulesoft.als.logger

import org.mulesoft.als.logger.MessageSeverity.MessageSeverity

case class LogMessage(content: String, severity: MessageSeverity, component: String, subComponent: String)
