package org.mulesoft.als.suggestions.client

import amf.aml.client.scala.model.document.Dialect
import amf.core.client.common.position.{Position => AmfPosition}
import amf.core.client.scala.model.document.BaseUnit
import org.mulesoft.als.common.DirectoryResolver
import org.mulesoft.als.common.dtoTypes.{Position => DtoPosition}
import org.mulesoft.als.configuration.AlsConfigurationReader
import org.mulesoft.als.suggestions._
import org.mulesoft.als.suggestions.aml.webapi._
import org.mulesoft.als.suggestions.aml.{
  AmlCompletionRequestBuilder,
  MetaDialectPluginRegistry,
  VocabularyDialectPluginRegistry
}
import org.mulesoft.als.suggestions.interfaces.CompletionProvider
import org.mulesoft.amfintegration.amfconfiguration.{ALSConfigurationState, AmfParseContext}
import org.mulesoft.amfintegration.dialect.dialects.ExternalFragmentDialect
import org.mulesoft.lsp.feature.completion.CompletionItem

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class Suggestions(configuration: AlsConfigurationReader,
                  directoryResolver: DirectoryResolver,
                  cuProvider: String => Future[UnitBundle]) {

  // header plugin static?
  val completionsPluginHandler = new CompletionsPluginHandler()

  def initialized(): this.type = {
    completionsPluginHandler.cleanIndex()
    HeaderBaseCompletionPlugins.initAll()
    Oas30CompletionPluginRegistry.init(completionsPluginHandler)
    Oas20CompletionPluginRegistry.init(completionsPluginHandler)
    RamlCompletionPluginRegistry.init(completionsPluginHandler)
    Raml08CompletionPluginRegistry.init(completionsPluginHandler)
    AsyncApiCompletionPluginRegistry.init(completionsPluginHandler)
    MetaDialectPluginRegistry.init(completionsPluginHandler)
    VocabularyDialectPluginRegistry.init(completionsPluginHandler)
    this
  }

  def suggest(url: String,
              position: Int,
              snippetsSupport: Boolean,
              rootLocation: Option[String]): Future[Seq[CompletionItem]] =
    for {
      bundle      <- cuProvider(url)
      suggestions <- buildProvider(bundle, position, url, snippetsSupport, rootLocation).suggest()
    } yield {
      suggestions
    }

  def buildProvider(result: UnitBundle,
                    position: Int,
                    url: String,
                    snippetSupport: Boolean,
                    rootLocation: Option[String]): CompletionProvider = {
    result.definedBy match {
      case ExternalFragmentDialect.dialect if isHeader(position, result.unit.raw.getOrElse("")) =>
        if (!url.toLowerCase().endsWith(".raml"))
          HeaderCompletionProviderBuilder
            .build(url,
                   result.unit.raw.getOrElse(""),
                   DtoPosition(position, result.unit.raw.getOrElse("")),
                   result.context,
                   configuration)
        else
          RamlHeaderCompletionProvider
            .build(url, result.unit.raw.getOrElse(""), DtoPosition(position, result.unit.raw.getOrElse("")))
      case _ =>
        buildCompletionProviderAST(
          result.unit,
          result.definedBy,
          DtoPosition(position, result.unit.raw.getOrElse("")),
          snippetSupport,
          rootLocation,
          result.context.state
        )
    }
  }

  private def isHeader(position: Int, originalContent: String): Boolean =
    !originalContent
      .substring(0, position)
      .replaceAll("^\\{?\\s+", "")
      .contains('\n')

  private def buildCompletionProviderAST(bu: BaseUnit,
                                         dialect: Dialect,
                                         pos: DtoPosition,
                                         snippetSupport: Boolean,
                                         rootLocation: Option[String],
                                         alsConfiguration: ALSConfigurationState): CompletionProviderAST = {

    val amfPosition: AmfPosition = pos.toAmfPosition
    CompletionProviderAST(
      AmlCompletionRequestBuilder
        .build(
          bu,
          amfPosition,
          dialect,
          directoryResolver,
          snippetSupport,
          rootLocation,
          configuration,
          completionsPluginHandler,
          alsConfiguration
        ))
  }
}

case class UnitBundle(unit: BaseUnit, definedBy: Dialect, context: AmfParseContext)
