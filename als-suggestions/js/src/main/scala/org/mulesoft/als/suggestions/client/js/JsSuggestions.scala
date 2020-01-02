package org.mulesoft.als.suggestions.client.js

import amf.client.remote.Content
import amf.client.resource.ClientResourceLoader
import amf.core.unsafe.PlatformSecrets
import amf.internal.environment.Environment
import org.mulesoft.als.common.{DirectoryResolver => InternalResolver}
import org.mulesoft.als.suggestions.client.Suggestions
import org.mulesoft.amfmanager.InitOptions
import org.mulesoft.als.client.convert.LspConvertersSharedToClient._
import org.mulesoft.als.client.lsp.feature.completion.ClientCompletionItem

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel, ScalaJSDefined}

@JSExportTopLevel("Suggestions")
object JsSuggestions extends PlatformSecrets with EmptyDirectoryResolver {

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
              dirResolver: ClientDirectoryResolver = emptyDirectoryResolver,
              snippetSupport: Boolean = true): js.Promise[js.Array[ClientCompletionItem]] = {

    val environment = Environment(loaders.map(internalResourceLoader).toSeq)

    Suggestions
      .suggest(language,
               url,
               position,
               DirectoryResolverAdapter.convert(dirResolver),
               environment,
               platform,
               snippetSupport)
      .map(_.map(_.toClient).toJSArray)
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

      override def exists(path: String): Future[Boolean] =
        clientResolver.exists(toPath(path)).toFuture

      override def readDir(path: String): Future[Seq[String]] =
        clientResolver.readDir(toPath(path)).toFuture.map(_.toSeq)

      override def isDirectory(path: String): Future[Boolean] =
        clientResolver.isDirectory(toPath(path)).toFuture

    }
  }
}
