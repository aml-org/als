package org.mulesoft.language.server.js


import org.mulesoft.language.entryPoints.common.{ProtocolMessage => SharedProtocolMessage}

case class ProtocolMessage[PayloadType](
  `type`: String,
  payload: Option[PayloadType],
  id: Option[String] = None,
  errorMessage: Option[String] = None
) {
}

object ProtocolMessage {

  implicit def message2SharedMessage[PayloadType](
    message: ProtocolMessage[PayloadType]): SharedProtocolMessage[PayloadType] = {

    SharedProtocolMessage(
      message.`type`,
      message.payload,
      message.id,
      message.errorMessage
    )
  }

  implicit def sharedMessage2Message[PayloadType](
    message: SharedProtocolMessage[PayloadType]): ProtocolMessage[PayloadType] = {

    ProtocolMessage(
      message.`type`,
      message.payload,
      message.id,
      message.errorMessage
    )
  }
}