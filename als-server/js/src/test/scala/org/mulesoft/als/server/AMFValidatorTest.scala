package org.mulesoft.als.server

import amf.core.client.common.validation.ValidationMode
import amf.core.client.platform.model.domain
import amf.core.client.platform.validation.payload
import amf.core.client.platform.validation.payload.{
  JsAMFPayloadValidationPlugin,
  JsPayloadValidator,
  ValidatePayloadRequest
}
import amf.core.internal.unsafe.PlatformSecrets

trait AMFValidatorTest extends LanguageServerBaseTest with PlatformSecrets {
//  def testValidator(fn: () => Unit): JsAMFPayloadValidationPlugin = new JsAMFPayloadValidationPlugin {
//    override val id: String = "test-plugin"
//
//    override def applies(element: ValidatePayloadRequest): Boolean = {
//      fn()
//      logger.debug("Validator called", "AMFValidatorTest", "testValidator:applies")
//      false
//    }
//
//    override def validator(shape: domain.Shape, mediaType: String, config: payload.ShapeValidationConfiguration, validationMode: ValidationMode): JsPayloadValidator = ???
//  }
}
