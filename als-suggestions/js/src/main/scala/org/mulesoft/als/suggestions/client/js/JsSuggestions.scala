package org.mulesoft.als.suggestions.client.js

import amf.client.remote.Content
import amf.client.resource.ClientResourceLoader
import amf.internal.environment.Environment
import org.mulesoft.als.suggestions.client.{Suggestion, Suggestions}
import org.mulesoft.high.level.InitOptions
import org.mulesoft.high.level.interfaces.{DirectoryResolver => InternalResolver}
import org.mulesoft.high.level.implementation.{AlsPlatform, AlsPlatformWrapper}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.Promise
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel, ScalaJSDefined}

@JSExportTopLevel("Suggestions")
object JsSuggestions {

  @JSExport
  def init(options: InitOptions = InitOptions.WebApiProfiles): js.Promise[Unit] =
    Suggestions.init(options).toJSPromise

  def internalResourceLoader(loader: ClientResourceLoader): amf.internal.resource.ResourceLoader =
    new amf.internal.resource.ResourceLoader {
      override def fetch(resource: String): Future[Content] =
        loader.fetch(resource).toFuture

      override def accepts(resource: String): Boolean = loader.accepts(resource)
    }

  @JSExport
  def suggest(language: String,
              url: String,
              position: Int,
              loaders: js.Array[ClientResourceLoader] = js.Array(),
              dirResolver: js.UndefOr[ClientDirectoryResolver] = js.undefined): js.Promise[js.Array[Suggestion]] = {
    val environment = new Environment(loaders.map(internalResourceLoader).toSeq)
    val alsPlatform: AlsPlatform =
      new AlsPlatformWrapper(environment, dirResolver.toOption.map(DirectoryResolverAdapter.convert))

    Suggestions
      .suggest(language, url, position, environment, alsPlatform)
      .map(_.toJSArray)
      .toJSPromise
  }
}

@ScalaJSDefined
trait ClientDirectoryResolver extends js.Object {

  def exists(path: String): js.Promise[Boolean]

  def readDir(path: String): js.Promise[js.Array[String]]

  def isDirectory(path: String): js.Promise[Boolean]
}

object DirectoryResolverAdapter {
  def convert(clientResolver: ClientDirectoryResolver): InternalResolver = {
    new InternalResolver {

      override def exists(path: String): Future[Boolean] = clientResolver.exists(path).toFuture

      override def readDir(path: String): Future[Seq[String]] = clientResolver.readDir(path).toFuture.map(_.toSeq)

      override def isDirectory(path: String): Future[Boolean] = clientResolver.isDirectory(path).toFuture

    }
  }
}
