package org.mulesoft.als.suggestions.aml.webapi

import amf.aml.client.scala.model.document.Dialect
import org.mulesoft.als.suggestions.AMLBaseCompletionPlugins
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.async._
import org.mulesoft.als.suggestions.plugins.aml.webapi.async.bindings.{
  AsyncApiBindingsCompletionPlugin,
  BindingsDiscreditableProperties
}
import org.mulesoft.als.suggestions.plugins.aml.webapi.async.structure._
import org.mulesoft.als.suggestions.plugins.aml.webapi.oas.OaslikeSecurityScopesCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.oas.structure.ResolveInfo
import org.mulesoft.als.suggestions.plugins.aml.webapi.raml.RamlTypeDeclarationReferenceCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.{
  Async2AMLJsonSchemaStyleDeclarationReferences,
  ResolveDefault,
  StructureCompletionPlugin
}
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.AsyncApi20Dialect
import org.mulesoft.amfintegration.dialect.dialects.asyncapi26.AsyncApi26Dialect

trait AsyncApiCompletionPluginRegistry extends WebApiCompletionPluginRegistry {
  private lazy val all: Seq[AMLCompletionPlugin] =
    AMLBaseCompletionPlugins.all :+
      Async2AMLJsonSchemaStyleDeclarationReferences :+
      Async20SecuredByCompletionPlugin :+
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
          AsyncApiVariableValueException,
          Async2HeadersSchema,
          ResolveDefault
        )
      ) :+
      Async20PayloadCompletionPlugin :+
      Async20TypeFacetsCompletionPlugin :+
      Async20MessageOneOfCompletionPlugin :+
      AsyncApiBindingsCompletionPlugin :+
      BindingsDiscreditableProperties :+
      AsyncApi20RefTag :+
      Async20TypeFacetsCompletionPlugin :+
      Async20ShapeTypeFormatCompletionPlugin :+
      AsyncMessageContentType :+
      Async20RequiredObjectCompletionPlugin :+
      Async2SecuritySchemeType :+
      Async2VariableValueParam :+
      Async2SecuredByCompletionPlugin :+
      Async2MessageExamplesCompletionPlugin :+
      RamlTypeDeclarationReferenceCompletionPlugin :+
      Async2ExamplesPlugin

  override def plugins: Seq[AMLCompletionPlugin] = all
}
object AsyncApi2CompletionPluginRegistry extends AsyncApiCompletionPluginRegistry {
  override def dialect: Dialect = AsyncApi20Dialect()
  override def plugins: Seq[AMLCompletionPlugin] = super.plugins ++ Seq(
    Async20EnumCompletionPlugin
  )
}
object AsyncApi26CompletionPluginRegistry extends AsyncApiCompletionPluginRegistry {
  override def plugins: Seq[AMLCompletionPlugin] = super.plugins ++ Seq(
    Async26ChannelServersPlugin,
    Async26EnumCompletionPlugin
  )

  override def dialect: Dialect = AsyncApi26Dialect()
}
