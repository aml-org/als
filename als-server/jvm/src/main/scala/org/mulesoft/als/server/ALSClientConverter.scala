package org.mulesoft.als.server
import amf.core.client.scala.validation.payload.AMFShapePayloadValidationPlugin
import org.mulesoft.als.logger.Logger
import org.mulesoft.als.server.modules.diagnostic.custom.{AMFOpaValidator, AMFOpaValidatorBuilder}

import scala.concurrent.Future

trait ALSClientConverter extends ALSConverters with ClientLoggerConverter {
  override type ClientLogger       = Logger
  override type ClientAMFValidator = AMFOpaValidator
  override type ClientAMFPlugin    = AMFShapePayloadValidationPlugin

  override def asInternal(from: ClientLogger): Logger = from

  override def toInternal(from: ClientAMFValidator): AMFOpaValidatorBuilder =
    (log: Logger) =>
      new AMFOpaValidator {
        override val logger: Logger = log
        override def validateWithProfile(profile: String, data: String): Future[ValidationResult] =
          from.validateWithProfile(profile, data)
    }

  override def toInternal(from: ClientAMFPlugin): AMFShapePayloadValidationPlugin = from
}
