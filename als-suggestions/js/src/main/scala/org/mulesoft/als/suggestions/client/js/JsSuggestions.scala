package org.mulesoft.als.suggestions.client.js

import amf.client.remote.Content
import amf.client.resource.ClientResourceLoader
import amf.internal.environment.Environment
import org.mulesoft.als.suggestions.client.{AlsPlatform, AlsPlatformWrapper, Suggestion, Suggestions}
import org.mulesoft.high.level.InitOptions

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

@JSExportTopLevel("Suggestions")
object JsSuggestions {

  @JSExport
  def init(options: InitOptions = InitOptions.WebApiProfiles): js.Promise[Unit] =
    Suggestions.init(options).toJSPromise

  def internalResourceLoader(loader: ClientResourceLoader): amf.internal.resource.ResourceLoader = new amf.internal.resource.ResourceLoader {
    override def fetch(resource: String): Future[Content] = loader.fetch(resource).toFuture

    override def accepts(resource: String): Boolean = loader.accepts(resource)
  }

  @JSExport
  def suggest(language: String,
              url: String,
              position: Int,
              loaders: js.Array[ClientResourceLoader] = js.Array()): js.Promise[js.Array[Suggestion]] = {
    val environment = new Environment(loaders.map(internalResourceLoader).toSeq)
    val alsPlatform: AlsPlatform = new AlsPlatformWrapper(environment)

    Suggestions.suggest(language, url, position, environment, alsPlatform)
      .map(_.toJSArray)
      .toJSPromise
  }
}
