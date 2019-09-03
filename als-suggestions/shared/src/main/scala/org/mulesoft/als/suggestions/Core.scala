package org.mulesoft.als.suggestions

import org.mulesoft.als.suggestions.aml.webapi.{
  OasCompletionPluginRegistry,
  Raml08CompletionPluginRegistry,
  RamlCompletionPluginRegistry
}
import org.mulesoft.als.suggestions.interfaces.Syntax
import org.mulesoft.als.suggestions.interfaces.Syntax._
import org.mulesoft.amfmanager.{DialectInitializer, InitOptions}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps

object Core {
  def init(initOptions: InitOptions = InitOptions.AllProfiles): Future[Unit] = {
    DialectInitializer
      .init(initOptions)
      .map(_ => {
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
