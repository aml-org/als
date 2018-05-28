package org.mulesoft.language.server.core.platform

import amf.core.lexer.CharSequenceStream
import amf.core.remote._
import org.mulesoft.common.io.FileSystem
import org.mulesoft.language.server.core.connections.IServerConnection
import org.mulesoft.language.server.server.modules.editorManager.IEditorManagerModule

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
  * Platform based on connection.
  * Intended for subclassing to implement fetchHttp method
  */
abstract class ConnectionBasedPlatform (connection: IServerConnection,
                                        val editorManager: IEditorManagerModule)
  extends Platform {

  override val fs: FileSystem = new ConnectionBasedFS(connection, editorManager)

  override def resolvePath(uri: String): String = {

    val result = uri match {
      case File(path) =>
        if (path.startsWith("/")) {
          File.FILE_PROTOCOL + path
        } else {
          File.FILE_PROTOCOL + withTrailingSlash(path).substring(1)
        }

      case Http(protocol, host, path) => protocol + host + withTrailingSlash(path)

      case default => File.FILE_PROTOCOL + uri
    }

    connection.debugDetail(s"Resolved ${uri} as ${result}", "ConnectionBasedPlatform", "resolvePath")

    result
  }

  override protected def fetchFile(path: String): Future[Content] = {

    //val uri = if(path.startsWith(File.FILE_PROTOCOL)) path else File.FILE_PROTOCOL + path
    val uri = path

    val editorOption = this.editorManager.getEditor(uri)
    connection.debugDetail(s"Result of editor check for uri ${uri}: ${editorOption.isDefined}", "ConnectionBasedPlatform", "fetchFile")

    val contentFuture =
      if (editorOption.isDefined){

        Future.successful(editorOption.get.text)
      }
      else {
        this.connection.content(uri)
      }

    contentFuture
      .map(
        content => {

          Content(new CharSequenceStream(path, content),
            ensureFileAuthority(path),
            extension(path).flatMap(mimeFromExtension))
        })
  }

  override def tmpdir(): String = ???

  private def withTrailingSlash(path: String) = {
    (if (!path.startsWith("/")) "/" else "") + path
  }
}

