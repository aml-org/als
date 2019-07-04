package org.mulesoft.als.suggestions

import org.mulesoft.als.suggestions.implementation.SuggestionCategoryRegistry
import org.mulesoft.als.suggestions.interfaces.Syntax
import org.mulesoft.als.suggestions.interfaces.Syntax._
import org.mulesoft.als.suggestions.plugins.oas.{
  DefinitionReferenceCompletionPlugin,
  EmptyFileCompletionPlugin,
  ParameterReferencePlugin,
  ResponseReferencePlugin
}
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
  def init(initOptions: InitOptions = InitOptions.AllProfiles): Future[Unit] =
    org.mulesoft.high.level.Core
      .init(initOptions)
      .flatMap(_ => SuggestionCategoryRegistry.init())
      .map(_ => {
        CompletionPluginsRegistry.registerPlugin(StructureCompletionPlugin())
        CompletionPluginsRegistry.registerPlugin(KnownKeyPropertyValuesCompletionPlugin())
        CompletionPluginsRegistry.registerPlugin(KnownPropertyValuesCompletionPlugin())
        CompletionPluginsRegistry.registerPlugin(TemplateReferencesCompletionPlugin())
        CompletionPluginsRegistry.registerPlugin(ResponseReferencePlugin())
        CompletionPluginsRegistry.registerPlugin(ParameterReferencePlugin())
        CompletionPluginsRegistry.registerPlugin(DefinitionReferenceCompletionPlugin())
        CompletionPluginsRegistry.registerPlugin(MasterReferenceCompletionPlugin())
        CompletionPluginsRegistry.registerPlugin(TypeReferencesCompletionPlugin())
        CompletionPluginsRegistry.registerPlugin(SecurityReferencesCompletionPlugin())
        CompletionPluginsRegistry.registerPlugin(AnnotationReferencesCompletionPlugin())
        CompletionPluginsRegistry.registerPlugin(FacetsCompletionPlugin())
        CompletionPluginsRegistry.registerPlugin(BodyCompletionPlugin())
        CompletionPluginsRegistry.registerPlugin(UsesCompletionPlugin())
        CompletionPluginsRegistry.registerPlugin(IncludeCompletionPlugin())
        CompletionPluginsRegistry.registerPlugin(ExampleCompletionPlugin())
        CompletionPluginsRegistry.registerPlugin(EmptyRamlFileCompletionPlugin())
        CompletionPluginsRegistry.registerPlugin(EmptyFileCompletionPlugin())
        CompletionPluginsRegistry.registerPlugin(IncludeTagCompletionPlugin())
        CompletionPluginsRegistry.registerPlugin(BooleanPropertyCompletionPlugin())
        CompletionPluginsRegistry.registerPlugin(CommonHeadersNamesCompletionPlugin())
        CompletionPluginsRegistry.registerPlugin(ExampleStructureCompletionPlugin())
        CompletionPluginsRegistry.registerPlugin(BaseUriParametersCompletionPlugin())

        // **************** AML *************************
        // initialize aml plugins option?

        AMLBaseCompletionPlugins.initAll()
      })

  def prepareText(text: String, offset: Int, syntax: Syntax): String =
    if (text.trim.startsWith("{")) {
      CompletionProviderWebApi.prepareJsonContent(text, offset)
    } else {
      syntax match {
        case YAML => CompletionProviderWebApi.prepareYamlContent(text, offset);
        case _    => throw new Error(s"Syntax not supported: $syntax");
      }
    }
}
