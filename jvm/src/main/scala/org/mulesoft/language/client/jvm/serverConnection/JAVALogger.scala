package org.mulesoft.language.client.jvm.serverConnection

import org.mulesoft.language.common.logger.MessageSeverity

trait JAVALogger {
	def log(message: String, severity: MessageSeverity.Value, component: String, subcomponent: String);
}
