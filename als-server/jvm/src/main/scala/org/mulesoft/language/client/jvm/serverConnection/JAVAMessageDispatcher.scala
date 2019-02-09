// $COVERAGE-OFF$
package org.mulesoft.language.client.jvm.serverConnection

import org.mulesoft.language.client.jvm.dtoTypes.ProtocolMessagePayload
import org.mulesoft.language.common.logger.Logger
import org.mulesoft.language.entryPoints.common.{MessageDispatcher, ProtocolMessage, ProtocolSeqMessage}

trait JAVAMessageDispatcher extends MessageDispatcher[ProtocolMessagePayload, JAVAMessageType] with Logger {
  def internalSendJSONMessage(message: Any) {

  }

  def handleJSONMessageReceived(message: Any) {

  }

  def internalSendMessage(message: ProtocolMessage[ProtocolMessagePayload]) {

  }

  def internalSendSeqMessage(message: ProtocolSeqMessage[ProtocolMessagePayload]) {

  }
}

// $COVERAGE-ON$