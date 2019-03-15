package org.mulesoft.language.common.logger

import org.mulesoft.language.common.logger.MessageSeverity.MessageSeverity

case class LogMessage(content: String, severity: MessageSeverity, component: String, subComponent: String)
