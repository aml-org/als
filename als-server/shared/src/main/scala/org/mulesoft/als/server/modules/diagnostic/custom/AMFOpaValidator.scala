package org.mulesoft.als.server.modules.diagnostic.custom

import org.mulesoft.als.logger.Logger

import scala.concurrent.Future

trait AMFOpaValidator {
  type ValidationResult = String
  val logger: Logger
  def validateWithProfile(profile: String, data: String): Future[ValidationResult]
}

trait AMFOpaValidatorBuilder {
  def build(logger: Logger): AMFOpaValidator
}
