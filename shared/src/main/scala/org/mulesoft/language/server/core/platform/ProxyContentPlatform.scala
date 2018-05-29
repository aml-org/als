package org.mulesoft.language.server.core.platform

import amf.core.remote.Content
import amf.core.lexer.CharSequenceStream
import amf.core.remote._

import amf.core.lexer.CharSequenceStream
import amf.core.remote._
import org.mulesoft.common.io.FileSystem
import org.mulesoft.language.server.core.connections.IServerConnection
import org.mulesoft.language.server.server.modules.editorManager.IEditorManagerModule

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class ProxyContentPlatform(protected val source: ConnectionBasedPlatform,
                           protected val overrideUrl: String,
                           protected val overrideContent: String) extends Platform {

  override val fs: FileSystem = source.fs

  override def resolvePath(uri: String): String = {
    source.resolvePath(uri)
  }

  override protected def fetchFile(path: String): Future[Content] = {

    source.connection.debugDetail("Asked to fetch file " + path,
      "ProxyContentPlatform", "fetchFile")

    if (path == this.overrideUrl) {
      source.connection.debugDetail("Path found to be overriden " + path,
        "ProxyContentPlatform", "fetchFile")

      Future.successful(Content(new CharSequenceStream(path, this.overrideContent),
        ensureFileAuthority(path),
        extension(path).flatMap(mimeFromExtension)))
    } else {
      source.fetchFile(path)
    }
  }

  override protected def fetchHttp(url: String): Future[Content] = {
    source.fetchHttp(url)
  }

  override def tmpdir(): String = {
    source.tmpdir()
  }
}
