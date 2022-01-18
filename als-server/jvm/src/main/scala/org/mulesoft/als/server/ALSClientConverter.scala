package org.mulesoft.als.server
import amf.core.client.scala.validation.payload.AMFShapePayloadValidationPlugin
import org.mulesoft.als.logger.Logger

trait ALSClientConverter extends ALSConverters with ClientLoggerConverter {
  override type ClientLogger    = Logger
  override type ClientAMFPlugin = AMFShapePayloadValidationPlugin

  override def asInternal(from: ClientLogger): Logger = from

  override def toInternal(from: ClientAMFPlugin): AMFShapePayloadValidationPlugin = from
}
