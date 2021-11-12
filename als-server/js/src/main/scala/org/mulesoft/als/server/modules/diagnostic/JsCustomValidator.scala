package org.mulesoft.als.server.modules.diagnostic

import org.mulesoft.als.logger.Logger
import org.mulesoft.als.server.modules.diagnostic.custom.AMFOpaValidator
import org.mulesoft.als.server.wasm.{AmfWasmOpaValidator, Callback1, Callback2}

import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

class JsCustomValidator private (override val logger: Logger, val validator: AmfWasmOpaValidator)
    extends AMFOpaValidator {

  val initCallback: Callback1 = new Callback1

  private def init(): JsCustomValidator = {
    logger.debug("Initializing JsCustomValidator", "init", "JsCustomValidator")
    validator.initialize(initCallback.callback)
    initCallback.future andThen {
      case Success(_) =>
        logger.debug(s"JsCustomValidator initialized", "JsCustomValidator", "init")
      case Failure(exception) =>
        logger.error(s"Custom validation failed: ${exception.getMessage}", "JsCustomValidator", "init")
    }
    this
  }

  override def validateWithProfile(profile: String, data: String): Future[ValidationResult] = {
    val uuid = UUID.randomUUID().toString
    if (!initCallback.future.isCompleted)
      logger.warning("Running custom validation on uninitialized validator",
                     "JsCustomValidator",
                     "validateWithProfile")
    val cb = new Callback2[ValidationResult]
    initCallback.future.flatMap(_ => {
      logger.debug(s"Starting custom validation $uuid", "JsCustomValidator", "validateWithProfile")
      validator.validate(profile, data, debug = false, cb.callback)
      cb.future andThen {
        case Success(r) =>
          logger.debug(s"Finished custom validation $uuid", "JsCustomValidator", "validateWithProfile")
          r
        case Failure(exception) =>
          logger.error(s"Custom validation failed: ${exception.getMessage}",
                       "JsCustomValidator",
                       "validateWithProfile")
      }
    })
  }
}

object JsCustomValidator {
  def apply(logger: Logger, validator: AmfWasmOpaValidator): JsCustomValidator = {
    new JsCustomValidator(logger, validator).init()
  }
}
