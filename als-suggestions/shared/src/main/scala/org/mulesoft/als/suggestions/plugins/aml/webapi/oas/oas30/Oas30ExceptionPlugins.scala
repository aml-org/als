package org.mulesoft.als.suggestions.plugins.aml.webapi.oas.oas30

import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.plugins.aml.webapi.ExceptionPlugin

object Oas30ExceptionPlugins {

  def applyAny(request: AmlCompletionRequest): Boolean = exceptions.exists(_.applies(request))

  private val exceptions: Seq[ExceptionPlugin] =
    Seq(VariableValueParam, DiscriminatorObject, DiscriminatorMappingValue)
}
