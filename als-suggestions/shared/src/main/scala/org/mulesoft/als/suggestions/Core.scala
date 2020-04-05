package org.mulesoft.als.suggestions

import org.mulesoft.als.suggestions.aml.MetaDialectPluginRegistry
import org.mulesoft.als.suggestions.aml.webapi.{
  AsyncApiCompletionPluginRegistry,
  Oas20CompletionPluginRegistry,
  Oas30CompletionPluginRegistry,
  Raml08CompletionPluginRegistry,
  RamlCompletionPluginRegistry
}
import org.mulesoft.amfintegration.AmfInstance
import org.mulesoft.amfmanager.{DialectInitializer, InitOptions}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps

object Core {
  def init(initOptions: InitOptions = InitOptions.AllProfiles, amfInstance: AmfInstance): Future[Unit] = {
    DialectInitializer
      .init(initOptions, amfInstance)
      .map(_ => {
        // **************** AML *************************
        // initialize aml plugins option?
        Oas30CompletionPluginRegistry.init()
        Oas20CompletionPluginRegistry.init()
        HeaderBaseCompletionPlugins.initAll() // TODO: inside OAS CPR?
        RamlCompletionPluginRegistry.init()
        Raml08CompletionPluginRegistry.init()
        AsyncApiCompletionPluginRegistry.init()
        MetaDialectPluginRegistry.init()
      })
  }
}
