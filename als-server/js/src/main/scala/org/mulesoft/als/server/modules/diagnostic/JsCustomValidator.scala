package org.mulesoft.als.server.modules.diagnostic

import org.mulesoft.als.logger.Logger
import org.mulesoft.als.server.modules.diagnostic.custom.AMFOpaValidator
import org.mulesoft.als.server.wasm.{AmfWasmOpaValidator, Callback}

import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class JsCustomValidator(override val logger: Logger, val validator: AmfWasmOpaValidator) extends AMFOpaValidator {
  override def validateWithProfile(profile: String, data: String): Future[ValidationResult] = {
    val uuid = UUID.randomUUID().toString
    logger.debug(s"Starting custom validation $uuid", "JsCustomValidator", "validateWithProfile")
    val cb = new Callback[ValidationResult]
    validator.validate(profile, data, debug = false, cb.callback)
    cb.future.map(
      r => {
        logger.debug(s"Finished custom validation $uuid", "JsCustomValidator", "validateWithProfile")
        r
      }
    )
  }
}
