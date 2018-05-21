package org.mulesoft.language.client.js

import amf.core.lexer.CharSequenceStream
import amf.core.remote.Content
import amf.core.remote.server.{Http, Https}
import org.mulesoft.language.server.core.connections.IServerConnection
import org.mulesoft.language.server.core.platform.ConnectionBasedPlatform
import org.mulesoft.language.server.server.modules.editorManager.IEditorManagerModule

import scala.concurrent.{Future, Promise}
import scala.scalajs.js
import org.mulesoft.language.server.core.platform.HttpFetcher

/**
  * Connection-based platform being able to fetch http resources
  * via standard JS tools
  */
object JSHttpFetcher extends HttpFetcher {

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
}
