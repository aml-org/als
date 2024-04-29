package org.mulesoft.als.server.modules.diagnostic

import amf.aml.client.scala.model.document.DialectInstance
import amf.custom.validation.client.ProfileValidatorWebBuilder
import amf.custom.validation.client.scala.{BaseProfileValidatorBuilder, ProfileValidatorExecutor}

object PlatformTestProfileValidatorBuilder extends BaseProfileValidatorBuilder {
  override def validator(profile: DialectInstance): ProfileValidatorExecutor =
    ProfileValidatorWebBuilder.validator(profile)
}
