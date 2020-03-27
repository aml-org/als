package org.mulesoft.als.suggestions.aml.webapi

import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.SecuredByCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.async.bindings.{
  AsyncApiBindingsCompletionPlugin,
  BindingsDiscreditableProperties
}
import org.mulesoft.als.suggestions.plugins.aml.webapi.async.{
  Async20EnumCompletionPlugin,
  Async20PayloadCompletionPlugin,
  Async20RuntimeExpressionsCompletionPlugin,
  Async20ShapeTypeFormatCompletionPlugin,
  Async20StructureCompletionPlugin,
  Async20TypeFacetsCompletionPlugin,
  _
}
import org.mulesoft.als.suggestions.plugins.aml.webapi.oas.OaslikeSecurityScopesCompletionPlugin
import org.mulesoft.als.suggestions.{AMLBaseCompletionPlugins, CompletionsPluginHandler}
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.AsyncApi20Dialect

object AsyncApiCompletionPluginRegistry {
  private val all: Seq[AMLCompletionPlugin] =
    AMLBaseCompletionPlugins.all :+
      SecuredByCompletionPlugin :+
      OaslikeSecurityScopesCompletionPlugin :+
      Async20RuntimeExpressionsCompletionPlugin :+
      OaslikeSecurityScopesCompletionPlugin :+
      Async20StructureCompletionPlugin :+
      Async20PayloadCompletionPlugin :+
      Async20TypeFacetsCompletionPlugin :+
      Async20MessageOneOfCompletionPlugin :+
      AsyncApiBindingsCompletionPlugin :+
      BindingsDiscreditableProperties :+
      AsyncApi20TefTag :+
      Async20TypeFacetsCompletionPlugin :+
      Async20ShapeTypeFormatCompletionPlugin :+
      Async20EnumCompletionPlugin :+
      AsyncMessageContentType

  def init(): Unit =
    CompletionsPluginHandler.registerPlugins(all, AsyncApi20Dialect().id)
}
