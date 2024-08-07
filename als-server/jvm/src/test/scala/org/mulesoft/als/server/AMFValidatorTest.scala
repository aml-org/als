package org.mulesoft.als.server

import amf.core.client.common.validation.ValidationMode
import amf.core.client.scala.model.domain.Shape
import amf.core.client.scala.validation.payload.{
  AMFShapePayloadValidationPlugin,
  AMFShapePayloadValidator,
  ShapeValidationConfiguration,
  ValidatePayloadRequest
}
import org.mulesoft.amfintegration.platform.AlsPlatformSecrets

trait AMFValidatorTest extends LanguageServerBaseTest with AlsPlatformSecrets {
  case class TestValidator(fn: () => Unit) extends AMFShapePayloadValidationPlugin {
    override def applies(element: ValidatePayloadRequest): Boolean = {
      fn()
      logger.debug("Test validator called", "TestValidator", "applies")
      false
    }

    override def validator(
        shape: Shape,
        mediaType: String,
        config: ShapeValidationConfiguration,
        validationMode: ValidationMode
    ): AMFShapePayloadValidator = ???

    override val id: String = "test-plugin"
  }
}
