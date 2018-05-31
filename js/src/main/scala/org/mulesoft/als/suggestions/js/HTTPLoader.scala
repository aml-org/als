package org.mulesoft.als.suggestions.js

import amf.client.remote.Content
import amf.core.lexer.CharSequenceStream
import amf.internal.resource.ResourceLoader
import amf.core.remote._
import org.scalajs.dom.ext.Ajax

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class HTTPLoader()
  extends ResourceLoader {

  /** Fetch specified resource and return associated content. Resource should have benn previously accepted. */
  def fetch(url: String): Future[Content] = {
    Ajax
      .get(url)
      .flatMap(xhr =>
        xhr.status match {
          case 200 => Future { Content(new CharSequenceStream(xhr.responseText), url) }
          case s   => Future.failed(FileNotFound(new Exception(s"Unhandled status code $s with ${xhr.statusText}")))
        })
  }

  /** Accepts specified resource. */
  def accepts(uri: String): Boolean = {

    uri match {
      case Http(protocol, host, path) => true
      case default => false
    }
  }
}
