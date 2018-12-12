package org.mulesoft.language.server.js

import java.net.URI

import amf.core.lexer.CharSequenceStream
import amf.core.remote.server.Path
import amf.core.remote.{Content, FileNotFound}
import org.mulesoft.language.server.core.platform.PlatformDependentPart
import org.scalajs.dom.ext.Ajax

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js.URIUtils


object WebJSHttpFetcher extends PlatformDependentPart {

  def fetchHttp(url: String): Future[Content] = {
    Ajax
      .get(url)
      .flatMap(xhr =>
        xhr.status match {
          case 200 => Future { Content(new CharSequenceStream(xhr.responseText), url) }
          case s   => Future.failed(FileNotFound(new Exception(s"Unhandled status code $s with ${xhr.statusText}")))
        })
  }

  /** encodes a complete uri. Not encodes chars like / */
  override def encodeURI(url: String): String = URIUtils.encodeURI(url)

  /** encodes a uri component, including chars like / and : */
  override def encodeURIComponent(url: String): String = URIUtils.encodeURIComponent(url)

  /** decode a complete uri. */
  override def decodeURI(url: String): String = URIUtils.decodeURI(url)

  /** decodes a uri component */
  override def decodeURIComponent(url: String): String = URIUtils.decodeURIComponent(url)

  override def normalizeURL(url: String): String = Path.resolve(url)
}
