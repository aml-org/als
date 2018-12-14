package org.mulesoft.als.suggestions

import org.mulesoft.als.suggestions.implementation.SuggestionCategoryRegistry
import org.mulesoft.als.suggestions.interfaces.Syntax
import org.mulesoft.als.suggestions.interfaces.Syntax._
import org.mulesoft.als.suggestions.plugins.{BooleanPropertyCompletionPlugin, KnownKeyPropertyValuesCompletionPlugin, KnownPropertyValuesCompletionPlugin, StructureCompletionPlugin}
import org.mulesoft.als.suggestions.plugins.oas.{DefinitionReferenceCompletionPlugin, EmptyFileCompletionPlugin, ParameterReferencePlugin, ResponseReferencePlugin}
import org.mulesoft.als.suggestions.plugins.raml._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps

object Core {
    def init():Future[Unit] = org.mulesoft.high.level.Core.init()
        .flatMap(x=>SuggestionCategoryRegistry.init()).map(x => {

        CompletionPluginsRegistry.registerPlugin(StructureCompletionPlugin())
        CompletionPluginsRegistry.registerPlugin(KnownKeyPropertyValuesCompletionPlugin())
        CompletionPluginsRegistry.registerPlugin(KnownPropertyValuesCompletionPlugin())
        CompletionPluginsRegistry.registerPlugin(TemplateReferencesCompletionPlugin())
        CompletionPluginsRegistry.registerPlugin(ResponseReferencePlugin())
        CompletionPluginsRegistry.registerPlugin(ParameterReferencePlugin())
        CompletionPluginsRegistry.registerPlugin(DefinitionReferenceCompletionPlugin())
        CompletionPluginsRegistry.registerPlugin(MasterReferenceCompletionPlugin());
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
    });
    
    def prepareText(text:String, offset:Int, syntax:Syntax):String = {
        var isJSON = text.trim.startsWith("{")
        if(isJSON){
            CompletionProvider.prepareJsonContent(text,offset);
        }
        else {
            syntax match {
                case YAML => CompletionProvider.prepareYamlContent(text, offset);
                case _ => throw new Error(s"Syntax not supported: $syntax");
            }
        }
    }
}
