package org.mulesoft.als.server
import amf.core.client.platform.validation.payload.{AMFPayloadValidationPluginConverter, JsAMFPayloadValidationPlugin}
import amf.core.client.scala.validation.payload.AMFShapePayloadValidationPlugin
import amf.core.internal.convert.PayloadValidationPluginConverter.PayloadValidationPluginMatcher
import org.mulesoft.als.logger.Logger

import scala.concurrent.ExecutionContext.Implicits.global

trait ALSClientConverter extends ALSConverters with ClientLoggerConverter {
  override type ClientLogger    = JsClientLogger
  override type ClientAMFPlugin = JsAMFPayloadValidationPlugin

  override def asInternal(from: ClientLogger): Logger =
    ClientLoggerAdapter(from)

  override def toInternal(from: ClientAMFPlugin): AMFShapePayloadValidationPlugin =
    PayloadValidationPluginMatcher
      .asInternal(AMFPayloadValidationPluginConverter.toAMF(from))

}
