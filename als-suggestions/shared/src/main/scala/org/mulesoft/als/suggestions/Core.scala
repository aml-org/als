package org.mulesoft.als.suggestions

import org.mulesoft.als.suggestions.aml.webapi.{
  OasCompletionPluginRegistry,
  Raml08CompletionPluginRegistry,
  RamlCompletionPluginRegistry
}
import org.mulesoft.als.suggestions.implementation.SuggestionCategoryRegistry
import org.mulesoft.als.suggestions.interfaces.Syntax
import org.mulesoft.als.suggestions.interfaces.Syntax._
import org.mulesoft.als.suggestions.plugins.raml._
import org.mulesoft.als.suggestions.plugins.{
  BooleanPropertyCompletionPlugin,
  KnownKeyPropertyValuesCompletionPlugin,
  KnownPropertyValuesCompletionPlugin,
  StructureCompletionPlugin
}
import org.mulesoft.high.level.InitOptions

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps

object Core {
  def init(initOptions: InitOptions = InitOptions.AllProfiles): Future[Unit] = {
    org.mulesoft.high.level.Core
      .init(initOptions)
      .flatMap(_ => SuggestionCategoryRegistry.init())
      .map(_ => {
        CompletionPluginsRegistry.registerPlugin(StructureCompletionPlugin())
        CompletionPluginsRegistry.registerPlugin(KnownKeyPropertyValuesCompletionPlugin())
        CompletionPluginsRegistry.registerPlugin(KnownPropertyValuesCompletionPlugin())
        CompletionPluginsRegistry.registerPlugin(TypeReferencesCompletionPlugin())
        CompletionPluginsRegistry.registerPlugin(SecurityReferencesCompletionPlugin())
        CompletionPluginsRegistry.registerPlugin(BodyCompletionPlugin())
        CompletionPluginsRegistry.registerPlugin(IncludeCompletionPlugin())
        CompletionPluginsRegistry.registerPlugin(EmptyRamlFileCompletionPlugin())
        CompletionPluginsRegistry.registerPlugin(IncludeTagCompletionPlugin())
        CompletionPluginsRegistry.registerPlugin(BooleanPropertyCompletionPlugin())
        CompletionPluginsRegistry.registerPlugin(CommonHeadersNamesCompletionPlugin())
        CompletionPluginsRegistry.registerPlugin(BaseUriParametersCompletionPlugin())

        // **************** AML *************************
        // initialize aml plugins option?

        OasCompletionPluginRegistry.init()
        HeaderBaseCompletionPlugins.initAll() // TODO: inside OAS CPR?
        RamlCompletionPluginRegistry.init()
        Raml08CompletionPluginRegistry.init()
      })
  }

  def prepareText(text: String, offset: Int, syntax: Syntax): String =
    if (text.trim.startsWith("{"))
      ContentPatcher.prepareJsonContent(text, offset)
    else
      syntax match {
        case YAML => ContentPatcher.prepareYamlContent(text, offset)
        case _    => throw new Error(s"Syntax not supported: $syntax")
      }
}
