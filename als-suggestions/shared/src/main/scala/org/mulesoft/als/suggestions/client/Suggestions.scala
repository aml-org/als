package org.mulesoft.als.suggestions.client

import amf.aml.client.scala.model.document.Dialect
import amf.core.client.common.position.{Position => AmfPosition}
import amf.core.client.scala.model.document.BaseUnit
import amf.core.internal.unsafe.PlatformSecrets
import org.mulesoft.als.common.dtoTypes.{Position => DtoPosition}
import org.mulesoft.als.common.{AmfConfigurationPatcher, DirectoryResolver, PlatformDirectoryResolver}
import org.mulesoft.als.configuration.{AlsConfiguration, AlsConfigurationReader}
import org.mulesoft.als.suggestions._
import org.mulesoft.als.suggestions.aml.webapi._
import org.mulesoft.als.suggestions.aml.{
  AmlCompletionRequestBuilder,
  MetaDialectPluginRegistry,
  VocabularyDialectPluginRegistry
}
import org.mulesoft.als.suggestions.interfaces.Syntax._
import org.mulesoft.als.suggestions.interfaces.{CompletionProvider, Syntax}
import org.mulesoft.als.suggestions.patcher.{ContentPatcher, PatchedContent}
import org.mulesoft.amfintegration.amfconfiguration.{AmfConfigurationWrapper, AmfParseResult}
import org.mulesoft.amfintegration.dialect.dialects.ExternalFragmentDialect
import org.mulesoft.lsp.feature.completion.CompletionItem

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class Suggestions(configuration: AlsConfigurationReader, directoryResolver: DirectoryResolver)
    extends SuggestionsHelper {

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
              rootLocation: Option[String],
              amfConfiguration: AmfConfigurationWrapper): Future[Seq[CompletionItem]] = {

    amfConfiguration
      .fetchContent(url)
      .map(content => {
        val originalContent = content.stream.toString
        val (patched, patchedConf) =
          patchContentInEnvironment(url, originalContent, position, amfConfiguration)
        (patched, patchedConf)
      })
      .flatMap {
        case (patchedContent, patchedConf) =>
          suggestWithPatchedEnvironment(url, patchedContent, position, patchedConf, snippetsSupport, rootLocation)
      }
  }

  def buildProvider(result: AmfParseResult,
                    position: Int,
                    url: String,
                    patchedContent: PatchedContent,
                    snippetSupport: Boolean,
                    rootLocation: Option[String]): CompletionProvider = {
    result.definedBy match {
      case ExternalFragmentDialect.dialect if isHeader(position, patchedContent.original) =>
        if (!url.toLowerCase().endsWith(".raml"))
          HeaderCompletionProviderBuilder
            .build(url,
                   patchedContent.original,
                   DtoPosition(position, patchedContent.original),
                   result.amfConfiguration,
                   configuration)
        else
          RamlHeaderCompletionProvider
            .build(url, patchedContent.original, DtoPosition(position, patchedContent.original))
      case _ =>
        buildCompletionProviderAST(
          result.result.baseUnit,
          result.definedBy,
          DtoPosition(position, patchedContent.original),
          patchedContent,
          snippetSupport,
          rootLocation,
          result.amfConfiguration
        )
    }
  }

  def buildProviderAsync(unitFuture: Future[AmfParseResult],
                         position: Int,
                         url: String,
                         patchedContent: PatchedContent,
                         snippetSupport: Boolean,
                         rootLocation: Option[String]): Future[CompletionProvider] = {
    unitFuture
      .map(buildProvider(_, position, url, patchedContent, snippetSupport, rootLocation))
  }

  private def isHeader(position: Int, originalContent: String): Boolean =
    !originalContent
      .substring(0, position)
      .replaceAll("^\\{?\\s+", "")
      .contains('\n')

  private def suggestWithPatchedEnvironment(url: String,
                                            patchedContent: PatchedContent,
                                            position: Int,
                                            patchedConf: AmfConfigurationWrapper,
                                            snippetsSupport: Boolean,
                                            rootLocation: Option[String]): Future[Seq[CompletionItem]] = {

    buildProviderAsync(patchedConf.parse(url), position, url, patchedContent, snippetsSupport, rootLocation)
      .flatMap(_.suggest())
  }

  private def buildCompletionProviderAST(bu: BaseUnit,
                                         dialect: Dialect,
                                         pos: DtoPosition,
                                         patchedContent: PatchedContent,
                                         snippetSupport: Boolean,
                                         rootLocation: Option[String],
                                         amfConfiguration: AmfConfigurationWrapper): CompletionProviderAST = {

    val amfPosition: AmfPosition = pos.toAmfPosition
    CompletionProviderAST(
      AmlCompletionRequestBuilder
        .build(
          bu,
          amfPosition,
          dialect,
          directoryResolver,
          patchedContent,
          snippetSupport,
          rootLocation,
          configuration,
          completionsPluginHandler,
          amfConfiguration
        ))
  }
}

// is it ok to use PlatformSecrets here?
object Suggestions extends PlatformSecrets {
  def default: Suggestions = new Suggestions(AlsConfiguration(), new PlatformDirectoryResolver(platform))
}

trait SuggestionsHelper {

  def amfParse(url: String, amfConfiguration: AmfConfigurationWrapper): Future[BaseUnit] =
    amfConfiguration.parse(url).map(_.result.baseUnit)

  def getMediaType(originalContent: String): Syntax = {

    val trimmed = originalContent.trim
    if (trimmed.startsWith("{") || trimmed.startsWith("[")) Syntax.JSON
    else Syntax.YAML
  }

  def patchContentInEnvironment(
      fileUrl: String,
      fileContentsStr: String,
      position: Int,
      amfConfiguration: AmfConfigurationWrapper): (PatchedContent, AmfConfigurationWrapper) = {

    val patchedContent = ContentPatcher(fileContentsStr, position, YAML).prepareContent()
    val patchedConf =
      AmfConfigurationPatcher.patch(amfConfiguration.branch, fileUrl, patchedContent.content)

    (patchedContent, patchedConf)
  }
}
