// $COVERAGE-OFF$
package org.mulesoft.language.client.js

import java.net.URI

import amf.core.lexer.CharSequenceStream
import amf.client.remote.Content
import amf.core.remote.server.{Http, Https}
import org.mulesoft.language.server.core.connections.IServerConnection
import org.mulesoft.language.server.core.platform.ConnectionBasedPlatform
import org.mulesoft.language.server.server.modules.editorManager.IEditorManagerModule

import scala.concurrent.{Future, Promise}
import scala.scalajs.js
import org.mulesoft.language.server.core.platform.PlatformDependentPart
import scala.scalajs.js.URIUtils
import amf.core.remote.server.Path

/**
  * Connection-based platform being able to fetch http resources
  * via standard JS tools
  */
object JSPlatformDependentPart extends PlatformDependentPart {

  def fetchHttp(url: String): Future[Content] = {
    val promise: Promise[Content] = Promise()

    if (url.startsWith("https:")) {
      Https.get(
        url,
        (response: js.Dynamic) => {
          var str = ""

          // CAREFUL!
          // this is required to avoid undefined behaviours
          val dataCb: js.Function1[Any, Any] = { (res: Any) =>
            str += res.toString
          }
          // Another chunk of data has been received, append it to `str`
          response.on("data", dataCb)

          val completedCb: js.Function = () => {
            val mediaType = try {
              Some(response.headers.asInstanceOf[js.Dictionary[String]]("content-type"))
            } catch {
              case e: Throwable => None
            }
            promise.success(Content(new CharSequenceStream(url, str), url, mediaType))
          }
          response.on("end", completedCb)
        }
      )

    } else {
      Http.get(
        url,
        (response: js.Dynamic) => {
          var str = ""

          val dataCb: js.Function1[Any, Any] = { (res: Any) =>
            str += res.toString
          }
          // Another chunk of data has been received, append it to `str`
          response.on("data", dataCb)

          val completedCb: js.Function = () => {
            val mediaType = try {
              Some(response.headers.asInstanceOf[js.Dictionary[String]]("content-type"))
            } catch {
              case e: Throwable => None
            }
            promise.success(Content(new CharSequenceStream(url, str), url, mediaType))
          }
          response.on("end", completedCb)
        }
      )
    }

    promise.future
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

  override def normalizePath(url: String): String = new URI(encodeURI(url)).normalize.toString

  override def findCharInCharSequence(stream: CharSequence)(p: Char => Boolean): Option[Char] = stream.toString.find(p)
  
  override def operativeSystem(): String = Globals.process.platform;
}
// $COVERAGE-ON$