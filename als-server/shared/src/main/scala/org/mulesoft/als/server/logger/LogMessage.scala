package org.mulesoft.als.server.logger

import org.mulesoft.als.server.logger.MessageSeverity.MessageSeverity

case class LogMessage(content: String, severity: MessageSeverity, component: String, subComponent: String)
