package org.mulesoft.als.server.platform

import amf.client.remote.Content
import amf.core.lexer.CharSequenceStream
import amf.internal.environment.Environment
import amf.internal.resource.ResourceLoader
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.textsync.TextDocumentManager
import org.mulesoft.als.server.util.PathRefine
import org.mulesoft.high.level.implementation.{AlsPlatform, AlsPlatformWrapper}
import org.mulesoft.high.level.interfaces.DirectoryResolver

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Http {
  def unapply(uri: String): Option[(String, String, String)] = uri match {
    case url if url.startsWith("http://") || url.startsWith("https://") =>
      val protocol = url.substring(0, url.indexOf("://") + 3)
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

class DefaultFileLoader(platform: ServerPlatform) extends ResourceLoader {

  override def accepts(resource: String): Boolean = {
    !(resource.startsWith("http") || resource.startsWith("HTTP"))
  }

  override def fetch(resource: String): Future[Content] = {
    platform.fetchFile(resource)
  }
}

/**
  * Platform based on connection.
  * Intended for subclassing to implement fetchHttp method
  */
class ServerPlatform(val logger: Logger,
                     val textDocumentManager: TextDocumentManager,
                     directoryResolver: Option[DirectoryResolver] = None,
                     defaultEnvironment: Environment = Environment())
  extends AlsPlatformWrapper(defaultEnvironment, dirResolver = directoryResolver) { self =>

  val fileLoader = new DefaultFileLoader(this)

  override val loaders: Seq[ResourceLoader] = Seq(fileLoader)

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

      case _ => File.FILE_PROTOCOL + uri
    }

    logger.debugDetail(s"Resolved $uri as $result", "ConnectionBasedPlatform", "resolvePath")

    result
  }

  def fetchFile(_path: String): Future[Content] = {
    var path = _path

    this.logger.debugDetail("Asked to fetch file " + path, "ConnectionBasedPlatform", "fetchFile")

    path = PathRefine.refinePath(path, this)
    this.logger.debugDetail("Refined path is " + path, "ConnectionBasedPlatform", "fetchFile")

    val uri = path

    val editorOption = textDocumentManager.getTextDocument(uri)
    logger.debugDetail(s"Result of editor check for uri $uri: ${editorOption.isDefined}",
      "ConnectionBasedPlatform",
      "fetchFile")

    val contentFuture =
      if (editorOption.isDefined) {

        Future.successful(editorOption.get.text)
      } else {
        fs.asyncFile(uri).read()
      }

    contentFuture
      .map(content => {

        Content(new CharSequenceStream(path, content),
          ensureFileAuthority(path),
          extension(path).flatMap(mimeFromExtension))
      })
  }


  private def withTrailingSlash(path: String) = {
    (if (!path.startsWith("/")) "/" else "") + path
  }

  override def withDefaultEnvironment(defaultEnvironment: Environment): AlsPlatform =
    new ServerPlatform(logger, textDocumentManager, directoryResolver, defaultEnvironment)
}
