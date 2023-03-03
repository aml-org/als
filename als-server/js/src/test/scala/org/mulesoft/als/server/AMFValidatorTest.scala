package org.mulesoft.als.server

import amf.core.client.common.validation.ValidationMode
import amf.core.client.platform.model.domain.Shape
import amf.core.client.platform.validation.payload.{
  JsAMFPayloadValidationPlugin,
  JsPayloadValidator,
  ShapeValidationConfiguration,
  ValidatePayloadRequest
}
import amf.core.internal.unsafe.PlatformSecrets

import scala.scalajs.js

trait AMFValidatorTest extends LanguageServerBaseTest with PlatformSecrets {
  def testValidator(fn: () => Unit): JsAMFPayloadValidationPlugin =
    js.Dynamic
      .literal(
        id = "test-plugin",
        applies = new AppliesFunction {
          override def applies(element: ValidatePayloadRequest): Boolean = {
            def apply(element: ValidatePayloadRequest): Boolean = {
              fn()
              logger.debug("Validator called", "AMFValidatorTest", "testValidator:applies")
              false
            }
          }.asInstanceOf[Boolean]
        },
        validator = new ValidatorFunction {
          override def validator(): JsPayloadValidator = {
            def apply(
                shape: Shape,
                mediaType: String,
                config: ShapeValidationConfiguration,
                validationMode: ValidationMode
            ): JsPayloadValidator = ???
          }.asInstanceOf[JsPayloadValidator]
        }
      )
      .asInstanceOf[JsAMFPayloadValidationPlugin]
}

trait AppliesFunction extends js.Object {
  def applies(element: ValidatePayloadRequest): Boolean
}

trait ValidatorFunction extends js.Object {
  def validator(): JsPayloadValidator
}
