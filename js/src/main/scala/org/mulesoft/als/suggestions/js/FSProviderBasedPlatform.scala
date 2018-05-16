package org.mulesoft.als.suggestions.js

import amf.core.lexer.CharSequenceStream

import amf.core.remote._
import amf.client.remote._
import org.mulesoft.common.io.FileSystem
import org.scalajs.dom.ext.Ajax

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

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

/**
  * Platform based on external fs provider
  */
class FSProviderBasedPlatform (fsProvider: IFSProvider) extends Platform {

  override val fs: FileSystem = new FSProviderBasedFS(fsProvider)

  override def resolvePath(uri: String): String = {

    uri match {
      case File(path) =>
        if (path.startsWith("/")) {
          File.FILE_PROTOCOL + path
        } else {
          File.FILE_PROTOCOL + withTrailingSlash(path).substring(1)
        }

      case Http(protocol, host, path) => protocol + host + withTrailingSlash(path)
    }
  }

  override protected def fetchFile(path: String): Future[Content] = {

    val uri = if(path.startsWith(File.FILE_PROTOCOL)) path else File.FILE_PROTOCOL + path


    this.fsProvider.contentAsync(uri).toFuture
      .map(
        content => {

          Content(new CharSequenceStream(path, content),
            ensureFileAuthority(path),
            extension(path).flatMap(mimeFromExtension))
        })
  }

  override protected def fetchHttp(url: String): Future[Content] = {

    Ajax
      .get(url)
      .flatMap(xhr =>
        xhr.status match {
          case 200 => Future { Content(new CharSequenceStream(xhr.responseText), url) }
          case s   => Future.failed(FileNotFound(new Exception(s"Unhandled status code $s with ${xhr.statusText}")))
        })
  }

  override def tmpdir(): String = ???

  private def withTrailingSlash(path: String) = {
    (if (!path.startsWith("/")) "/" else "") + path
  }
}
