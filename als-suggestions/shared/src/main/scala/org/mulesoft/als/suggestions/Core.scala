package org.mulesoft.als.suggestions

import org.mulesoft.als.suggestions.aml.webapi.{
  Oas20CompletionPluginRegistry,
  Oas30CompletionPluginRegistry,
  Raml08CompletionPluginRegistry,
  RamlCompletionPluginRegistry
}
import org.mulesoft.amfmanager.{DialectInitializer, InitOptions}
import org.mulesoft.lsp.server.AmfEnvHandler

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps

object Core {
  def init(initOptions: InitOptions = InitOptions.AllProfiles, amfEnvHandler: AmfEnvHandler): Future[Unit] = {
    DialectInitializer
      .init(initOptions, amfEnvHandler)
      .map(_ => {
        // **************** AML *************************
        // initialize aml plugins option?
        Oas30CompletionPluginRegistry.init()
        Oas20CompletionPluginRegistry.init()
        HeaderBaseCompletionPlugins.initAll() // TODO: inside OAS CPR?
        RamlCompletionPluginRegistry.init()
        Raml08CompletionPluginRegistry.init()
      })
  }
}
