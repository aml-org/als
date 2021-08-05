package org.mulesoft.als.suggestions.client

import amf.core.model.document.BaseUnit
import amf.core.parser.{Position => AmfPosition}
import amf.core.registries.AMFPluginsRegistry
import amf.core.remote.{Aml, AsyncApi, AsyncApi20, Oas, Oas20, Oas30, Platform, Raml, Raml08, Raml10}
import amf.core.unsafe.PlatformSecrets
import amf.internal.environment.Environment
import amf.plugins.document.vocabularies.model.document.Dialect
import org.mulesoft.als.common.dtoTypes.{Position => DtoPosition}
import org.mulesoft.als.common.{DirectoryResolver, EnvironmentPatcher, PlatformDirectoryResolver}
import org.mulesoft.als.configuration.{AlsConfiguration, AlsConfigurationReader}
import org.mulesoft.als.suggestions._
import org.mulesoft.als.suggestions.aml.{
  AmlCompletionRequestBuilder,
  MetaDialectPluginRegistry,
  VocabularyDialectPluginRegistry
}
import org.mulesoft.als.suggestions.aml.webapi.{
  AsyncApiCompletionPluginRegistry,
  Oas20CompletionPluginRegistry,
  Oas30CompletionPluginRegistry,
  Raml08CompletionPluginRegistry,
  RamlCompletionPluginRegistry
}
import org.mulesoft.als.suggestions.interfaces.Syntax._
import org.mulesoft.als.suggestions.interfaces.{CompletionProvider, EmptyCompletionProvider, Syntax}
import org.mulesoft.als.suggestions.patcher.{ContentPatcher, PatchedContent}
import org.mulesoft.amfintegration.dialect.dialects.ExternalFragmentDialect
import org.mulesoft.amfintegration.{AmfInstance, AmfParseResult, InitOptions}
import org.mulesoft.lsp.feature.completion.CompletionItem

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class Suggestions(platform: Platform,
                  environment: Environment,
                  configuration: AlsConfigurationReader,
                  directoryResolver: DirectoryResolver,
                  amfInstance: AmfInstance)
    extends SuggestionsHelper {

  // header plugin static?
  val completionsPluginHandler = new CompletionsPluginHandler()

  def initialized(options: InitOptions = InitOptions.AllProfiles): this.type = {
    completionsPluginHandler.cleanIndex()
    HeaderBaseCompletionPlugins.initAll() // TODO: inside OAS CPR?
    if (options.contains(Oas30) || options.contains(Oas))
      Oas30CompletionPluginRegistry.init(amfInstance, completionsPluginHandler)
    if (options.contains(Oas20) || options.contains(Oas))
      Oas20CompletionPluginRegistry.init(amfInstance, completionsPluginHandler)
    if (options.contains(Raml10) || options.contains(Raml))
      RamlCompletionPluginRegistry.init(amfInstance, completionsPluginHandler)
    if (options.contains(Raml08) || options.contains(Raml))
      Raml08CompletionPluginRegistry.init(amfInstance, completionsPluginHandler)
    if (options.contains(AsyncApi20) || options.contains(AsyncApi))
      AsyncApiCompletionPluginRegistry.init(amfInstance, completionsPluginHandler)
    if (options.contains(Aml)) {
      MetaDialectPluginRegistry.init(amfInstance, completionsPluginHandler)
      VocabularyDialectPluginRegistry.init(amfInstance, completionsPluginHandler)
    }

    this
  }

  def suggest(url: String,
              position: Int,
              snippetsSupport: Boolean,
              rootLocation: Option[String]): Future[Seq[CompletionItem]] = {

    platform
      .fetchContent(url, amfInstance.amfConfiguration.withResourceLoaders(environment.loaders.toList))
      .map(content => {
        val originalContent = content.stream.toString
        val (patched, patchedEnv) =
          patchContentInEnvironment(environment, url, originalContent, position)
        (patched, patchedEnv)
      })
      .flatMap {
        case (patchedContent, patchedEnv) =>
          suggestWithPatchedEnvironment(url, patchedContent, position, patchedEnv, snippetsSupport, rootLocation)
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
                   amfInstance,
                   configuration)
        else
          RamlHeaderCompletionProvider
            .build(url, patchedContent.original, DtoPosition(position, patchedContent.original))
      case _ =>
        buildCompletionProviderAST(result.baseUnit,
                                   result.definedBy,
                                   DtoPosition(position, patchedContent.original),
                                   patchedContent,
                                   snippetSupport,
                                   rootLocation)
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
                                            environment: Environment,
                                            snippetsSupport: Boolean,
                                            rootLocation: Option[String]): Future[Seq[CompletionItem]] = {

    buildProviderAsync(amfInstance.modelBuilder().parse(url, environment, None),
                       position,
                       url,
                       patchedContent,
                       snippetsSupport,
                       rootLocation)
      .flatMap(_.suggest())
  }

  private def buildCompletionProviderAST(bu: BaseUnit,
                                         dialect: Dialect,
                                         pos: DtoPosition,
                                         patchedContent: PatchedContent,
                                         snippetSupport: Boolean,
                                         rootLocation: Option[String]): CompletionProviderAST = {

    val amfPosition: AmfPosition = pos.toAmfPosition
    CompletionProviderAST(
      AmlCompletionRequestBuilder
        .build(
          bu,
          amfPosition,
          dialect,
          environment,
          directoryResolver,
          platform,
          patchedContent,
          snippetSupport,
          rootLocation,
          configuration,
          completionsPluginHandler,
          amfInstance
        ))
  }
}

object Suggestions extends PlatformSecrets {
  def default =
    new Suggestions(platform,
                    Environment(),
                    AlsConfiguration(),
                    new PlatformDirectoryResolver(platform),
                    AmfInstance.default)
}

trait SuggestionsHelper {

  def amfParse(url: String, amfInstance: AmfInstance, environment: Environment): Future[BaseUnit] =
    amfInstance.modelBuilder().parse(url, environment, None).map(_.baseUnit)

  def getMediaType(originalContent: String): Syntax = {

    val trimmed = originalContent.trim
    if (trimmed.startsWith("{") || trimmed.startsWith("[")) Syntax.JSON
    else Syntax.YAML
  }

  def patchContentInEnvironment(environment: Environment,
                                fileUrl: String,
                                fileContentsStr: String,
                                position: Int): (PatchedContent, Environment) = {

    val patchedContent = ContentPatcher(fileContentsStr, position, YAML).prepareContent()
    val envWithOverride =
      EnvironmentPatcher.patch(environment, fileUrl, patchedContent.content)

    (patchedContent, envWithOverride)
  }
}
