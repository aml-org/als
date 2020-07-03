package org.mulesoft.als.suggestions.aml.webapi

import amf.core.remote.{AsyncApi20, Vendor}
import amf.plugins.document.vocabularies.model.document.Dialect
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.SecuredByCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.async.bindings.{
  AsyncApiBindingsCompletionPlugin,
  BindingsDiscreditableProperties
}
import org.mulesoft.als.suggestions.plugins.aml.webapi.async.structure._
import org.mulesoft.als.suggestions.plugins.aml.webapi.async.{
  Async20EnumCompletionPlugin,
  Async20PayloadCompletionPlugin,
  Async20RuntimeExpressionsCompletionPlugin,
  Async20ShapeTypeFormatCompletionPlugin,
  Async20TypeFacetsCompletionPlugin,
  _
}
import org.mulesoft.als.suggestions.plugins.aml.webapi.oas.OaslikeSecurityScopesCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.oas.structure.ResolveInfo
import org.mulesoft.als.suggestions.plugins.aml.{ResolveDefault, StructureCompletionPlugin}
import org.mulesoft.als.suggestions.{AMLBaseCompletionPlugins, CompletionsPluginHandler}
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.AsyncApi20Dialect

object AsyncApiCompletionPluginRegistry extends WebApiCompletionPluginRegistry {
  private val all: Seq[AMLCompletionPlugin] =
    AMLBaseCompletionPlugins.all :+
      SecuredByCompletionPlugin :+
      OaslikeSecurityScopesCompletionPlugin :+
      Async20RuntimeExpressionsCompletionPlugin :+
      OaslikeSecurityScopesCompletionPlugin :+
      StructureCompletionPlugin(
        List(
          ResolveServers,
          ResolveShapeInPayload,
          ResolveResponses,
          ResolveTraits,
          ResolveInfo,
          ResolveDefault
        )) :+
      Async20PayloadCompletionPlugin :+
      Async20TypeFacetsCompletionPlugin :+
      Async20MessageOneOfCompletionPlugin :+
      AsyncApiBindingsCompletionPlugin :+
      BindingsDiscreditableProperties :+
      AsyncApi20RefTag :+
      Async20TypeFacetsCompletionPlugin :+
      Async20ShapeTypeFormatCompletionPlugin :+
      Async20EnumCompletionPlugin :+
      AsyncMessageContentType :+
      Async20RequiredObjectCompletionPlugin

  override def plugins: Seq[AMLCompletionPlugin] = all

  override def dialect: Dialect = AsyncApi20Dialect()
}
