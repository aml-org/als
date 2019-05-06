package org.mulesoft.als.server.platform

import amf.client.remote.Content
import amf.core.lexer.CharSequenceStream
import amf.core.remote._
import amf.internal.environment.Environment
import amf.internal.resource.ResourceLoader
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.util.PathRefine
import org.mulesoft.common.io.FileSystem

import scala.collection.Map
import scala.concurrent.Future

class ProxyFileLoader(platform: ProxyContentPlatform) extends ResourceLoader {

  override def accepts(resource: String): Boolean = {
    !(resource.startsWith("http") || resource.startsWith("HTTP"))
  }

  override def fetch(resource: String): Future[Content] = {
    platform.fetchFile(resource)
  }
}

class ProxyContentPlatform(private val base: ServerPlatform,
                           private val logger: Logger,
                           private val map: Map[String, String])
    extends Platform { self =>

  def this(source: ServerPlatform, logger: Logger, overrideUrl: String, overrideContent: String) = {
    this(source, logger, Map(overrideUrl -> overrideContent))
  }

  override val fs: FileSystem = base.fs

  val fileLoader = new ProxyFileLoader(this)

  val loaders: Seq[ResourceLoader] = Seq(
    fileLoader
  )

  val defaultEnvironment = new Environment(this.loaders)

  override def resolvePath(uri: String): String = base.resolvePath(uri)

  def fetchFile(uri: String): Future[Content] = {
    val refinedUri = PathRefine.refinePath(uri, this)

    logger.debugDetail("Asked to fetch file " + refinedUri + " while override urls are " + map.keys.mkString(", "),
                       "ProxyContentPlatform",
                       "fetchFile")

    if (this.map.contains(uri) ||
        (uri.startsWith("file://") && this.map.contains(uri.substring("file://".length)))) {

      logger.debugDetail("Path found to be overriden " + refinedUri, "ProxyContentPlatform", "fetchFile")

      Future.successful(
        Content(new CharSequenceStream(uri, this.map(uri)),
                ensureFileAuthority(uri),
                extension(uri).flatMap(mimeFromExtension)))
    } else {
      base.fetchFile(uri)
    }
  }

  override def tmpdir(): String = base.tmpdir()

  /** encodes a complete uri. Not encodes chars like / */
  override def encodeURI(url: String): String = base.encodeURI(url)

  /** encodes a uri component, including chars like / and : */
  override def encodeURIComponent(url: String): String = base.encodeURIComponent(url)

  /** decode a complete uri. */
  override def decodeURI(url: String): String = base.decodeURI(url)

  /** decodes a uri component */
  override def decodeURIComponent(url: String): String = base.decodeURIComponent(url)

  override def normalizeURL(url: String): String = base.normalizeURL(url)

  override def normalizePath(url: String): String = base.normalizePath(url)

  override def findCharInCharSequence(stream: CharSequence)(p: Char => Boolean): Option[Char] =
    base.findCharInCharSequence(stream)(p)

  override def operativeSystem(): String = base.operativeSystem()
}
