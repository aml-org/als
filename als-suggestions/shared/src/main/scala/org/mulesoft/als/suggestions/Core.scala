package org.mulesoft.als.suggestions

import amf.core.remote.{Aml, AsyncApi, AsyncApi20, Oas, Oas20, Oas30, Raml, Raml08, Raml10, Vendor}
import org.mulesoft.als.suggestions.aml.MetaDialectPluginRegistry
import org.mulesoft.als.suggestions.aml.webapi.{
  AsyncApiCompletionPluginRegistry,
  Oas20CompletionPluginRegistry,
  Oas30CompletionPluginRegistry,
  Raml08CompletionPluginRegistry,
  RamlCompletionPluginRegistry,
  WebApiCompletionPluginRegistry
}
import org.mulesoft.amfintegration.{AmfInstance, InitOptions}

import scala.language.postfixOps

object Core {

  def init(initOptions: InitOptions = InitOptions.AllProfiles, amfInstance: AmfInstance): Unit = {
    // **************** AML *************************
    // initialize aml plugins option?
    // another map??

  }
}
