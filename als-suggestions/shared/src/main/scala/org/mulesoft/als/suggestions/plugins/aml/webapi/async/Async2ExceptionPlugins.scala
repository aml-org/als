package org.mulesoft.als.suggestions.plugins.aml.webapi.async

import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.plugins.aml.webapi.ExceptionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.async.bindings.{
  AsyncApi20BindingsCompletionPlugin,
  AsyncApi26BindingsCompletionPlugin
}

object Async2ExceptionPlugins {

  def applyAny(request: AmlCompletionRequest): Boolean = exceptions.exists(_.applies(request))

  private val exceptions: Seq[ExceptionPlugin] =
    Seq(AsyncApi20BindingsCompletionPlugin, AsyncApi26BindingsCompletionPlugin, Async2VariableValueParam)
}
