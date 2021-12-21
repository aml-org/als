package org.mulesoft.als.server
import amf.core.client.platform.validation.payload.{AMFPayloadValidationPluginConverter, JsAMFPayloadValidationPlugin}
import amf.core.client.scala.validation.payload.AMFShapePayloadValidationPlugin
import amf.core.internal.convert.PayloadValidationPluginConverter.PayloadValidationPluginMatcher
import org.mulesoft.als.logger.Logger
import org.mulesoft.als.server.modules.diagnostic.JsCustomValidator
import org.mulesoft.als.server.modules.diagnostic.custom.AMFOpaValidatorBuilder
import org.mulesoft.als.server.wasm.AmfWasmOpaValidator

import scala.concurrent.ExecutionContext.Implicits.global

trait ALSClientConverter extends ALSConverters with ClientLoggerConverter {
  override type ClientLogger       = JsClientLogger
  override type ClientAMFValidator = AmfWasmOpaValidator
  override type ClientAMFPlugin    = JsAMFPayloadValidationPlugin

  override def asInternal(from: ClientLogger): Logger =
    ClientLoggerAdapter(from)

  override def asInternal(from: ClientAMFValidator): AMFOpaValidatorBuilder =
    (logger: Logger) => JsCustomValidator(logger, from)

  override def asInternal(from: ClientAMFPlugin): AMFShapePayloadValidationPlugin =
    PayloadValidationPluginMatcher
      .asInternal(AMFPayloadValidationPluginConverter.toAMF(from))

}
