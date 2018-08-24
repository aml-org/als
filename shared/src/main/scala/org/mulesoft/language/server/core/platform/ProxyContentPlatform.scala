package org.mulesoft.language.server.core.platform

import amf.client.remote.Content
import amf.core.lexer.CharSequenceStream
import amf.core.remote._
import amf.core.lexer.CharSequenceStream
import amf.core.remote._
import org.mulesoft.common.io.FileSystem
import org.mulesoft.language.server.core.connections.IServerConnection
import org.mulesoft.language.server.server.modules.editorManager.IEditorManagerModule

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import amf.client.remote.Content
import amf.internal.environment.Environment
import amf.internal.resource.ResourceLoader
import org.mulesoft.language.server.common.utils.PathRefine

class ProxyFileLoader(platform: ProxyContentPlatform) extends ResourceLoader {

    override def accepts(resource: String): Boolean = {
      !(resource.startsWith("http") || resource.startsWith("HTTP"))
    }

    override def fetch(resource: String): Future[Content] = {
      platform.fetchFile(resource)
    }
}

class ProxyContentPlatform(protected val source: ConnectionBasedPlatform,
                           val overrideUrl: String,
                           val overrideContent: String)
  extends Platform {  self =>

  override val fs: FileSystem = source.fs

  val fileLoader = new ProxyFileLoader(this)

  val httpLoader = source.httpLoader

  val loaders: Seq[ResourceLoader] = Seq(
    fileLoader,
    httpLoader
  )

  val defaultEnvironment = new Environment(this.loaders)

  override def resolvePath(uri: String): String = source.resolvePath(uri)

  def fetchFile(_path: String): Future[Content] = {
    var path = _path
    path = PathRefine.refinePath(path,this)

    source.connection.debugDetail("Asked to fetch file " + path + " while override url is " + overrideUrl,
      "ProxyContentPlatform", "fetchFile")

    if (path == this.overrideUrl ||
      (path.startsWith("file://") && this.overrideUrl == path.substring("file://".length))) {

      source.connection.debugDetail("Path found to be overriden " + path,
        "ProxyContentPlatform", "fetchFile")

      Future.successful(Content(new CharSequenceStream(path, this.overrideContent),
        ensureFileAuthority(path),
        extension(path).flatMap(mimeFromExtension)))
    } else {
      source.fetchFile(path)
    }
  }

  def fetchHttp(url: String): Future[Content] = source.fetchHttp(url)

  override def tmpdir(): String = source.tmpdir()

  /** encodes a complete uri. Not encodes chars like / */
  override def encodeURI(url: String): String = source.encodeURI(url)

  /** encodes a uri component, including chars like / and : */
  override def encodeURIComponent(url: String): String = source.encodeURIComponent(url)

  /** decode a complete uri. */
  override def decodeURI(url: String): String = source.decodeURI(url)

  /** decodes a uri component */
  override def decodeURIComponent(url: String): String = source.decodeURIComponent(url)

  override def normalizeURL(url: String): String = source.normalizeURL(url)

  override def normalizePath(url: String): String = source.normalizePath(url)

  override def findCharInCharSequence(stream: CharSequence)(p: Char => Boolean): Option[Char] =
    source.findCharInCharSequence(stream)(p)

  override def operativeSystem(): String = source.operativeSystem()
}
