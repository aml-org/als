package org.mulesoft.language.server.core.platform

import amf.client.remote.Content
import amf.core.lexer.CharSequenceStream
import amf.core.remote._
import amf.internal.environment.Environment
import amf.internal.resource.ResourceLoader
import org.mulesoft.common.io.FileSystem
import org.mulesoft.high.level.implementation.AlsPlatform
import org.mulesoft.language.server.common.utils.PathRefine
import org.mulesoft.language.server.core.connections.IServerConnection
import org.mulesoft.language.server.modules.editorManager.IEditorManagerModule

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Http {
  def unapply(uri: String): Option[(String, String, String)] = uri match {
    case url if url.startsWith("http://") || url.startsWith("https://") =>
      val protocol        = url.substring(0, url.indexOf("://") + 3)
      val rightOfProtocol = url.stripPrefix(protocol)
      val host =
        if (rightOfProtocol.contains("/")) rightOfProtocol.substring(0, rightOfProtocol.indexOf("/"))
        else rightOfProtocol
      val path = rightOfProtocol.replace(host, "")
      Some(protocol, host, path)
    case _ => None
  }
}

object File {
  val FILE_PROTOCOL = "file://"

  def unapply(url: String): Option[String] = {
    url match {
      case s if s.startsWith(FILE_PROTOCOL) =>
        val path = s.stripPrefix(FILE_PROTOCOL)
        Some(path)
      case _ => None
    }
  }
}

class DefaultFileLoader(platform: ConnectionBasedPlatform) extends ResourceLoader {

  override def accepts(resource: String): Boolean = {
    !(resource.startsWith("http") || resource.startsWith("HTTP"))
  }

  override def fetch(resource: String): Future[Content] = {
    platform.fetchFile(resource)
  }
}

class DefaultHttpLoader(platform: ConnectionBasedPlatform) extends ResourceLoader {

  override def accepts(resource: String): Boolean = {
    resource.startsWith("http") || resource.startsWith("HTTP")
  }

  override def fetch(resource: String): Future[Content] = {
    platform.fetchHttp(resource)
  }
}

/**
  * Platform based on connection.
  * Intended for subclassing to implement fetchHttp method
  */
class ConnectionBasedPlatform(val connection: IServerConnection,
                              val editorManager: IEditorManagerModule,
                              val platformPart: PlatformDependentPart,
                              override val defaultEnvironment: Environment = Environment())
    extends AlsPlatform(defaultEnvironment) { self =>

  override val fs: FileSystem = new ConnectionBasedFS(connection, editorManager)

  val fileLoader = new DefaultFileLoader(this)

  val httpLoader = new DefaultHttpLoader(this)

  val loaders: Seq[ResourceLoader] = Seq(
    fileLoader,
    httpLoader
  )

  override def resolvePath(_uri: String): String = {

    var uri = _uri
    uri = PathRefine.refinePath(uri, this)
    val result = uri match {
      case File(path) =>
        val isWindows = operativeSystem().toLowerCase().indexOf("win") >= 0
        if (path.startsWith("/")) {
          File.FILE_PROTOCOL + path
        } else if (isWindows) {
          File.FILE_PROTOCOL + withTrailingSlash(path)
        } else {
          File.FILE_PROTOCOL + path
        }

      case Http(protocol, host, path) => protocol + host + withTrailingSlash(path)

      case default => File.FILE_PROTOCOL + uri
    }

    connection.debugDetail(s"Resolved $uri as $result", "ConnectionBasedPlatform", "resolvePath")

    result
  }

  def fetchFile(_path: String): Future[Content] = {
    var path = _path

    this.connection.debugDetail("Asked to fetch file " + path, "ConnectionBasedPlatform", "fetchFile")

    path = PathRefine.refinePath(path, this)
    this.connection.debugDetail("Refined path is " + path, "ConnectionBasedPlatform", "fetchFile")

    //val uri = if(path.startsWith(File.FILE_PROTOCOL)) path else File.FILE_PROTOCOL + path
    val uri = path

    val editorOption = this.editorManager.getEditor(uri)
    connection.debugDetail(s"Result of editor check for uri ${uri}: ${editorOption.isDefined}",
                           "ConnectionBasedPlatform",
                           "fetchFile")

    val contentFuture =
      if (editorOption.isDefined) {

        Future.successful(editorOption.get.text)
      } else {
        this.connection.content(uri)
      }

    contentFuture
      .map(content => {

        Content(new CharSequenceStream(path, content),
                ensureFileAuthority(path),
                extension(path).flatMap(mimeFromExtension))
      })
  }

  // $COVERAGE-OFF$
  def fetchHttp(url: String): Future[Content] = platformPart.fetchHttp(url)

  override def tmpdir(): String = ???

  private def withTrailingSlash(path: String) = {
    (if (!path.startsWith("/")) "/" else "") + path
  }

  /** encodes a complete uri. Not encodes chars like / */
  override def encodeURI(url: String): String = platformPart.encodeURI(url)

  /** encodes a uri component, including chars like / and : */
  override def encodeURIComponent(url: String): String = platformPart.encodeURIComponent(url)

  /** decode a complete uri. */
  override def decodeURI(url: String): String = platformPart.decodeURI(url)

  /** decodes a uri component */
  override def decodeURIComponent(url: String): String = platformPart.decodeURIComponent(url)

  override def normalizeURL(url: String): String = platformPart.normalizeURL(url)

  override def normalizePath(url: String): String = platformPart.normalizePath(url)

  override def findCharInCharSequence(stream: CharSequence)(p: Char => Boolean): Option[Char] =
    platformPart.findCharInCharSequence(stream)(p)

  // $COVERAGE-ON$
  override def operativeSystem(): String = platformPart.operativeSystem()

  override def withDefaultEnvironment(defaultEnvironment: Environment): AlsPlatform =
    new ConnectionBasedPlatform(connection, editorManager, platformPart, defaultEnvironment)
}
