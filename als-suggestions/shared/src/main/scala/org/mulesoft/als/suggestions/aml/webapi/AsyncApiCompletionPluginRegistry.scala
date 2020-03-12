package org.mulesoft.als.suggestions.aml.webapi

import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.SecuredByCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.async.AsyncAPIRuntimeExpressionsCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.oas.OaslikeSecurityScopesCompletionPlugin
import org.mulesoft.als.suggestions.{AMLBaseCompletionPlugins, CompletionsPluginHandler}
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.AsyncApi20Dialect

object AsyncApiCompletionPluginRegistry {
  private val all: Seq[AMLCompletionPlugin] =
    AMLBaseCompletionPlugins.all :+
      SecuredByCompletionPlugin :+
      OaslikeSecurityScopesCompletionPlugin :+
      AsyncAPIRuntimeExpressionsCompletionPlugin

  def init(): Unit =
    CompletionsPluginHandler.registerPlugins(all, AsyncApi20Dialect().id)
}
