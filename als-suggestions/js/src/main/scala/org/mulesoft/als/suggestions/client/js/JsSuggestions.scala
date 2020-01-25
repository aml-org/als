package org.mulesoft.als.suggestions.client.js

import amf.client.resource.ClientResourceLoader
import amf.core.unsafe.PlatformSecrets
import amf.internal.environment.Environment
import org.mulesoft.als.client.configuration.{
  ClientDirectoryResolver,
  DirectoryResolverAdapter,
  EmptyJsDirectoryResolver,
  ResourceLoaderConverter
}
import org.mulesoft.als.client.convert.LspConvertersSharedToClient._
import org.mulesoft.als.client.lsp.feature.completion.ClientCompletionItem
import org.mulesoft.als.suggestions.client.Suggestions
import org.mulesoft.amfmanager.InitOptions
import org.mulesoft.lsp.server.AmfInstance

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

@JSExportTopLevel("Suggestions")
object JsSuggestions extends PlatformSecrets {

  @JSExport
  def init(options: InitOptions = InitOptions.WebApiProfiles): js.Promise[Unit] =
    Suggestions.default.init(options).toJSPromise

  @JSExport
  def suggest(language: String,
              url: String,
              position: Int,
              loaders: js.Array[ClientResourceLoader] = js.Array(),
              dirResolver: ClientDirectoryResolver = EmptyJsDirectoryResolver,
              snippetSupport: Boolean = true): js.Promise[js.Array[ClientCompletionItem]] = {

    val environment = Environment(loaders.map(ResourceLoaderConverter.internalResourceLoader).toSeq)

    new Suggestions(platform, environment, DirectoryResolverAdapter.convert(dirResolver), AmfInstance(environment))
      .suggest(url, position, snippetSupport)
      .map(_.map(_.toClient).toJSArray)
      .toJSPromise
  }
}
