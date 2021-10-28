package org.mulesoft.als.suggestions.aml.webapi

import amf.aml.client.scala.model.document.Dialect
import org.mulesoft.als.suggestions.AMLBaseCompletionPlugins
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.SecuredByCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.async.bindings.{
  AsyncApiBindingsCompletionPlugin,
  BindingsDiscreditableProperties
}
import org.mulesoft.als.suggestions.plugins.aml.webapi.async.structure._
import org.mulesoft.als.suggestions.plugins.aml.webapi.async._
import org.mulesoft.als.suggestions.plugins.aml.webapi.extensions.OasLikeSemanticExtensionsFlavour
import org.mulesoft.als.suggestions.plugins.aml.webapi.oas.OaslikeSecurityScopesCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.oas.structure.ResolveInfo
import org.mulesoft.als.suggestions.plugins.aml.webapi.raml.RamlTypeDeclarationReferenceCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.{ResolveDefault, StructureCompletionPlugin}
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
          AsyncApiVariableValueException,
          Async2HeadersSchema,
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
      Async20RequiredObjectCompletionPlugin :+
      Async2SecuritySchemeType :+
      Async2VariableValueParam :+
      Async2SecuredByCompletionPlugin :+
      Async2MessageExamplesCompletionPlugin :+
      RamlTypeDeclarationReferenceCompletionPlugin :+
      Async2ExamplesPlugin :+
      OasLikeSemanticExtensionsFlavour

  override def plugins: Seq[AMLCompletionPlugin] = all

  override def dialect: Dialect = AsyncApi20Dialect()
}
