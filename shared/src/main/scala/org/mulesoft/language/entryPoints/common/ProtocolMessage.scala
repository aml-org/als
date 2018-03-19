package org.mulesoft.language.entryPoints.common

case class ProtocolMessage[PayloadType](
 `type`: String,
 payload: Option[PayloadType],
 id: Option[String] = None,
 errorMessage: Option[String] = None
)
{
}