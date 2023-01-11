package org.mulesoft.als.server.modules.diagnostic

import amf.custom.validation.client.scala.BaseProfileValidatorBuilder

trait PlatformTestCustomValidator {
  def builder: BaseProfileValidatorBuilder = PlatformTestProfileValidatorBuilder
}
