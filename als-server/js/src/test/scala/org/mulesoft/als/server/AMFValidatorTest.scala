package org.mulesoft.als.server

import amf.core.client.common.validation.ValidationMode
import amf.core.client.platform.model.domain.Shape
import amf.core.client.platform.validation.payload.{
  JsAMFPayloadValidationPlugin,
  JsPayloadValidator,
  ShapeValidationConfiguration
}
import org.mulesoft.amfintegration.platform.AlsPlatformSecrets

import scala.scalajs.js

trait AMFValidatorTest extends LanguageServerBaseTest with AlsPlatformSecrets {

  def getAppliesFunction(fn: () => Unit): Boolean = {
    fn()
    logger.debug("Validator called", "AMFValidatorTest", "testValidator:applies")
    false
  }

  def getValidatorFunction(): JsPayloadValidator = {
    def apply(
        shape: Shape,
        mediaType: String,
        config: ShapeValidationConfiguration,
        validationMode: ValidationMode
    ): JsPayloadValidator = ???
  }.asInstanceOf[JsPayloadValidator]

  def testValidator(fn: () => Unit): JsAMFPayloadValidationPlugin =
    js.Dynamic
      .literal(
        id = "test-plugin",
        applies = getAppliesFunction(fn),
        validator = getValidatorFunction()
      )
      .asInstanceOf[JsAMFPayloadValidationPlugin]
}
