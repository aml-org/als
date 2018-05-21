package org.mulesoft.language.server.js

import amf.core.lexer.CharSequenceStream
import amf.core.remote.{Content, FileNotFound}
import org.mulesoft.language.server.core.platform.HttpFetcher
import org.scalajs.dom.ext.Ajax

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


object WebJSHttpFetcher extends HttpFetcher {

  def fetchHttp(url: String): Future[Content] = {
    Ajax
      .get(url)
      .flatMap(xhr =>
        xhr.status match {
          case 200 => Future { Content(new CharSequenceStream(xhr.responseText), url) }
          case s   => Future.failed(FileNotFound(new Exception(s"Unhandled status code $s with ${xhr.statusText}")))
        })
  }
}
