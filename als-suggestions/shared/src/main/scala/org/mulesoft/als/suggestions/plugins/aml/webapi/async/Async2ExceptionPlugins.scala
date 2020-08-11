package org.mulesoft.als.suggestions.plugins.aml.webapi.async

import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.plugins.aml.webapi.ExceptionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.async.bindings.AsyncApiBindingsCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.oas.VariableValueParam

object Async2ExceptionPlugins {

  def applyAny(request: AmlCompletionRequest): Boolean = exceptions.exists(_.applies(request))

  private val exceptions: Seq[ExceptionPlugin] =
    Seq(AsyncApiBindingsCompletionPlugin, Async2VariableValueParam)
}
