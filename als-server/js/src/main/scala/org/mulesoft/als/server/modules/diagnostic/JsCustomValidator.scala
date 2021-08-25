package org.mulesoft.als.server.modules.diagnostic

import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.modules.diagnostic.custom.AMFOpaValidator
import org.mulesoft.als.server.wasm.{AmfWasmOpaValidator, Callback}

import scala.concurrent.Future

class JsCustomValidator(override val logger: Logger) extends AMFOpaValidator {

  override def validateWithProfile(profile: String, data: String): Future[ValidationResult] = {
    logger.debug("Starting custom validation", "JsCustomValidator", "validateWithProfile")
    val cb = new Callback[ValidationResult]
    AmfWasmOpaValidator.validate(profile, data, debug = false, cb.callback)
    cb.future
  }

}
